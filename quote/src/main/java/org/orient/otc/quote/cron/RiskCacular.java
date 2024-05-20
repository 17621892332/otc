package org.orient.otc.quote.cron;

import cn.hutool.core.date.StopWatch;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.market.vo.MarketInfoVO;
import org.orient.otc.api.quote.dto.risk.RiskMarkDto;
import org.orient.otc.api.quote.dto.risk.TradeRiskCacularResult;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.api.user.dto.ExchangeAccountQueryDto;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.jni.dto.*;
import org.orient.otc.common.jni.util.JniUtil;
import org.orient.otc.common.jni.vo.*;
import org.orient.otc.quote.dto.risk.ExchangeRealTimePos;
import org.orient.otc.quote.dto.volatility.LinearInterpVolSurfaceDto;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import org.orient.otc.quote.entity.TradeRiskInfo;
import org.orient.otc.quote.enums.ExchangeEodType;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.service.RiskMarkService;
import org.orient.otc.quote.service.TradeMngService;
import org.orient.otc.quote.service.TradeRiskInfoService;
import org.orient.otc.quote.service.VolatilityService;
import org.orient.otc.quote.util.QuoteUtil;
import org.orient.otc.quote.util.VolatilityUtil;
import org.orient.otc.quote.vo.quote.QuoteSoResultVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.orient.otc.quote.util.QuoteUtil.getCallOrPut;

/**
 * 风险轮询计算
 */
@Slf4j
@Component
@Data
public class RiskCacular {

    @Resource
    private JniUtil jniUtil;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UnderlyingManagerClient underlyingManagerClient;

    @Resource
    private ExchangeAccountClient exchangeAccountClient;

    @Resource
    private TradeMngService tradeMngService;

    @Resource
    private VolatilityService volatilityService;

    @Resource
    private TradeRiskInfoService tradeRiskInfoService;

    @Resource
    private MarketClient marketClient;

    @Resource
    private RiskMarkService riskMarkService;
    /**
     * 是否执行风险计算
     */
    @Value("${isOpenCacuRisk: false}")
    private Boolean isOpenCacuRisk;

    @Resource
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor asyncTaskExecutor;


    /**
     * 当前交易时间
     */
    private LocalDate tradeDay;

    /**
     * 上一个交易日
     */
    private LocalDate lastTradeDay;

    /**
     * 公共股息率
     */
    private BigDecimal dividendYield;

    /**
     * 无风险利率
     */
    private BigDecimal riskFreeInterestRate;

    private Integer mcNumberPaths;
    private Integer pdeTimeGrid;
    private Integer pdeSpotGrid;

    /**
     * 模拟路径数
     */
    private Integer pathNumber;
    /**
     * 线程数
     */
    private Integer threadNumber;

    /**
     * 自定义计算时间
     */
    private Long riskTime;


    /**
     * 今日开仓金额
     */
    private Map<String, BigDecimal> todayOpenTradeAmountMap;

    /**
     * 今日平仓金额
     */
    private Map<String, BigDecimal> todayCloseTradeAmountMap;

    /**
     * 昨日风险情况
     */
    private Map<String, TradeRiskCacularResult> lastRiskInfoMap = new HashMap<>();

    private final static ScheduledExecutorService pool = Executors.newScheduledThreadPool(6);

    /**
     * 启动项目执行
     */
    @PostConstruct
    public void init() {
        if (!isOpenCacuRisk) {
            return;
        }
        pool.scheduleWithFixedDelay(() -> {
            try {
                this.calculate();
            } catch (Exception e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                log.error("计算报错:", e);
            }
        }, 0, 1, TimeUnit.MICROSECONDS);
    }

    /**
     * 计算风险
     */
    private void calculate() {
        //开始计算
        StopWatch stopWatch = new StopWatch();
        //初始化基础数据
        stopWatch.start("初始化基础数据");
        initBasicData();

        stopWatch.stop();
        stopWatch.start("获取计算需要的数据");
        //获取场外需要计算的数据
        UnderlyingByRisk underlyingByRisk = setNeedRiskData();
        if (underlyingByRisk.getTradeMngVOList()==null){
            return;
        }
        stopWatch.stop();

        stopWatch.start("场外计算");
        Map<String, String> resultMap = new HashMap<>();
        List<TradeMngVO> snowballList = underlyingByRisk.getTradeMngVOList().stream().filter(
                vo -> vo.getOptionType() == OptionTypeEnum.AISnowBallCallPricer
                        || vo.getOptionType() == OptionTypeEnum.AISnowBallPutPricer
                        || vo.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer
                        || vo.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer
                        || vo.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer
                        || vo.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer
                        || vo.getOptionType() == OptionTypeEnum.AICallKOAccPricer
                        || vo.getOptionType() == OptionTypeEnum.AIPutKOAccPricer
                        || vo.getOptionType() == OptionTypeEnum.AICallFixKOAccPricer
                        || vo.getOptionType() == OptionTypeEnum.AIPutFixKOAccPricer
        ).collect(Collectors.toList());
        //计算雪球类型的风险
        if (!snowballList.isEmpty()) {
            RLock lock = redissonClient.getLock("lock:overCalculateRisk" + underlyingByRisk.getUnderlyingCode());
            lock.lock();
            try {
                long start = System.currentTimeMillis();
                underlyingByRisk.getTradeMngVOList().removeAll(snowballList);

                List<CompletableFuture<TradeRiskCacularResult>> futureAll = new ArrayList<>();
                snowballList.forEach(tradeMngVo -> {
                    //场外计算
                    futureAll.add(CompletableFuture.supplyAsync(() -> {
                        log.debug("线程：CompletableFuture:{}", Thread.currentThread().getName());
                        long startOne = System.currentTimeMillis();
                        TradeRiskCacularResult result = overCacularRisk(tradeMngVo, underlyingByRisk);
                        long endOne = System.currentTimeMillis();
                        log.debug("{}异步耗时:{}", endOne - startOne, tradeMngVo.getOptionType() + ":" + tradeMngVo.getTradeCode());
                        return result;
                    }, asyncTaskExecutor));

                });
                //等待全部执行完成
                CompletableFuture.allOf(futureAll.toArray(new CompletableFuture[0])).join();
                //获取内容
                for (CompletableFuture<TradeRiskCacularResult> future : futureAll) {
                    TradeRiskCacularResult result = future.get();
                    resultMap.put(result.getTradeCode(), JSONObject.toJSONString(result));
                }
                long end = System.currentTimeMillis();
                log.debug("{},{}异步总耗时:{}", Thread.currentThread().getName(), underlyingByRisk.getUnderlyingCode(), end - start);
            } catch (Exception e) {
                log.error("计算失败:{}", underlyingByRisk.getUnderlyingCode());
            } finally {
                lock.unlock();
            }
        }
        //普通场外期权
        underlyingByRisk.getTradeMngVOList().forEach(tradeMngVo -> {
            //场外计算
            resultMap.put(tradeMngVo.getTradeCode(), JSONObject.toJSONString(overCacularRisk(tradeMngVo, underlyingByRisk)));
        });
        stopWatch.stop();
        stopWatch.start("场内计算");
        underlyingByRisk.getExchangeRealTimePosList().forEach(exchangeRealTimePos -> {
            //场内计算
            String redisTradeKey = exchangeRealTimePos.getInvestorID() + "_" + exchangeRealTimePos.getInstrumentID() + "_"
                    + ("2".equals(exchangeRealTimePos.getPosiDirection()) ? ExchangeEodType.LONG.name() : ExchangeEodType.SHORT.name());
            resultMap.put(redisTradeKey, JSONObject.toJSONString(exchangeCacularRisk(exchangeRealTimePos, underlyingByRisk)));

        });
        stopWatch.stop();
        stopWatch.start("存储计算结果");
        stringRedisTemplate.opsForHash().putAll(RedisAdapter.TRADE_RISK_RESULT, resultMap);
        stopWatch.stop();
        log.debug("合约代码:{},计算情况:{}", underlyingByRisk.getUnderlyingCode(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));

    }

    /**
     * 初始化系统配置
     */
    public void initSystemConfig() {
        //获取系统配置信息
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisAdapter.SYSTEM_CONFIG_INFO);
        Map<String, String> systemInfoMap = entries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
        //股息率
        dividendYield = BigDecimalUtil.percentageToBigDecimal(new BigDecimal(systemInfoMap.get(SystemConfigEnum.dividendYield.name())));
        //无风险利率
        riskFreeInterestRate = BigDecimalUtil.percentageToBigDecimal(new BigDecimal(systemInfoMap.get(SystemConfigEnum.riskFreeInterestRate.name())));
        //系统交易日
        tradeDay = LocalDate.parse(systemInfoMap.get(SystemConfigEnum.tradeDay.name()));
        lastTradeDay = LocalDate.parse(systemInfoMap.get(SystemConfigEnum.lastTradeDay.name()));
        mcNumberPaths = Integer.valueOf(systemInfoMap.get(SystemConfigEnum.mcNumberPaths.name()));
        pdeTimeGrid = Integer.valueOf(systemInfoMap.get(SystemConfigEnum.pdeTimeGrid.name()));
        pdeSpotGrid = Integer.valueOf(systemInfoMap.get(SystemConfigEnum.pdeSpotGrid.name()));
        threadNumber = Integer.valueOf(systemInfoMap.get(SystemConfigEnum.threadNumber.name()));
        pathNumber = Integer.valueOf(systemInfoMap.get(SystemConfigEnum.pathNumber.name()));

    }

    /**
     * 初始化基础数据
     */
    private void initBasicData() {
        initSystemConfig();
        String tradeDayStr = tradeDay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //从redis中获取合约自定义行情
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisAdapter.RISK_TIME))) {
            riskTime = Long.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(RedisAdapter.RISK_TIME)));
        }
//        //风险最近一次的快照数据
//        List<Object> values = stringRedisTemplate.opsForHash().values(RedisAdapter.TRADE_LAST_RISK_INFO);
//        List<TradeRiskCacularResult> totalPnlList = JSONArray.parseArray(values.toString(), TradeRiskCacularResult.class);
//        lastTotalPnlMap = totalPnlList.stream().collect(Collectors.toMap(TradeRiskCacularResult::getId
//                , TradeRiskCacularResult::getTotalProfitLoss, (v1, v2) -> v2));
        //初始化开仓金额
        Map<Object, Object> todayOpenTradeAmountEntries = stringRedisTemplate.opsForHash().entries(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT + tradeDayStr);
        todayOpenTradeAmountMap = todayOpenTradeAmountEntries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> new BigDecimal(e.getValue().toString())));
        //初始化平仓金额
        Map<Object, Object> todayCloseTradeAmountEntries = stringRedisTemplate.opsForHash().entries(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT + tradeDayStr);
        todayCloseTradeAmountMap = todayCloseTradeAmountEntries.entrySet().stream().collect(
                Collectors.toMap(e -> String.valueOf(e.getKey()), e -> new BigDecimal(e.getValue().toString())));

    }

    /**
     * 获取需要计算的合约信息
     * @param underlyingCode 合约代码
     * @return 合约信息
     */
    public UnderlyingByRisk getUnderlyingByRisk(String underlyingCode) {
        UnderlyingManagerVO underlying = underlyingManagerClient.getUnderlyingByCode(underlyingCode);
        //查询合约股息率
        BigDecimal underlyingDividendYield = dividendYield;
        if (Objects.nonNull(underlying) && Objects.nonNull(underlying.getDividendYield())) {
            underlyingDividendYield = BigDecimalUtil.percentageToBigDecimal(underlying.getDividendYield());
        }
        UnderlyingByRisk underlyingByRisk = new UnderlyingByRisk();
        underlyingByRisk.setUnderlyingCode(underlyingCode);
        underlyingByRisk.setExchangeUnderlyingCode(underlying.getExchangeUnderlyingCode());
        underlyingByRisk.setUnderlyingDividendYield(underlyingDividendYield);
        underlyingByRisk.setContractSize(underlying.getContractSize());
        underlyingByRisk.setUnderlyingName(underlying.getUnderlyingName());
        underlyingByRisk.setVarietyId(underlying.getVarietyId());
        underlyingByRisk.setVarietyCode(underlying.getVarietyCode());
        return underlyingByRisk;
    }

    /**
     * 获取需要计算的数据
     */
    private UnderlyingByRisk setNeedRiskData() {

        //获取有效的交易记录放到TradeMngVOList中
        StopWatch stopWatch = new StopWatch("setNeedRiskData");
        Long size = stringRedisTemplate.opsForSet().size(RedisAdapter.RISK_UNDERLYING_SET);
        stopWatch.start("获取Set内容"+size);
        //如果没有从数据库中加载到redis中
        if (size != null && size <= 0) {
            RLock lock = redissonClient.getLock("lock:getRiskDataFromDb");
            boolean tryLock = false;
            try {
                tryLock = lock.tryLock(0, 10, TimeUnit.SECONDS);
                if (tryLock) {
                    log.info("开始获取风险计算数据");
                    getDataFromDb();
                }else {
                    log.info("等待新的一批数据中......");
                    return  new UnderlyingByRisk();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (tryLock && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("成功获取到数据，并释放锁");
                }

            }
        }
        //从redis中取underlyingCode记录计算
        String underlyingCode = stringRedisTemplate.opsForSet().pop(RedisAdapter.RISK_UNDERLYING_SET);
        stopWatch.stop();

            if (StringUtils.isNotBlank(underlyingCode)) {
                stopWatch.start("获取" + underlyingCode + "合约信息");
                UnderlyingByRisk underlyingByRisk = getUnderlyingByRisk(underlyingCode);
                stopWatch.stop();
                //获取实时行情
                stopWatch.start("获取实时行情");
                BigDecimal riskMarkCode = riskMarkService.getRiskMark(RiskMarkDto.builder().underlyingCode(underlyingCode.toUpperCase()).build());
                if (Objects.nonNull(riskMarkCode) && !riskMarkCode.equals(BigDecimal.ZERO)) {
                    underlyingByRisk.setLastPrice(riskMarkCode);
                } else {
                    MarketInfoVO marketInfoVO= marketClient.getLastMarketDataByCode(underlyingCode);
                    underlyingByRisk.setLastPrice(marketInfoVO.getLastPrice());
                }
                stopWatch.stop();
                stopWatch.start("设置波动率");
                //设置波动率
                underlyingByRisk.setMidVolatility(volatilityService.getNewVolatilityByType(underlyingCode, VolTypeEnum.mid));
                underlyingByRisk.setEvaluationTime(getEvaluationTime());
                stopWatch.stop();
                stopWatch.start("设置lastTradeDay交易数据");
                //从Redis获取对应合约上一次的交易数据
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisAdapter.TRADE_LAST_RISK_INFO + tradeDay))){
                    Object values = stringRedisTemplate.opsForHash().get(RedisAdapter.TRADE_LAST_RISK_INFO + tradeDay, underlyingCode);
                    if (Objects.nonNull(values)) {
                        List<TradeRiskCacularResult> lastRiskInfoList = JSONArray.parseArray(values.toString(), TradeRiskCacularResult.class);
                        lastRiskInfoMap = lastRiskInfoList.stream().collect(Collectors.toMap(TradeRiskCacularResult::getId, Function.identity(), (v1, v2) -> v2));
                    }
                }else {
                    //从数据库中获取PNL数据
                    List<TradeRiskInfo> lastRiskInfoList = tradeRiskInfoService.getTradeTotalPnl(tradeDay,Boolean.TRUE, Boolean.FALSE);
                    if (lastRiskInfoList != null) {
                        //按照合约代码进行分组
                        Map<String, List<TradeRiskInfo>> lastRiskDbMap = lastRiskInfoList.stream().collect(Collectors.groupingBy(TradeRiskInfo::getUnderlyingCode));
                        List<TradeRiskCacularResult> tradeRiskListByUnderlyingCode = CglibUtil.copyList(lastRiskDbMap.get(underlyingCode), TradeRiskCacularResult::new);
                        //赋值对应合约的历史交易
                        for (TradeRiskCacularResult entry : tradeRiskListByUnderlyingCode) {
                            lastRiskInfoMap.put(entry.getId(), entry);
                        }
                        //将数据缓存到Redis中
                        for (Map.Entry<String, List<TradeRiskInfo>> entry : lastRiskDbMap.entrySet()) {
                            stringRedisTemplate.opsForHash().put(RedisAdapter.TRADE_LAST_RISK_INFO + tradeDay, entry.getKey(), JSONObject.toJSONString(entry.getValue()));
                        }
                    }
                }
                stopWatch.stop();
                stopWatch.start("获取场外的持仓信息");
                //获取场外的持仓信息
                Object s = stringRedisTemplate.opsForHash().get(RedisAdapter.OTC_RISK_UNDERLYING_LIST, underlyingCode);
                if (s != null) {
                    underlyingByRisk.setTradeMngVOList(JSONArray.parseArray(s.toString(), TradeMngVO.class));
                } else {
                    underlyingByRisk.setTradeMngVOList(new ArrayList<>());
                }
                stopWatch.stop();
                stopWatch.start("获取场内的持仓信息");
                //获取场内的持仓信息
                Object ex = stringRedisTemplate.opsForHash().get(RedisAdapter.EXCHANGE_RISK_UNDERLYING_LIST, underlyingCode);
                if (ex != null) {
                    underlyingByRisk.setExchangeRealTimePosList(JSONArray.parseArray(ex.toString(), ExchangeRealTimePos.class));
                } else {
                    underlyingByRisk.setExchangeRealTimePosList(new ArrayList<>());
                }
                stopWatch.stop();
                log.debug("setNeedRiskData:{},{}", underlyingByRisk.getUnderlyingCode(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
                return underlyingByRisk;
            }
        log.debug("setNeedRiskData:{}", stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        return new UnderlyingByRisk();
    }

    /**
     * 从数据库获取需要计算的数据
     */
    private void getDataFromDb() {

        StopWatch stopWatch = StopWatch.create("加载数据");
        //设置场外数据
        stopWatch.start("获取场外数据");
        List<TradeMngVO> survivalTrade = getOtcRiskList();
        Map<String, List<TradeMngVO>> survivalTradeMap = survivalTrade.stream().collect(Collectors.groupingBy(TradeMngVO::getUnderlyingCode));
        Set<String> underlyingSet= new HashSet<>();
        if (!survivalTradeMap.keySet().isEmpty()){
            underlyingSet.addAll(survivalTradeMap.keySet());
        }
        stopWatch.stop();
        stopWatch.start("存储场外data");
        stringRedisTemplate.delete(RedisAdapter.OTC_RISK_UNDERLYING_LIST);
        for (Map.Entry<String, List<TradeMngVO>> entry : survivalTradeMap.entrySet()) {
            stringRedisTemplate.opsForHash().put(RedisAdapter.OTC_RISK_UNDERLYING_LIST, entry.getKey(), JSONObject.toJSONString(entry.getValue()));
        }
        stopWatch.stop();
        stopWatch.start("设置场内数据");
        //设置场内数据
        List<ExchangeRealTimePos> exchangeRealTimePosList = getExchangeRiskList();
        Map<String, List<ExchangeRealTimePos>> exchangeRealTimePosMap = exchangeRealTimePosList.stream().collect(Collectors.groupingBy(e -> e.getUnderlyingCode().toUpperCase()));
        Set<String> exchangePosSet = exchangeRealTimePosList.stream().map(e -> e.getUnderlyingCode().toUpperCase()).collect(Collectors.toSet());
        underlyingSet.addAll(exchangePosSet);
        if (!exchangePosSet.isEmpty()){
            underlyingSet.addAll(exchangePosSet);
        }
        stringRedisTemplate.delete(RedisAdapter.EXCHANGE_RISK_UNDERLYING_LIST);
        for (Map.Entry<String, List<ExchangeRealTimePos>> entry : exchangeRealTimePosMap.entrySet()) {
            stringRedisTemplate.opsForHash().put(RedisAdapter.EXCHANGE_RISK_UNDERLYING_LIST, entry.getKey(), JSONObject.toJSONString(entry.getValue()));
        }
        stopWatch.stop();
        stopWatch.start("设置合约key");
        stringRedisTemplate.opsForSet().add(RedisAdapter.RISK_UNDERLYING_SET, underlyingSet.toArray(new String[0]));
        stopWatch.stop();

        Set<Object> oldSet = stringRedisTemplate.opsForHash().keys(RedisAdapter.TRADE_RISK_RESULT);
        Set<String> newSet = survivalTrade.stream().map(TradeMngVO::getTradeCode).collect(Collectors.toSet());
        newSet.addAll(exchangeRealTimePosList.stream().map(e -> e.getInvestorID() + "_" + e.getInstrumentID() + "_" +
                ("2".equals(e.getPosiDirection()) ? ExchangeEodType.LONG.name() : ExchangeEodType.SHORT.name())).collect(Collectors.toSet()));
        oldSet.removeAll(newSet);
        if (!oldSet.isEmpty()){
            stopWatch.start("删除无效的数据");
            for (Object o : oldSet){
                stringRedisTemplate.opsForHash().delete(RedisAdapter.TRADE_RISK_RESULT,o);
            }

            stopWatch.stop();
        }

        log.info("{}计算新的一批数据:{}", LocalDateTime.now(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }

    /**
     * 场外计算逻辑
     * @param tradeMngVo       场外交易记录详情
     * @param underlyingByRisk 合约信息
     * @return 风险计算结果
     */
    public TradeRiskCacularResult overCacularRisk(TradeMngVO tradeMngVo, UnderlyingByRisk underlyingByRisk) {
        TradeRiskCacularResult tradeRiskCacularResult = new TradeRiskCacularResult();
        try {
            //初始化基础信息
            tradeRiskCacularResult = initTradeRiskCacularResult(tradeMngVo, underlyingByRisk);

            //获取实时波动率
            BigDecimal midVol;
            if (OptionTypeEnum.getSnowBall().contains(tradeMngVo.getOptionType())
                    || OptionTypeEnum.AIVanillaPricer == tradeMngVo.getOptionType()) {

                LinearInterpVolSurfaceDto linearInterpVolSurfaceDto = getLinearInterpVolSurfaceDto(tradeMngVo, underlyingByRisk);
                midVol = getMidVol(linearInterpVolSurfaceDto, underlyingByRisk.getMidVolatility());
                tradeRiskCacularResult.setNowVol(midVol);
            } else {
                midVol = BigDecimal.ZERO;
                tradeRiskCacularResult.setNowVol(BigDecimal.ZERO);
            }
            //波动率覆盖
            if (Objects.nonNull(tradeMngVo.getRiskVol())) {
                tradeRiskCacularResult.setRiskVol(tradeMngVo.getRiskVol());
                midVol = tradeMngVo.getRiskVol();
            }
            //调用so
            QuoteSoResultVO quoteSoResultVO = new QuoteSoResultVO();
            handleTrade(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
            //组装TradeRiskCacularResult结果
            handleTradeRiskCacularResult(tradeRiskCacularResult, quoteSoResultVO, tradeMngVo, underlyingByRisk);
            tradeRiskCacularResult.setStatus(SuccessStatusEnum.success);
        } catch (Exception e) {
            tradeRiskCacularResult.setStatus(SuccessStatusEnum.faild);
            log.error(tradeMngVo.getTradeCode(), e.getMessage(), e);
        }
        return tradeRiskCacularResult;
    }

    @NonNull
    private LinearInterpVolSurfaceDto getLinearInterpVolSurfaceDto(TradeMngVO tradeMngVo, UnderlyingByRisk underlyingByRisk) {
        LinearInterpVolSurfaceDto linearInterpVolSurfaceDto = new LinearInterpVolSurfaceDto();
        //雪球期权的执行价格使用执行价格1如果执行价格1为空则使用入场价格
        if (OptionTypeEnum.getSnowBall().contains(tradeMngVo.getOptionType())) {
            if (tradeMngVo.getStrikeOnceKnockedinValue() != null) {
                if (tradeMngVo.getStrikeOnceKnockedinRelative()) {
                    linearInterpVolSurfaceDto.setStrike(tradeMngVo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeMngVo.getStrikeOnceKnockedinValue())));
                } else {
                    linearInterpVolSurfaceDto.setStrike(tradeMngVo.getStrikeOnceKnockedinValue());
                }
            } else {
                linearInterpVolSurfaceDto.setStrike(tradeMngVo.getEntryPrice());
            }
        } else {
            linearInterpVolSurfaceDto.setStrike(tradeMngVo.getStrike());
        }
        linearInterpVolSurfaceDto.setEntryPrice(underlyingByRisk.getLastPrice());

        linearInterpVolSurfaceDto.setUnderlyingCode(tradeMngVo.getUnderlyingCode());
        linearInterpVolSurfaceDto.setTradeDate(tradeDay);
        linearInterpVolSurfaceDto.setMaturityDate(tradeMngVo.getMaturityDate());
        linearInterpVolSurfaceDto.setBuyOrSell(tradeMngVo.getBuyOrSell());
        linearInterpVolSurfaceDto.setOptionType(tradeMngVo.getOptionType());
        return linearInterpVolSurfaceDto;
    }

    private BigDecimal getMidVol(LinearInterpVolSurfaceDto linearInterpVolSurfaceDto, VolatilityVO midVolatility) {
        List<VolatityDataDto> volatityDataList = CglibUtil.copyList(midVolatility.getData(), VolatityDataDto::new);

        VolSurface volSurface = VolatilityUtil.getVolSurface(volatityDataList);

        AILinearInterpVolSurfaceRequest aiLinearInterpVolSurface = new AILinearInterpVolSurfaceRequest();
        aiLinearInterpVolSurface.setDimMoneyness(linearInterpVolSurfaceDto.getStrike().divide(linearInterpVolSurfaceDto.getEntryPrice(), 4, RoundingMode.HALF_UP).doubleValue());

        aiLinearInterpVolSurface.setDimTenor((double) linearInterpVolSurfaceDto.getTradeDate().until(linearInterpVolSurfaceDto.getMaturityDate(), ChronoUnit.DAYS));
        AILinearInterpVolSurfaceResult midAiLinearInterpVolSurfaceResult = jniUtil.AILinearInterpVolSurface(aiLinearInterpVolSurface, volSurface);
        return BigDecimal.valueOf(midAiLinearInterpVolSurfaceResult.getVolatility()).multiply(BigDecimal.valueOf(100));
    }

    /**
     * 从数据库中获取场外数据列表
     * @return 数据列表
     */
    private List<TradeMngVO> getOtcRiskList() {
        //存续大于0的交易记录
        List<TradeMngVO> survivalTrade = tradeMngService.getSurvivalTradeByTradeDay(tradeDay);
        //当天平仓的交易记录
        List<TradeMngVO> todayCloseTrade = tradeMngService.getCloseTradeByTradeDay(tradeDay);
        survivalTrade.addAll(todayCloseTrade);
        return survivalTrade;
    }

    /**
     * 从Redis获取场内数据
     * @return 场内数据列表
     */
    public List<ExchangeRealTimePos> getExchangeRiskList() {

        Set<String> accountKeySet = stringRedisTemplate.keys(RedisAdapter.EXCHANGE_POSITION_INFO + "*");
        List<ExchangeRealTimePos> getList = new ArrayList<>();
        if (accountKeySet != null) {
            for (String key : accountKeySet) {
                String[] split = key.split(":");
                String account = split[split.length - 1];
                ExchangeAccountQueryDto exchangeAccountQueryDto = new ExchangeAccountQueryDto();
                exchangeAccountQueryDto.setAccount(account);
                ExchangeAccountFeignVO exchangeAccountFeignVO = exchangeAccountClient.getVoByname(exchangeAccountQueryDto);
                List<Object> values = stringRedisTemplate.opsForHash().values(key);
                List<ExchangeRealTimePos> exchangeRealTimePoss = JSONArray.parseArray(values.toString(), ExchangeRealTimePos.class);
                //持仓数据添加簿记信息与账号信息
                for (ExchangeRealTimePos exchangeRealTimePos : exchangeRealTimePoss) {
                    exchangeRealTimePos.setAssetId(exchangeAccountFeignVO == null ? 0 : exchangeAccountFeignVO.getAssetunitId());
                }
                getList.addAll(exchangeRealTimePoss);
            }
        }
        return getList;
    }

    /**
     * 场内计算逻辑
     * @param exchangeRealTimePos 场内持仓详情
     * @param underlyingByRisk    合约信息
     * @return 有两个值，size=0是多头的值，size=1是空头的值
     */
    public TradeRiskCacularResult exchangeCacularRisk(ExchangeRealTimePos exchangeRealTimePos, UnderlyingByRisk underlyingByRisk) {
        TradeRiskCacularResult tradeRiskCacularResult = new TradeRiskCacularResult();//多头

        try {

            //场内期货
            if (exchangeRealTimePos.getOptionsType() == 0) {
                initExchangeTradeRiskResult(tradeRiskCacularResult, exchangeRealTimePos, underlyingByRisk.getEvaluationTime(), underlyingByRisk);

                handleExchangeEuropeanTradeRiskCacularResult(tradeRiskCacularResult, exchangeRealTimePos, underlyingByRisk);
            } else {
                LocalDate expireDate = LocalDate.parse(exchangeRealTimePos.getExpireDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                //场内期权
                initExchangeTradeRiskResult(tradeRiskCacularResult, exchangeRealTimePos, underlyingByRisk.getEvaluationTime(), underlyingByRisk);
                //获取实时波动率
                LinearInterpVolSurfaceDto linearInterpVolSurfaceDto = getLinearInterpVolSurfaceDto(exchangeRealTimePos, expireDate, underlyingByRisk);
                BigDecimal midVol = getMidVol(linearInterpVolSurfaceDto, underlyingByRisk.getMidVolatility());
                QuoteSoResultVO quoteSoResultVO = new QuoteSoResultVO();
                handleExchangeTrade(quoteSoResultVO, exchangeRealTimePos, underlyingByRisk, midVol, expireDate);
                tradeRiskCacularResult.setStatus(SuccessStatusEnum.success);
                handleExchangeOptionTradeRiskCacularResult(tradeRiskCacularResult, quoteSoResultVO, exchangeRealTimePos, underlyingByRisk, midVol, expireDate);

            }
        } catch (Exception e) {
            log.error(exchangeRealTimePos.getInstrumentID(), e.getMessage(), e);
            tradeRiskCacularResult.setStatus(SuccessStatusEnum.faild);

        }
        return tradeRiskCacularResult;
    }

    @NonNull
    private LinearInterpVolSurfaceDto getLinearInterpVolSurfaceDto(ExchangeRealTimePos exchangeRealTimePos
            , LocalDate expireDate, UnderlyingByRisk underlyingByRisk) {
        LinearInterpVolSurfaceDto linearInterpVolSurfaceDto = new LinearInterpVolSurfaceDto();
        linearInterpVolSurfaceDto.setStrike(exchangeRealTimePos.getStrikePrice());
        linearInterpVolSurfaceDto.setUnderlyingCode(underlyingByRisk.getUnderlyingCode());
        linearInterpVolSurfaceDto.setTradeDate(tradeDay);
        linearInterpVolSurfaceDto.setEntryPrice(underlyingByRisk.getLastPrice());
        linearInterpVolSurfaceDto.setMaturityDate(expireDate);
        return linearInterpVolSurfaceDto;
    }

    /**
     * 风险计算调用so
     * @param quoteSoResultVO 风险计算结果
     * @param tradeMngVo      交易记录详情
     * @param midVol          mid波动率
     */
    private void handleTrade(QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVo, BigDecimal midVol, UnderlyingByRisk underlyingByRisk) {
        switch (tradeMngVo.getOptionType()) {
            case AIVanillaPricer:
                quoteAIVanillaPricer(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
                break;
            case AIAsianPricer:
                quoteAIAsianPricer(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
                break;
            case AIEnAsianPricer:
                enAsianPricer(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
                break;
            case AICallAccPricer:
            case AIPutAccPricer:
            case AICallFixAccPricer:
            case AIPutFixAccPricer:
                accumulatorPricer(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
                break;
            case AICallKOAccPricer:
            case AIPutKOAccPricer:
            case AICallFixKOAccPricer:
            case AIPutFixKOAccPricer:
            case AIEnPutKOAccPricer:
            case AIEnCallKOAccPricer:
                koAccumulatorPricer(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
                break;
            case AIForwardPricer:
                forwardPricer(quoteSoResultVO, tradeMngVo, underlyingByRisk.getLastPrice());
                break;
            case AISnowBallCallPricer:
            case AISnowBallPutPricer:
            case AILimitLossesSnowBallCallPricer:
            case AILimitLossesSnowBallPutPricer:
            case AIBreakEvenSnowBallCallPricer:
            case AIBreakEvenSnowBallPutPricer:
                snowBallPricer(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
                break;
            case AICustomPricer:
                insuranceAsianPricer(quoteSoResultVO, tradeMngVo, midVol, underlyingByRisk);
                break;
            default:
                BussinessException.E_300102.doThrow("期权类型错误", OptionTypeEnum.AIVanillaPricer);
        }
    }

    /**
     * 香草计算
     * @param quoteSoResultVO  风险计算结果
     * @param tradeMngVo       交易记录详情
     * @param midVol           mid波动率
     * @param underlyingByRisk 合约信息
     */
    private void quoteAIVanillaPricer(QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVo, BigDecimal midVol, UnderlyingByRisk underlyingByRisk) {
        AIVanillaPricerRequest aiVanillaPricerRequestDto = new AIVanillaPricerRequest();
        aiVanillaPricerRequestDto.setOptionType(tradeMngVo.getCallOrPut().name());
        aiVanillaPricerRequestDto.setStrike(tradeMngVo.getStrike().doubleValue());
        aiVanillaPricerRequestDto.setVolatility(BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue());
        aiVanillaPricerRequestDto.setRiskFreeInterestRate(riskFreeInterestRate.doubleValue());
        aiVanillaPricerRequestDto.setDividendYield(dividendYield.doubleValue());
        aiVanillaPricerRequestDto.setUnderlyingPrice(underlyingByRisk.getLastPrice().doubleValue());
        aiVanillaPricerRequestDto.setEvaluationTime(underlyingByRisk.getEvaluationTime());
        aiVanillaPricerRequestDto.setExpiryTime(tradeMngVo.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
        AIVanillaPricerResult aiVanillaPricerResult = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto);
        quoteSoResultVO.setPv(BigDecimal.valueOf(aiVanillaPricerResult.getPv()));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(aiVanillaPricerResult.getDelta()));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(aiVanillaPricerResult.getGamma()));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(aiVanillaPricerResult.getThetaPerDay()));
        quoteSoResultVO.setVega(BigDecimal.valueOf(aiVanillaPricerResult.getVegaPercentage()));
        quoteSoResultVO.setRho(BigDecimal.valueOf(aiVanillaPricerResult.getRhoPercentage()));
        quoteSoResultVO.setDividendRho(BigDecimal.valueOf(aiVanillaPricerResult.getDividendRhoPercentage()));
    }

    /**
     * 亚式计算
     * @param quoteSoResultVO  风险计算结果
     * @param tradeMngVo       交易记录详情
     * @param midVol           mid波动率
     * @param underlyingByRisk 合约信息
     */
    private void quoteAIAsianPricer(QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVo, BigDecimal midVol, UnderlyingByRisk underlyingByRisk) {
        AIAsianPricerRequest aiAsianPricerRequest = new AIAsianPricerRequest(tradeMngVo.getCallOrPut().name(),
                underlyingByRisk.getLastPrice().doubleValue(),
                underlyingByRisk.getEvaluationTime(),
                tradeMngVo.getStrike().doubleValue(),
                tradeMngVo.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                riskFreeInterestRate.doubleValue(),
                BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue(),
                tradeMngVo.getTradeObsDateList().size());
        AIAsianPricerResult aiAsianPricerResult = jniUtil.AIAsianPricer(aiAsianPricerRequest, handleObsDate(tradeMngVo.getTradeObsDateList()).toArray(new ObserveSchedule[0]));
        quoteSoResultVO.setPv(BigDecimal.valueOf(aiAsianPricerResult.getPv()));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(aiAsianPricerResult.getDelta()));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(aiAsianPricerResult.getGamma()));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(aiAsianPricerResult.getThetaPerDay()));
        quoteSoResultVO.setVega(BigDecimal.valueOf(aiAsianPricerResult.getVegaPercentage()));
        quoteSoResultVO.setRho(BigDecimal.valueOf(aiAsianPricerResult.getRhoPercentage()));
        quoteSoResultVO.setDividendRho(BigDecimal.valueOf(aiAsianPricerResult.getDividendRhoPercentage()));
    }

    /**
     * 增强亚式计算
     * @param quoteSoResultVO  风险计算结果
     * @param tradeMngVo       交易记录详情
     * @param midVol           mid波动率
     * @param underlyingByRisk 合约信息
     */
    private void enAsianPricer(QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVo, BigDecimal midVol, UnderlyingByRisk underlyingByRisk) {

        List<VolatityDataDto> volatityDataList = CglibUtil.copyList(underlyingByRisk.getMidVolatility().getData(), VolatityDataDto::new);
        VolSurface volSurface = VolatilityUtil.getVolSurface(volatityDataList);

        AIEnhancedAsianPricerRequest request = new AIEnhancedAsianPricerRequest();
        request.setConstantVol(BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue());
        request.setExpiryTime(tradeMngVo.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
        request.setStrike(tradeMngVo.getStrike().doubleValue());
        request.setEvaluationTime(underlyingByRisk.getEvaluationTime());
        request.setOptionType(tradeMngVo.getCallOrPut().name());
        request.setUnderlyingPrice(underlyingByRisk.getLastPrice().doubleValue());
        request.setRiskFreeInterestRate(riskFreeInterestRate.doubleValue());
        request.setScenarioPrice(0);
        request.setValueType("a");
        request.setTotalObservations(tradeMngVo.getTradeObsDateList().size());
        request.setIsCashSettled(tradeMngVo.getSettleType().getKey());
        request.setEnhancedStrike(tradeMngVo.getEnhancedStrike().doubleValue());


        AIEnhancedAsianPricerResult aiEnhancedAsianPricerResult = jniUtil.AIEnhancedAsianPricer(request, handleObsDate(tradeMngVo.getTradeObsDateList()).toArray(new ObserveSchedule[0]), volSurface);
        quoteSoResultVO.setPv(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getPv()));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getDelta()));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getGamma()));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getThetaPerDay()));
        quoteSoResultVO.setVega(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getVegaPercentage()));
        quoteSoResultVO.setRho(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getRhoPercentage()));
        quoteSoResultVO.setDividendRho(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getDividendRhoPercentage()));
        quoteSoResultVO.setAccumulatedPosition(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getAccumulatedPosition()));
        quoteSoResultVO.setAccumulatedPayment(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getAccumulatedPayment()));
        quoteSoResultVO.setAccumulatedPnl(BigDecimal.valueOf(aiEnhancedAsianPricerResult.getAccumulatedPnl()));
    }

    /**
     * 累计计算
     * @param quoteSoResultVO  风险计算结果
     * @param tradeMngVo       交易记录详情
     * @param midVol           mid波动率
     * @param underlyingByRisk 合约信息
     */
    private void accumulatorPricer(QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVo, BigDecimal midVol, UnderlyingByRisk underlyingByRisk) {

        List<VolatityDataDto> volatityDataList = CglibUtil.copyList(underlyingByRisk.getMidVolatility().getData(), VolatityDataDto::new);
        VolSurface volSurface = VolatilityUtil.getVolSurface(volatityDataList);

        String accumulatorType = QuoteUtil.getAccumulatorType(tradeMngVo.getOptionType());
        AIAccumulatorPricerRequest aiAccumulatorPricerRequest = new AIAccumulatorPricerRequest(
                accumulatorType,
                "a",
                tradeMngVo.getBuyOrSell() == BuyOrSellEnum.buy ? -1 : 1,
                tradeMngVo.getBasicQuantity().doubleValue(),
                underlyingByRisk.getLastPrice().doubleValue(),
                tradeMngVo.getStrike().doubleValue(),
                underlyingByRisk.getEvaluationTime(),
                tradeMngVo.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond(),
                BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue(),
                tradeMngVo.getSettleType().getKey(),
                riskFreeInterestRate.doubleValue(),
                tradeMngVo.getLeverage().doubleValue(),
                tradeMngVo.getFixedPayment() == null ? 0.0 : tradeMngVo.getFixedPayment().doubleValue(),
                tradeMngVo.getBarrier().doubleValue(),
                Objects.nonNull(tradeMngVo.getStrikeRamp()) ? tradeMngVo.getStrikeRamp().doubleValue() : 0,
                tradeMngVo.getBarrierRamp().doubleValue(),
                tradeMngVo.getTradeObsDateList().size(),
                0);
        AIAccumulatorPricerResult aiAccumulatorPricerResult = jniUtil.AIAccumulatorPricer(aiAccumulatorPricerRequest, handleObsDate(tradeMngVo.getTradeObsDateList()).toArray(new ObserveSchedule[0]), volSurface);
        quoteSoResultVO.setPv(BigDecimal.valueOf(aiAccumulatorPricerResult.getPv()));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(aiAccumulatorPricerResult.getDelta()));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(aiAccumulatorPricerResult.getGamma()));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(aiAccumulatorPricerResult.getThetaPerDay()));
        quoteSoResultVO.setVega(BigDecimal.valueOf(aiAccumulatorPricerResult.getVegaPercentage()));
        quoteSoResultVO.setRho(BigDecimal.valueOf(aiAccumulatorPricerResult.getRhoPercentage()));
        quoteSoResultVO.setDividendRho(BigDecimal.valueOf(aiAccumulatorPricerResult.getDividendRhoPercentage()));
        quoteSoResultVO.setAccumulatedPosition(BigDecimal.valueOf(aiAccumulatorPricerResult.getAccumulatedPosition()));
        quoteSoResultVO.setAccumulatedPayment(BigDecimal.valueOf(aiAccumulatorPricerResult.getAccumulatedPayment()));
        quoteSoResultVO.setAccumulatedPnl(BigDecimal.valueOf(aiAccumulatorPricerResult.getAccumulatedPnl()));
    }

    /**
     * 远期计算
     * @param quoteSoResultVO 风险计算结果
     * @param tradeMngVo      交易记录详情
     * @param lastPrice       行情
     */
    private void forwardPricer(QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVo, BigDecimal lastPrice) {
        AIForwardPricerRequest aiForwardPricerRequest = new AIForwardPricerRequest(lastPrice.doubleValue(),
                tradeMngVo.getStrike().doubleValue()
        );
        AIForwardPricerResult aiForwardPricerResult = jniUtil.AIForwardPricer(aiForwardPricerRequest);
        quoteSoResultVO.setPv(BigDecimal.valueOf(aiForwardPricerResult.getPv()));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(aiForwardPricerResult.getDelta()));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(aiForwardPricerResult.getGamma()));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(aiForwardPricerResult.getThetaPerDay()));
        quoteSoResultVO.setVega(BigDecimal.valueOf(aiForwardPricerResult.getVegaPercentage()));
        quoteSoResultVO.setRho(BigDecimal.valueOf(aiForwardPricerResult.getRhoPercentage()));
        quoteSoResultVO.setDividendRho(BigDecimal.valueOf(aiForwardPricerResult.getDividendRhoPercentage()));
    }

    private ObserveSchedule[] getObserveScheduleArray(List<TradeObsDateVO> tradeObsDateList) {
        List<ObserveSchedule> observeScheduleList = new ArrayList<>();
        BussinessException.E_300101.assertTrue(Objects.nonNull(tradeObsDateList) && !tradeObsDateList.isEmpty(), "观察日期不能为空");
        for (TradeObsDateVO tradeObsDateVO : tradeObsDateList) {
            ObserveSchedule observeSchedule = new ObserveSchedule();
            observeSchedule.setObserveDate(tradeObsDateVO.getObsDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
            if (Objects.nonNull(tradeObsDateVO.getPrice())) {
                observeSchedule.setFixedPrice(tradeObsDateVO.getPrice().doubleValue());
            } else {
                observeSchedule.setFixedPrice(0);
            }
            observeScheduleList.add(observeSchedule);
        }
        return observeScheduleList.toArray(new ObserveSchedule[0]);
    }

    /**
     * 熔断累计期权计算
     * @param quoteSoResultVO 计算结果
     * @param tradeMngVO      定价计算明细
     * @param underlying      合约详情
     */
    private void koAccumulatorPricer(QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVO, BigDecimal midVol, UnderlyingByRisk underlying) {
        ObserveSchedule[] observeScheduleArray = getObserveScheduleArray(tradeMngVO.getTradeObsDateList());
        //如果敲出赔付没传则用0
        if (Objects.isNull(tradeMngVO.getKnockoutRebate())) {
            tradeMngVO.setKnockoutRebate(BigDecimal.ZERO);
        }
        AIKOAccumulatorPricerRequest aiKOAccumulatorPricerRequest =
                AIKOAccumulatorPricerRequest.builder()
                        .accumulatorType(QuoteUtil.getAccumulatorType(tradeMngVO.getOptionType()))
                        .valueType("a")
                        .buySell(tradeMngVO.getBuyOrSell() == BuyOrSellEnum.buy ? -1 : 1)
                        .basicQuantity(tradeMngVO.getBasicQuantity().abs().doubleValue())
                        .entryUnderlyingPrice(tradeMngVO.getEntryPrice().doubleValue())
                        .underlyingPrice(underlying.getLastPrice().doubleValue())
                        .strike(tradeMngVO.getStrike().doubleValue())
                        .evaluationTime(underlying.getEvaluationTime())
                        .expiryTime(tradeMngVO.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .constantVol(BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue())
                        .isCashSettled(tradeMngVO.getSettleType().getKey())
                        .riskFreeInterestRate(riskFreeInterestRate.doubleValue())
                        .dividendYield(underlying.getUnderlyingDividendYield().doubleValue())
                        .dailyLeverage(tradeMngVO.getLeverage().doubleValue())
                        .fixedPayment(tradeMngVO.getFixedPayment() == null ? 0.0 : tradeMngVO.getFixedPayment().doubleValue())
                        .totalObservations(observeScheduleArray.length)
                        .barrier(tradeMngVO.getBarrier().doubleValue())
                        .expiryLeverage(tradeMngVO.getExpireMultiple().doubleValue())
                        .knockoutRebate(tradeMngVO.getKnockoutRebate().doubleValue())
                        .build();
        List<VolatityDataDto> volatityDataList = CglibUtil.copyList(underlying.getMidVolatility().getData(), VolatityDataDto::new);
        VolSurface midVolSurface = VolatilityUtil.getVolSurface(volatityDataList);

        AIKOAccumulatorPricerResult aiKOAccumulatorPricerResult = jniUtil.AIKOAccumulatorPricer(aiKOAccumulatorPricerRequest, observeScheduleArray, midVolSurface);
        quoteSoResultVO.setPv(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getPv()));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getDelta()));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getGamma()));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getThetaPerDay()));
        quoteSoResultVO.setVega(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getVegaPercentage()));
        quoteSoResultVO.setRho(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getRhoPercentage()));
        quoteSoResultVO.setDividendRho(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getDividendRhoPercentage()));
        quoteSoResultVO.setAccumulatedPosition(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getAccumulatedPosition()));
        quoteSoResultVO.setAccumulatedPayment(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getAccumulatedPayment()));
        quoteSoResultVO.setAccumulatedPnl(BigDecimal.valueOf(aiKOAccumulatorPricerResult.getAccumulatedPnl()));
    }

    /**
     * 雪球计算结果
     * @param resultVo   计算结果
     * @param tradeMngVO 交易记录
     */
    private void snowBallPricer(QuoteSoResultVO resultVo, TradeMngVO tradeMngVO, BigDecimal midVol, UnderlyingByRisk underlyingByRisk) {

        KnockOutSchedule[] knockOutScheduleArray = getKnockOutScheduleArray(tradeMngVO.getTradeObsDateList());
        //定价方法
        AlgorithmParameters algorithmParameters = new AlgorithmParameters();
        algorithmParameters.setAlgorithmName(tradeMngVO.getAlgorithmName());
        algorithmParameters.setMcNumberPaths(mcNumberPaths);
        algorithmParameters.setPdeTimeGrid(pdeTimeGrid);
        algorithmParameters.setPdeSpotGrid(pdeSpotGrid);
        //返息率
        RateStruct returnRate;
        if (tradeMngVO.getReturnRateStructValue() != null) {
            returnRate = new RateStruct(BigDecimalUtil.percentageToBigDecimal(tradeMngVO.getReturnRateStructValue()).doubleValue(), tradeMngVO.getReturnRateAnnulized());
        } else {
            returnRate = new RateStruct();
        }
        //红利票息率
        tradeMngVO.setBonusRateStructValue(BigDecimalUtil.percentageToBigDecimal(tradeMngVO.getBonusRateStructValue()));
        RateStruct bonusRate = new RateStruct(tradeMngVO.getBonusRateStructValue().doubleValue(), tradeMngVO.getBonusRateAnnulized());

        AISnowBallPricerRequest aiSnowBallPricerRequest =
                AISnowBallPricerRequest.builder()
                        .algorithmParameters(algorithmParameters)
                        .evaluationTime(underlyingByRisk.getEvaluationTime())
                        .underlyingPrice(underlyingByRisk.getLastPrice().doubleValue())
                        .riskFreeInterestRate(riskFreeInterestRate.doubleValue())
                        .dividendYield(underlyingByRisk.getUnderlyingDividendYield().doubleValue())
                        .volatility(BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue())
                        .returnRate(returnRate)
                        .optionType(getCallOrPut(tradeMngVO.getOptionType()))
                        .bonusRate(bonusRate)
                        .totalObservations(knockOutScheduleArray.length)
                        .productStartDate(tradeMngVO.getProductStartDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .productEndDate(tradeMngVO.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                        .entryUnderlyingPrice(tradeMngVO.getEntryPrice().doubleValue())
                        .alreadyKnockedIn(tradeMngVO.getAlreadyKnockedIn() != null && tradeMngVO.getAlreadyKnockedIn())
                        .build();
        Level defaultLeve = new Level();
        //敲入价格
        if (tradeMngVO.getKnockinBarrierValue() != null && tradeMngVO.getKnockinBarrierRelative() != null) {
            if (tradeMngVO.getKnockinBarrierRelative()) {
                tradeMngVO.setKnockinBarrierValue(BigDecimalUtil.percentageToBigDecimal(tradeMngVO.getKnockinBarrierValue()));
            }
            Level knockinBarrier = new Level(tradeMngVO.getKnockinBarrierValue().doubleValue(), tradeMngVO.getKnockinBarrierRelative(),
                    tradeMngVO.getKnockinBarrierShift() == null ? 0 : tradeMngVO.getKnockinBarrierShift().doubleValue());
            aiSnowBallPricerRequest.setKnockinBarrier(knockinBarrier);
        } else {
            aiSnowBallPricerRequest.setKnockinBarrier(defaultLeve);
        }
        //敲入行权价格
        if (tradeMngVO.getStrikeOnceKnockedinRelative() != null && tradeMngVO.getStrikeOnceKnockedinValue() != null) {
            if (tradeMngVO.getStrikeOnceKnockedinRelative()) {
                tradeMngVO.setStrikeOnceKnockedinValue(BigDecimalUtil.percentageToBigDecimal(tradeMngVO.getStrikeOnceKnockedinValue()));
            }
            Level strikeOnceKnockedin = new Level(tradeMngVO.getStrikeOnceKnockedinValue().doubleValue(), tradeMngVO.getStrikeOnceKnockedinRelative()
                    , tradeMngVO.getStrikeOnceKnockedinShift() == null ? 0 : tradeMngVO.getStrikeOnceKnockedinShift().doubleValue());
            aiSnowBallPricerRequest.setStrikeOnceKnockedin(strikeOnceKnockedin);
        } else {
            aiSnowBallPricerRequest.setStrikeOnceKnockedin(defaultLeve);
        }
        //敲入行权价格2
        if (tradeMngVO.getStrike2OnceKnockedinValue() != null && tradeMngVO.getStrike2OnceKnockedinRelative() != null) {
            if (tradeMngVO.getStrike2OnceKnockedinRelative()) {
                tradeMngVO.setStrike2OnceKnockedinValue(BigDecimalUtil.percentageToBigDecimal(tradeMngVO.getStrike2OnceKnockedinValue()));
            }
            Level strike2OnceKnockedin = new Level(tradeMngVO.getStrike2OnceKnockedinValue().doubleValue(), tradeMngVO.getStrike2OnceKnockedinRelative()
                    , tradeMngVO.getStrike2OnceKnockedinShift() == null ? 0 : tradeMngVO.getStrike2OnceKnockedinShift().doubleValue());
            aiSnowBallPricerRequest.setStrike2OnceKnockedin(strike2OnceKnockedin);
        } else {
            if (tradeMngVO.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                aiSnowBallPricerRequest.setStrike2OnceKnockedin(Level.builder().levelRelative(true).levelValue(2).build());
            } else {
                aiSnowBallPricerRequest.setStrike2OnceKnockedin(defaultLeve);
            }
        }
        AISnowBallPricerResult aiSnowBallPricerResult = jniUtil.AISnowBallPricer(aiSnowBallPricerRequest, knockOutScheduleArray);
        resultVo.setPv(BigDecimal.valueOf(aiSnowBallPricerResult.getPv()));
        resultVo.setDelta(BigDecimal.valueOf(aiSnowBallPricerResult.getDelta()));
        resultVo.setGamma(BigDecimal.valueOf(aiSnowBallPricerResult.getGamma()));
        resultVo.setTheta(BigDecimal.valueOf(aiSnowBallPricerResult.getThetaPerDay()));
        resultVo.setVega(BigDecimal.valueOf(aiSnowBallPricerResult.getVegaPercentage()));
        resultVo.setRho(BigDecimal.valueOf(aiSnowBallPricerResult.getRhoPercentage()));
        resultVo.setDividendRho(BigDecimal.valueOf(aiSnowBallPricerResult.getDividendRhoPercentage()));
    }

    /**
     * 保险亚式计算结果
     * @param resultVo         计算结果
     * @param tradeMngVO       交易明细
     * @param underlyingByRisk 合约详情
     */
    private void insuranceAsianPricer(QuoteSoResultVO resultVo, TradeMngVO tradeMngVO, BigDecimal midVol, UnderlyingByRisk underlyingByRisk) {
        ObserveSchedule[] observeSchedules = getObserveScheduleArray(tradeMngVO.getTradeObsDateList());
        AIInsuranceAsianPricerRequest insuranceAsianPricerRequest = AIInsuranceAsianPricerRequest.builder()
                .callPut(tradeMngVO.getCallOrPut().name())
                .ceilFloor(tradeMngVO.getCeilFloor().name())
                .underlyingPrice(tradeMngVO.getEntryPrice().doubleValue())
                .strike1(tradeMngVO.getStrike().doubleValue())
                .strike2(tradeMngVO.getStrike2().doubleValue())
                .discountRate(BigDecimalUtil.percentageToBigDecimal(tradeMngVO.getDiscountRate()).doubleValue())
                .evaluationTime(underlyingByRisk.getEvaluationTime())
                .expiryTime(tradeMngVO.getMaturityDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond())
                .constantVol(BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue())
                .totalObservations(observeSchedules.length)
                .riskFreeInterestRate(riskFreeInterestRate.doubleValue())
                .dividendRate(riskFreeInterestRate.doubleValue())
                .pathNumber(pathNumber)
                .threadNumber(threadNumber)
                .build();
        List<VolatityDataDto> volatityDataList = CglibUtil.copyList(underlyingByRisk.getMidVolatility().getData(), VolatityDataDto::new);
        VolSurface volSurface = VolatilityUtil.getVolSurface(volatityDataList);
        //计算希腊字母使用midVol
        AIInsuranceAsianPricerResult aiInsuranceAsianPricerResult = jniUtil.AIInsuranceAsianPricer(insuranceAsianPricerRequest, observeSchedules, volSurface);
        resultVo.setPv(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getPv()));
        resultVo.setDelta(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getDelta()));
        resultVo.setGamma(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getGamma()));
        resultVo.setTheta(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getThetaPerDay()));
        resultVo.setVega(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getVegaPercentage()));
        resultVo.setRho(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getRhoPercentage()));
        resultVo.setDividendRho(BigDecimal.valueOf(aiInsuranceAsianPricerResult.getDividendRhoPercentage()));
    }
    private KnockOutSchedule[] getKnockOutScheduleArray(List<TradeObsDateVO> tradeObsDateList) {
        List<KnockOutSchedule> knockOutScheduleList = new ArrayList<>();
        BussinessException.E_300101.assertTrue(Objects.nonNull(tradeObsDateList) && !tradeObsDateList.isEmpty(), "观察日期不能为空");
        for (TradeObsDateVO dto : tradeObsDateList) {
            KnockOutSchedule observeSchedule = new KnockOutSchedule();
            observeSchedule.setBarrierRelative(dto.getBarrierRelative());
            observeSchedule.setRebateRateAnnulized(dto.getRebateRateAnnulized());
            observeSchedule.setObserveDate(dto.getObsDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
            //障碍部分赋值
            if (Objects.nonNull(dto.getBarrierRelative())) {
                observeSchedule.setBarrierRelative(dto.getBarrierRelative());
                if (dto.getBarrierRelative()) {
                    dto.setBarrier(BigDecimalUtil.percentageToBigDecimal(dto.getBarrier()));
                }
                observeSchedule.setBarrier(dto.getBarrier().doubleValue());
            }
            //票息部分赋值
            if (Objects.nonNull(dto.getRebateRateAnnulized())) {
                observeSchedule.setRebateRateAnnulized(dto.getRebateRateAnnulized());
                if (dto.getRebateRateAnnulized()) {
                    dto.setRebateRate(BigDecimalUtil.percentageToBigDecimal(dto.getRebateRate()));
                }
                observeSchedule.setRebateRate(dto.getRebateRate().doubleValue());
            }
            if (Objects.nonNull(dto.getPrice())) {
                observeSchedule.setFixedPrice(dto.getPrice().doubleValue());
            }
            if (Objects.nonNull(dto.getBarrierShift())) {
                observeSchedule.setBarrierShift(dto.getBarrierShift().doubleValue());
            }
            knockOutScheduleList.add(observeSchedule);
        }
        return knockOutScheduleList.toArray(new KnockOutSchedule[0]);
    }

    /**
     * 场内计算
     * @param quoteSoResultVO     风险计算结果
     * @param exchangeRealTimePos 场内交易记录详情
     * @param midVol              mid波动率
     * @param expireDate          合约失效时间
     */
    private void handleExchangeTrade(QuoteSoResultVO quoteSoResultVO, ExchangeRealTimePos exchangeRealTimePos, UnderlyingByRisk underlyingByRisk, BigDecimal midVol, LocalDate expireDate) {
        AIVanillaPricerRequest aiVanillaPricerRequestDto = new AIVanillaPricerRequest();
        aiVanillaPricerRequestDto.setOptionType(exchangeRealTimePos.getOptionsType() == 1 ? "call" : "put");
        aiVanillaPricerRequestDto.setStrike(exchangeRealTimePos.getStrikePrice().doubleValue());
        aiVanillaPricerRequestDto.setVolatility(BigDecimalUtil.percentageToBigDecimal(midVol).doubleValue());
        aiVanillaPricerRequestDto.setRiskFreeInterestRate(riskFreeInterestRate.doubleValue());
        aiVanillaPricerRequestDto.setDividendYield(underlyingByRisk.getUnderlyingDividendYield().doubleValue());
        aiVanillaPricerRequestDto.setUnderlyingPrice(underlyingByRisk.getLastPrice().doubleValue());
        aiVanillaPricerRequestDto.setEvaluationTime(underlyingByRisk.getEvaluationTime());
        aiVanillaPricerRequestDto.setExpiryTime(expireDate.atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
        AIVanillaPricerResult aiVanillaPricerResult = jniUtil.AIVanillaPricer(aiVanillaPricerRequestDto);
        quoteSoResultVO.setPv(BigDecimal.valueOf(aiVanillaPricerResult.getPv()));
        quoteSoResultVO.setDelta(BigDecimal.valueOf(aiVanillaPricerResult.getDelta()));
        quoteSoResultVO.setGamma(BigDecimal.valueOf(aiVanillaPricerResult.getGamma()));
        quoteSoResultVO.setTheta(BigDecimal.valueOf(aiVanillaPricerResult.getThetaPerDay()));
        quoteSoResultVO.setVega(BigDecimal.valueOf(aiVanillaPricerResult.getVegaPercentage()));
        quoteSoResultVO.setRho(BigDecimal.valueOf(aiVanillaPricerResult.getRhoPercentage()));
    }


    /**
     * 场外记录初始化数据
     */
    private TradeRiskCacularResult initTradeRiskCacularResult(TradeMngVO tradeMngVo, UnderlyingByRisk underlyingByRisk) {
        TradeRiskCacularResult result = CglibUtil.copy(tradeMngVo, TradeRiskCacularResult.class);
        result.setId(tradeMngVo.getTradeCode());
        result.setTradeRiskCacularResultSourceType(TradeRiskCacularResultSourceType.over);
        result.setTradeRiskCacularResultType(TradeRiskCacularResultType.option);
        //观察日列表
        result.setObsDateList(tradeMngVo.getTradeObsDateList() != null && !tradeMngVo.getTradeObsDateList().isEmpty()
                ? CglibUtil.copyList(tradeMngVo.getTradeObsDateList(), TradeObsDateVO::new)
                : new ArrayList<>());
        //风险里面买卖方向相反
        result.setBuyOrSell(tradeMngVo.getBuyOrSell() == BuyOrSellEnum.buy ? BuyOrSellEnum.sell : BuyOrSellEnum.buy);
        //实现盈亏
        result.setPositionProfitLoss(tradeMngVo.getTotalProfitLoss());
        //期权单价取反
        result.setOptionPremium(tradeMngVo.getOptionPremium() != null ? tradeMngVo.getOptionPremium().negate() : null);
        result.setTotalAmount(tradeMngVo.getTotalAmount().negate());
        result.setNotionalPrincipal(tradeMngVo.getNotionalPrincipal());
        result.setAvailableNotionalPrincipal(tradeMngVo.getAvailableNotionalPrincipal() == null
                ? tradeMngVo.getNotionalPrincipal() : tradeMngVo.getAvailableNotionalPrincipal());


        //如果交易记录为成交当日，则将获取该交易的Day1Pnl
        result.setDay1PnL(tradeMngVo.getTradeDate().isEqual(tradeDay)?tradeMngVo.getDay1PnL():BigDecimal.ZERO);
        initResultInfo(result, underlyingByRisk);
        return result;
    }

    /**
     * 风险计算默认赋值
     * @param result           计算结果
     * @param underlyingByRisk 计算合约
     */
    private void initResultInfo(TradeRiskCacularResult result, UnderlyingByRisk underlyingByRisk) {
        //计算结果初始化
        result.setDeltaLots(BigDecimal.ZERO);
        result.setDeltaCash(BigDecimal.ZERO);
        result.setGammaLots(BigDecimal.ZERO);
        result.setGammaCash(BigDecimal.ZERO);
        result.setVega(BigDecimal.ZERO);
        result.setTheta(BigDecimal.ZERO);
        result.setLastPrice(BigDecimal.ZERO);
        result.setNowVol(BigDecimal.ZERO);
        result.setTotalProfitLoss(BigDecimal.ZERO);
        result.setTodayProfitLoss(BigDecimal.ZERO);
        result.setAvailablePremium(BigDecimal.ZERO);
        result.setAvailableAmount(BigDecimal.ZERO);
        result.setAccumulatedPnl(BigDecimal.ZERO);
        result.setAccumulatedPayment(BigDecimal.ZERO);
        result.setAccumulatedPosition(BigDecimal.ZERO);
        result.setTodayAccumulatedPosition(BigDecimal.ZERO);
        result.setTodayAccumulatedPayment(BigDecimal.ZERO);
        result.setTodayAccumulatedPnl(BigDecimal.ZERO);
        result.setRiskFreeInterestRate(riskFreeInterestRate);
        result.setUnderlyingName(underlyingByRisk.getUnderlyingCode());
        result.setCacularTime(underlyingByRisk.getEvaluationTime());
        result.setDividendYield(underlyingByRisk.getUnderlyingDividendYield());
        result.setExchangeUnderlyingCode(underlyingByRisk.getExchangeUnderlyingCode());
        result.setLastPrice(underlyingByRisk.getLastPrice());
        result.setMultiplier(underlyingByRisk.getContractSize());
        result.setVarietyId(underlyingByRisk.getVarietyId());
        result.setVarietyCode(underlyingByRisk.getVarietyCode());

        //今日开平记录
        result.setTodayOpenAmount(todayOpenTradeAmountMap.get(result.getId()) == null ? BigDecimal.ZERO : todayOpenTradeAmountMap.get(result.getId()));
        result.setTodayCloseAmount(todayCloseTradeAmountMap.get(result.getId()) == null ? BigDecimal.ZERO : todayCloseTradeAmountMap.get(result.getId()));
    }

    /**
     * 组装计算后的场外数据
     * @param result          风险计算结果
     * @param quoteSoResultVO so计算结果
     * @param tradeMngVo      交易记录详情
     * @param underlying      合约
     */
    private void handleTradeRiskCacularResult(TradeRiskCacularResult result, QuoteSoResultVO quoteSoResultVO, TradeMngVO tradeMngVo, UnderlyingByRisk underlying) {
        BigDecimal delta;
        BigDecimal deltaLots;
        BigDecimal deltaCash;
        BigDecimal gamma;
        BigDecimal gammaLots;
        BigDecimal gammaCash;
        BigDecimal vega;
        BigDecimal theta;
        BigDecimal rho;
        BigDecimal dividendRho;
        switch (tradeMngVo.getOptionType()) {
            //累计期权
            case AICallAccPricer:
            case AIPutAccPricer:
            case AICallFixAccPricer:
            case AIPutFixAccPricer:
            case AICallKOAccPricer:
            case AIPutKOAccPricer:
            case AICallFixKOAccPricer:
            case AIPutFixKOAccPricer:
            case AIEnPutKOAccPricer:
            case AIEnCallKOAccPricer:
                //期权单机
                result.setAvailablePremium(quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP));
                //累计期权如果存续数量为0后
                if (tradeMngVo.getAvailableVolume().compareTo(BigDecimal.ZERO) != 0) {
                    result.setDelta(quoteSoResultVO.getDelta().setScale(4, RoundingMode.HALF_UP));
                    result.setDeltaLots(quoteSoResultVO.getDelta().divide(BigDecimal.valueOf(underlying.getContractSize()), 4, RoundingMode.HALF_UP));
                    result.setDeltaCash(quoteSoResultVO.getDelta().multiply(underlying.getLastPrice()).setScale(4, RoundingMode.HALF_UP));
                    result.setGamma(quoteSoResultVO.getGamma().setScale(4, RoundingMode.HALF_UP));
                    result.setGammaLots(quoteSoResultVO.getGamma().divide(BigDecimal.valueOf(underlying.getContractSize()), 4, RoundingMode.HALF_UP));
                    result.setGammaCash(quoteSoResultVO.getGamma().multiply(underlying.getLastPrice()).multiply(underlying.getLastPrice()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                    result.setVega(quoteSoResultVO.getVega().setScale(4, RoundingMode.HALF_UP));
                    result.setTheta(quoteSoResultVO.getTheta().setScale(4, RoundingMode.HALF_UP));
                    result.setRho(quoteSoResultVO.getRho().setScale(4, RoundingMode.HALF_UP));
                    result.setDividendRho(quoteSoResultVO.getDividendRho().setScale(4, RoundingMode.HALF_UP));
                    result.setAvailableAmount(result.getAvailablePremium());
                }
                result.setAccumulatedPosition(quoteSoResultVO.getAccumulatedPosition());
                result.setAccumulatedPayment(quoteSoResultVO.getAccumulatedPayment());
                result.setAccumulatedPnl(quoteSoResultVO.getAccumulatedPnl());
                break;
            //雪球期权
            case AILimitLossesSnowBallCallPricer:
            case AILimitLossesSnowBallPutPricer:
            case AISnowBallCallPricer:
            case AISnowBallPutPricer:
            case AIBreakEvenSnowBallCallPricer:
            case AIBreakEvenSnowBallPutPricer:

                delta = quoteSoResultVO.getDelta().multiply(tradeMngVo.getAvailableNotionalPrincipal())
                        .divide(tradeMngVo.getEntryPrice(), 4, RoundingMode.HALF_UP);
                //Delta*存续名义本金/期初标的价格/合约乘数
                deltaLots = delta.divide(BigDecimal.valueOf(underlying.getContractSize()), 4, RoundingMode.HALF_UP);
                //deltaCash=DeltaLots*期货当前价格*合约乘数.
                deltaCash = deltaLots.multiply(underlying.getLastPrice()).multiply(BigDecimal.valueOf(underlying.getContractSize()));
                gamma = quoteSoResultVO.getGamma().multiply(tradeMngVo.getAvailableNotionalPrincipal())
                        .divide(tradeMngVo.getEntryPrice().multiply(tradeMngVo.getEntryPrice()), 4, RoundingMode.HALF_UP);
                //Gamma*存续名义本金/合约乘数/期初标的价格^2*符号
                gammaLots = gamma.divide(BigDecimal.valueOf(underlying.getContractSize()), 4, RoundingMode.HALF_UP);
                //Gamma*存续名义本金*(当前标的价格/期初标的价格)^2/100*符号
                gammaCash = quoteSoResultVO.getGamma().multiply(tradeMngVo.getAvailableNotionalPrincipal())
                        .multiply(underlying.getLastPrice().divide(tradeMngVo.getEntryPrice(), 16, RoundingMode.HALF_UP))
                        .multiply(underlying.getLastPrice().divide(tradeMngVo.getEntryPrice(), 16, RoundingMode.HALF_UP))
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                //so计算出的vega*名义本金
                vega = quoteSoResultVO.getVega().multiply(tradeMngVo.getAvailableNotionalPrincipal()).setScale(4, RoundingMode.HALF_UP);
                //so计算出的theta*名义本金
                theta = quoteSoResultVO.getTheta().multiply(tradeMngVo.getAvailableNotionalPrincipal()).setScale(4, RoundingMode.HALF_UP);
                rho = quoteSoResultVO.getRho().multiply(tradeMngVo.getAvailableNotionalPrincipal()).setScale(4, RoundingMode.HALF_UP);
                dividendRho = quoteSoResultVO.getDividendRho().multiply(tradeMngVo.getAvailableNotionalPrincipal()).setScale(4, RoundingMode.HALF_UP);
                if (tradeMngVo.getBuyOrSell() == BuyOrSellEnum.buy) {
                    result.setDelta(delta.negate());
                    result.setDeltaLots(deltaLots.negate());
                    result.setDeltaCash(deltaCash.negate());
                    result.setGamma(gamma.negate());
                    result.setGammaLots(gammaLots.negate());
                    result.setGammaCash(gammaCash.negate());
                    result.setVega(vega.negate());
                    result.setTheta(theta.negate());
                    result.setRho(rho.negate());
                    result.setDividendRho(dividendRho.negate());
                    result.setAvailablePremium(quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP).negate());
                    result.setAvailableAmount(quoteSoResultVO.getPv().negate().multiply(result.getAvailableNotionalPrincipal()).setScale(2, RoundingMode.HALF_UP));
                } else {
                    result.setDelta(delta);
                    result.setDeltaLots(deltaLots);
                    result.setDeltaCash(deltaCash);
                    result.setGamma(gamma);
                    result.setGammaLots(gammaLots);
                    result.setGammaCash(gammaCash);
                    result.setVega(vega);
                    result.setTheta(theta);
                    result.setRho(rho);
                    result.setDividendRho(dividendRho);
                    result.setAvailablePremium(quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP));
                    result.setAvailableAmount(quoteSoResultVO.getPv().multiply(result.getAvailableNotionalPrincipal()).setScale(2, RoundingMode.HALF_UP));
                }
                break;
            default:
                delta = quoteSoResultVO.getDelta().multiply(tradeMngVo.getAvailableVolume()).setScale(4, RoundingMode.HALF_UP);
                deltaLots = quoteSoResultVO.getDelta().multiply(tradeMngVo.getAvailableVolume()).divide(BigDecimal.valueOf(underlying.getContractSize()), 4, RoundingMode.HALF_UP);
                deltaCash = quoteSoResultVO.getDelta().multiply(tradeMngVo.getAvailableVolume()).multiply(underlying.getLastPrice()).setScale(4, RoundingMode.HALF_UP);
                gamma = quoteSoResultVO.getGamma().multiply(tradeMngVo.getAvailableVolume()).setScale(4, RoundingMode.HALF_UP);

                gammaLots = quoteSoResultVO.getGamma().multiply(tradeMngVo.getAvailableVolume()).divide(BigDecimal.valueOf(underlying.getContractSize()), 4, RoundingMode.HALF_UP);
                gammaCash = quoteSoResultVO.getGamma().multiply(underlying.getLastPrice())
                        .multiply(underlying.getLastPrice()).multiply(tradeMngVo.getAvailableVolume()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                vega = quoteSoResultVO.getVega().multiply(tradeMngVo.getAvailableVolume()).setScale(4, RoundingMode.HALF_UP);
                theta = quoteSoResultVO.getTheta().multiply(tradeMngVo.getAvailableVolume()).setScale(4, RoundingMode.HALF_UP);
                rho = quoteSoResultVO.getRho().multiply(tradeMngVo.getAvailableVolume()).setScale(4, RoundingMode.HALF_UP);
                dividendRho = quoteSoResultVO.getDividendRho().multiply(tradeMngVo.getAvailableVolume()).setScale(4, RoundingMode.HALF_UP);
                if (tradeMngVo.getBuyOrSell() == BuyOrSellEnum.buy) {
                    result.setDelta(delta.negate());
                    result.setDeltaLots(deltaLots.negate());
                    result.setDeltaCash(deltaCash.negate());
                    result.setGamma(gamma.negate());
                    result.setGammaLots(gammaLots.negate());
                    result.setGammaCash(gammaCash.negate());
                    result.setVega(vega.negate());
                    result.setTheta(theta.negate());
                    result.setRho(rho.negate());
                    result.setDividendRho(dividendRho.negate());
                    result.setAvailablePremium(quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP).negate());
                } else {
                    result.setDelta(delta);
                    result.setDeltaLots(deltaLots);
                    result.setDeltaCash(deltaCash);
                    result.setGamma(gamma);
                    result.setGammaLots(gammaLots);
                    result.setGammaCash(gammaCash);
                    result.setVega(vega);
                    result.setTheta(theta);
                    result.setRho(rho);
                    result.setDividendRho(dividendRho);
                    result.setAvailablePremium(quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP));
                }
                result.setAvailableAmount(result.getAvailablePremium().multiply(result.getAvailableVolume()).setScale(2, RoundingMode.HALF_UP));
                break;
        }
        //今日盈亏
        setTodayPnl(result);
    }

    /**
     * 组装计算后的场内期权数据
     * @param result              风险计算结果
     * @param quoteSoResultVO     so计算结果
     * @param exchangeRealTimePos 场内期权详情
     * @param underlying          合约
     * @param midVol              mid波动率
     * @param expireDate          合约失效时间
     */
    private void handleExchangeOptionTradeRiskCacularResult(TradeRiskCacularResult result, QuoteSoResultVO quoteSoResultVO, ExchangeRealTimePos exchangeRealTimePos
            , UnderlyingByRisk underlying, BigDecimal midVol, LocalDate expireDate) {
        BigDecimal pos = BigDecimal.valueOf(exchangeRealTimePos.getPosition());
        ExchangeEodType type = "2".equals(exchangeRealTimePos.getPosiDirection()) ? ExchangeEodType.LONG : ExchangeEodType.SHORT;
        BigDecimal delta = quoteSoResultVO.getDelta().multiply(pos).multiply(BigDecimal.valueOf(underlying.getContractSize())).setScale(4, RoundingMode.HALF_UP);
        BigDecimal deltaLots = quoteSoResultVO.getDelta().multiply(pos).setScale(4, RoundingMode.HALF_UP);
        BigDecimal deltaCash = quoteSoResultVO.getDelta().multiply(pos).multiply(BigDecimal.valueOf(underlying.getContractSize())).multiply(underlying.getLastPrice()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal gamma = quoteSoResultVO.getGamma().multiply(pos).multiply(BigDecimal.valueOf(underlying.getContractSize())).setScale(4, RoundingMode.HALF_UP);
        BigDecimal gammaLots = quoteSoResultVO.getGamma().multiply(pos).setScale(4, RoundingMode.HALF_UP);
        BigDecimal gammaCash = quoteSoResultVO.getGamma().multiply(underlying.getLastPrice().multiply(underlying.getLastPrice())).multiply(pos)
                .multiply(BigDecimal.valueOf(underlying.getContractSize())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal vega = quoteSoResultVO.getVega().multiply(pos).multiply(BigDecimal.valueOf(underlying.getContractSize())).setScale(4, RoundingMode.HALF_UP);
        BigDecimal theta = quoteSoResultVO.getTheta().multiply(pos).multiply(BigDecimal.valueOf(underlying.getContractSize())).setScale(4, RoundingMode.HALF_UP);
        if (type == ExchangeEodType.SHORT) {
            result.setDelta(delta.negate());
            result.setDeltaLots(deltaLots.negate());
            result.setDeltaCash(deltaCash.negate());
            result.setGamma(gamma.negate());
            result.setGammaLots(gammaLots.negate());
            result.setGammaCash(gammaCash.negate());
            result.setVega(vega.negate());
            result.setTheta(theta.negate());
        } else {
            result.setDelta(delta);
            result.setDeltaLots(deltaLots);
            result.setDeltaCash(deltaCash);
            result.setGamma(gamma);
            result.setGammaLots(gammaLots);
            result.setGammaCash(gammaCash);
            result.setVega(vega);
            result.setTheta(theta);
        }
        result.setExchangeUnderlyingCode(underlying.getExchangeUnderlyingCode());
        result.setAvailablePremium(type == ExchangeEodType.LONG ? quoteSoResultVO.getPv().setScale(2, RoundingMode.HALF_UP)
                : quoteSoResultVO.getPv().multiply(BigDecimal.valueOf(-1)).setScale(2, RoundingMode.HALF_UP));
        result.setAvailableVolume(pos.multiply(BigDecimal.valueOf(underlying.getContractSize())));
        result.setLastPrice(underlying.getLastPrice());
        result.setMaturityDate(expireDate);
        result.setNowVol(midVol);
        result.setAvailableAmount(result.getAvailablePremium().multiply(result.getAvailableVolume()).setScale(2, RoundingMode.HALF_UP));

        setTodayPnl(result);
    }

    /**
     * 当前带符号的期权（或期货）估值（也就是存续总额）- 上个交易日收盘时的带符号的期权（或期货）估值（存续总额）- 今日买入带符号的成交总额 - 今日卖出带符号的成交总额 设置todayPnl
     * @param tradeRiskCacularResult 计算结果
     */
    private void setTodayPnl(TradeRiskCacularResult tradeRiskCacularResult) {

        TradeRiskCacularResult lastTradeDayRiskInfo = lastRiskInfoMap.get(tradeRiskCacularResult.getId());
        BigDecimal lastTradeDayAvailableAmount = BigDecimal.ZERO;
        BigDecimal lastTotalPnl = BigDecimal.ZERO;
        if (Objects.nonNull(lastTradeDayRiskInfo)) {
            lastTotalPnl = lastTradeDayRiskInfo.getTotalProfitLoss();
            if (lastTradeDayRiskInfo.getRiskDate().isEqual(lastTradeDay)) {
                lastTradeDayAvailableAmount = lastTradeDayRiskInfo.getAvailableAmount();
                //存续累计期权今日的头寸、赔付、盈亏=今日-昨日
                if (tradeRiskCacularResult.getAvailableVolume().compareTo(BigDecimal.ZERO) != 0) {
                    tradeRiskCacularResult.setTodayAccumulatedPosition(tradeRiskCacularResult.getAccumulatedPosition()
                            .subtract(lastTradeDayRiskInfo.getAccumulatedPosition() == null ? BigDecimal.ZERO : lastTradeDayRiskInfo.getAccumulatedPosition()));
                    tradeRiskCacularResult.setTodayAccumulatedPayment(tradeRiskCacularResult.getAccumulatedPayment()
                            .subtract(lastTradeDayRiskInfo.getAccumulatedPayment() == null ? BigDecimal.ZERO : lastTradeDayRiskInfo.getAccumulatedPayment()));
                    tradeRiskCacularResult.setTodayAccumulatedPnl(tradeRiskCacularResult.getAccumulatedPnl().
                            subtract(lastTradeDayRiskInfo.getAccumulatedPnl() == null ? BigDecimal.ZERO : lastTradeDayRiskInfo.getAccumulatedPnl()));
                } else {
                    //已平仓累计期权今日的头寸、赔付、盈亏=0,并且累计部分等于昨日的头寸
                    tradeRiskCacularResult.setAccumulatedPosition(lastTradeDayRiskInfo.getAccumulatedPosition() == null ? BigDecimal.ZERO : lastTradeDayRiskInfo.getAccumulatedPosition());
                    tradeRiskCacularResult.setAccumulatedPayment(lastTradeDayRiskInfo.getAccumulatedPayment() == null ? BigDecimal.ZERO : lastTradeDayRiskInfo.getAccumulatedPayment());
                    tradeRiskCacularResult.setAccumulatedPnl(lastTradeDayRiskInfo.getAccumulatedPnl() == null ? BigDecimal.ZERO : lastTradeDayRiskInfo.getAccumulatedPnl());
                }
            }
        } else {
            tradeRiskCacularResult.setTodayAccumulatedPosition(tradeRiskCacularResult.getAccumulatedPosition());
            tradeRiskCacularResult.setTodayAccumulatedPayment(tradeRiskCacularResult.getAccumulatedPayment());
            tradeRiskCacularResult.setTodayAccumulatedPnl(tradeRiskCacularResult.getAccumulatedPnl());
            if (tradeRiskCacularResult.getAvailableVolume().compareTo(BigDecimal.ZERO) == 0) {
                tradeRiskCacularResult.setAccumulatedPosition(BigDecimal.ZERO);
                tradeRiskCacularResult.setAccumulatedPayment(BigDecimal.ZERO);
                tradeRiskCacularResult.setAccumulatedPnl(BigDecimal.ZERO);
                tradeRiskCacularResult.setTodayAccumulatedPosition(BigDecimal.ZERO);
                tradeRiskCacularResult.setTodayAccumulatedPayment(BigDecimal.ZERO);
                tradeRiskCacularResult.setTodayAccumulatedPnl(BigDecimal.ZERO);
            }
        }

        tradeRiskCacularResult.setLastTradeDayAvailableAmount(lastTradeDayAvailableAmount);
        tradeRiskCacularResult.setTodayProfitLoss(tradeRiskCacularResult.getAvailableAmount()
                .subtract(lastTradeDayAvailableAmount)
                .subtract(tradeRiskCacularResult.getTodayOpenAmount())
                .subtract(tradeRiskCacularResult.getTodayCloseAmount()));
        tradeRiskCacularResult.setTotalProfitLoss(lastTotalPnl.add(tradeRiskCacularResult.getTodayProfitLoss()));
    }

    /**
     * 初始化场内期货
     * @param result              风险计算结果
     * @param exchangeRealTimePos 场内期货详情
     * @param evaluationTime      计算时间
     */
    private void initExchangeTradeRiskResult(TradeRiskCacularResult result, ExchangeRealTimePos exchangeRealTimePos, long evaluationTime, UnderlyingByRisk underlyingByRisk) {
        ExchangeEodType type = "2".equals(exchangeRealTimePos.getPosiDirection()) ? ExchangeEodType.LONG : ExchangeEodType.SHORT;
        result.setId(exchangeRealTimePos.getInvestorID() + "_" + exchangeRealTimePos.getInstrumentID() + "_" + type.name());
        result.setAssetId(exchangeRealTimePos.getAssetId());
        result.setInvestorId(exchangeRealTimePos.getInvestorID());
        result.setTradeRiskCacularResultSourceType(TradeRiskCacularResultSourceType.exchange);
        result.setTradeRiskCacularResultType(exchangeRealTimePos.getOptionsType() == 0 ? TradeRiskCacularResultType.european : TradeRiskCacularResultType.option);
        //合约代码
        result.setUnderlyingCode(exchangeRealTimePos.getUnderlyingCode().toUpperCase());
        //期权代码
        result.setInstrumentId(exchangeRealTimePos.getInstrumentID());
        result.setDay1PnL(exchangeRealTimePos.getDay1PnL());
        result.setBuyOrSell(type == ExchangeEodType.LONG ? BuyOrSellEnum.buy : BuyOrSellEnum.sell);
        //期权部分
        if (exchangeRealTimePos.getOptionsType() != 0) {
            result.setCallOrPut(exchangeRealTimePos.getOptionsType() == 1 ? CallOrPutEnum.call : CallOrPutEnum.put);
            result.setMaturityDate(LocalDate.parse(exchangeRealTimePos.getExpireDate(), DateTimeFormatter.ofPattern("yyyyMMdd")));
            result.setNowVol(BigDecimal.ZERO);
            result.setOptionType(OptionTypeEnum.AIVanillaPricer);
            result.setStrike(exchangeRealTimePos.getStrikePrice());
        }
        result.setCacularTime(evaluationTime);
        result.setLastPrice(BigDecimal.ZERO);

        initResultInfo(result, underlyingByRisk);
        /*空头开仓、多头平仓是卖出->我方卖出为负
        空头平仓、多头开仓是买入->我方买入为正
        * */
        if (type == ExchangeEodType.SHORT) {
            //空头：开仓为负数 平仓为正数
            result.setTodayOpenAmount(result.getTodayOpenAmount().negate());
            result.setTodayCloseAmount(result.getTodayCloseAmount());
        } else {
            //多头: 开仓是正数 平仓为负数
            result.setTodayOpenAmount(result.getTodayOpenAmount());
            result.setTodayCloseAmount(result.getTodayCloseAmount().negate());
        }

    }

    /**
     * 组装计算后的场内期货数据
     * @param result              风险计算结果
     * @param exchangeRealTimePos 场内期货详情
     * @param underlying          合约
     */
    private void handleExchangeEuropeanTradeRiskCacularResult(TradeRiskCacularResult result, ExchangeRealTimePos exchangeRealTimePos
            , UnderlyingByRisk underlying) {
        ExchangeEodType type = "2".equals(exchangeRealTimePos.getPosiDirection()) ? ExchangeEodType.LONG : ExchangeEodType.SHORT;
        result.setExchangeUnderlyingCode(underlying.getExchangeUnderlyingCode());
        result.setAvailablePremium(type == ExchangeEodType.LONG ? underlying.getLastPrice() : underlying.getLastPrice().multiply(BigDecimal.valueOf(-1)));
        result.setAvailableVolume(BigDecimal.valueOf(exchangeRealTimePos.getPosition()).multiply(BigDecimal.valueOf(underlying.getContractSize())));
        result.setAvailableAmount(result.getAvailablePremium().multiply(result.getAvailableVolume()).setScale(2, RoundingMode.HALF_UP));

        result.setStatus(SuccessStatusEnum.success);
        result.setLastPrice(underlying.getLastPrice());
        result.setCacularTime(underlying.getEvaluationTime());
        result.setDeltaLots(type == ExchangeEodType.LONG ? BigDecimal.valueOf(exchangeRealTimePos.getPosition()).multiply(BigDecimal.valueOf(underlying.getContractSize())) :
                BigDecimal.valueOf(exchangeRealTimePos.getPosition()).multiply(BigDecimal.valueOf(underlying.getContractSize())).negate());
        result.setDeltaLots(type == ExchangeEodType.LONG ? BigDecimal.valueOf(exchangeRealTimePos.getPosition()) :
                BigDecimal.valueOf(exchangeRealTimePos.getPosition()).negate());
        result.setDeltaCash((result.getDeltaLots().multiply(BigDecimal.valueOf(underlying.getContractSize()))
                .multiply(result.getAvailablePremium()).setScale(2, RoundingMode.HALF_UP))
                .multiply(type == ExchangeEodType.LONG ? BigDecimal.valueOf(1) : BigDecimal.valueOf(-1)));
        setTodayPnl(result);
    }

    /**
     * 将表中so观察日期的数据转为so需要的数据
     */
    private List<ObserveSchedule> handleObsDate(List<TradeObsDateVO> tradeObsDateList) {

        List<ObserveSchedule> observeScheduleList = new ArrayList<>();
        BussinessException.E_300101.assertTrue(Objects.nonNull(tradeObsDateList) && !tradeObsDateList.isEmpty(), "观察日期不能为空");
        for (TradeObsDateVO tradeObsDateVO : tradeObsDateList) {
            ObserveSchedule observeSchedule = new ObserveSchedule();
            observeSchedule.setObserveDate(tradeObsDateVO.getObsDate().atTime(15, 0, 0).toInstant(ZoneOffset.ofHours(8)).getEpochSecond());
            if (Objects.isNull(tradeObsDateVO.getPrice())) {
                observeSchedule.setFixedPrice(0);
            } else {
                observeSchedule.setFixedPrice(tradeObsDateVO.getPrice().doubleValue());
            }
            observeScheduleList.add(observeSchedule);
        }
        return observeScheduleList;
    }

    /**
     * 获取定价时间戳
     */
    private long getEvaluationTime() {
        //获取定价时间戳
        long evaluationTime = LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
        if (Objects.nonNull(riskTime)) {
            if (riskTime > evaluationTime) {
                evaluationTime = riskTime;
            }
        }
        return evaluationTime;
    }
}

