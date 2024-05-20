package org.orient.otc.yl.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Enums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.*;
import org.orient.otc.api.dm.dto.CalendarPropertyQueryDto;
import org.orient.otc.api.dm.dto.CalendarStartEndDto;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.feign.VarietyClient;
import org.orient.otc.api.dm.vo.CalendarProperty;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.dto.MarketCloseDataSaveDto;
import org.orient.otc.api.market.dto.MarketCodeDto;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.quote.dto.SyncUpdateDto;
import org.orient.otc.api.quote.feign.TradeMngClient;
import org.orient.otc.api.quote.feign.TradeMngCloseClient;
import org.orient.otc.api.quote.feign.TradeRiskInfoClient;
import org.orient.otc.api.quote.vo.TradeCloseMngFeignVo;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.quote.vo.TradeRiskPVInfoVO;
import org.orient.otc.api.user.feign.AssetUnitClient;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.dictionary.utils.DictionaryTransformer;
import org.orient.otc.common.security.exception.BussinessException;
import org.orient.otc.yl.dto.*;
import org.orient.otc.yl.entity.ExtendData;
import org.orient.otc.yl.entity.MetaDic;
import org.orient.otc.yl.enums.*;
import org.orient.otc.yl.service.OrderService;
import org.orient.otc.yl.service.SyncServe;
import org.orient.otc.yl.service.YlService;
import org.orient.otc.yl.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.orient.otc.yl.enums.OptionTypeEnum.*;


/**
 * @author dzrh
 */
@Service
@Slf4j
public class SyncServeImpl implements SyncServe {

    @Resource
    private YlService ylService;

    @Resource
    private OrderService orderService;
    @Resource
    private UnderlyingManagerClient underlyingManagerClient;
    @Resource
    private VarietyClient varietyClient;

    @Resource
    private ClientClient clientClient;

    @Resource
    private TradeMngClient tradeMngClient;

    @Resource
    private TradeMngCloseClient tradeMngCloseClient;

    @Resource
    private AssetUnitClient assetUnitClient;

    @Resource
    private CalendarClient calendarClient;

    @Resource
    private MarketClient marketClient;
    @Resource
    private UserClient userClient;

    @Resource
    private TradeRiskInfoClient tradeRiskInfoClient;
    @Resource
    private DictionaryTransformer dictionaryTransformer;
    /**
     * 从镒链同步合约信息
     * @return true 同步成功 false同步失败
     */
    @Override
    public Boolean syncUnderlying() {
        List<UnderlyingVo> voList = ylService.getUnderlyingList();
        Map<String, Integer> varietyMap = varietyClient.getVarietyMap();
        List<UnderlyingManagerVO> list = CglibUtil.copyList(voList, UnderlyingManagerVO::new, (vo, db) -> {
            db.setUnderlyingCode(vo.getCode());
            if ("CFFEX".equals(vo.getExchange()) || "CZCE".equals(vo.getExchange())) {
                db.setExchangeUnderlyingCode(vo.getCode());
            } else {
                db.setExchangeUnderlyingCode(vo.getCode().toLowerCase());
            }
            db.setUnderlyingName(vo.getName());
            db.setVarietyId(varietyMap.get(vo.getProductClass()));
            db.setUpDownLimit(BigDecimalUtil.bigDecimalToPercentage(vo.getUpDownLimit()));
            db.setPriceTick(vo.getPriceTick());
            db.setContractSize(vo.getMultiple());

        });
        return underlyingManagerClient.saveBatch(list);
    }

    /**
     * 从镒链同步客户信息
     * @return true 同步成功 false同步失败
     */
    @Override
    public Boolean syncClient() {
        // 获取镒链的客户信息列表
        List<ClientInfoVo> voList = ylService.getClientInfoList();
        // 并行获取客户详细信息
        List<ClientInfoDetailAllVo> detailAllVos = voList.parallelStream()
                .map(vo -> {
                    ClientInfoDetailDto dto = new ClientInfoDetailDto();
                    dto.setClientNumber(vo.getClientNumber());
                    ClientInfoDetailAllVo clientInfo = ylService.getClientInfo2(dto);
                    clientInfo.getClientInfo().setCode(vo.getClientNumber());
                    return clientInfo;
                }).collect(Collectors.toList());
        // 初始化用于存储人员信息、银行卡信息和客户详细信息的列表
        List<DutyInfoVo> dutiesInfoList = new ArrayList<>();
        List<BankCardInfoYLVO> bankCardInfoList = new ArrayList<>();
        List<ClientInfoDetailVo> dbList = new ArrayList<>();
        detailAllVos.forEach(detailAllVo -> {
            ClientInfoDetailVo detailVo = detailAllVo.getClientInfo();
            MetaDicVo metaDic = detailVo.getMetaDic();
            if(metaDic!=null){
                detailVo.setActualBeneficiaryLicenseCodeDate(metaDic.getActualBeneficiaryLicenseCodeDate());
                detailVo.setReportName(metaDic.getReportName());
            }
            detailVo.setTransactionTarget(TransactionTarget.convertRolesToKeys(detailVo.getTransactionTargetList()));
            // 假设properClientClass属性是一个String类型
            String currentClass = detailVo.getProperClientClass(); // 获取当前值
            // 根据当前值设置新值
            if ("普通".equals(currentClass)) {
                detailVo.setProperClientClass("普通投资者"); // 如果当前值为"普通"，设置为"普通投资者"
            } else if ("专业".equals(currentClass)) {
                detailVo.setProperClientClass("专业投资者"); // 如果当前值为"专业"，设置为"专业投资者"
            }
            dbList.add(detailVo);
            // 提取并设置关联的人员信息
            dutiesInfoList.addAll(detailAllVo.getDuties().stream()
                    .peek(duty -> duty.setClientId(detailVo.getId()))
                    .collect(Collectors.toList()));
            // 提取并设置关联的银行卡信息
            bankCardInfoList.addAll(detailAllVo.getBankCards().stream()
                    .peek(bankCard -> bankCard.setClientId(detailVo.getId()))
                    .collect(Collectors.toList()));
        });
        //字段字典值转换
        dictionaryTransformer.transform2ValueList(dbList);
        ClientInfoListVo clientInfoListVo = new ClientInfoListVo();
        clientInfoListVo.setBankCardInfoList(bankCardInfoList);
        clientInfoListVo.setDutiesInfoList(dutiesInfoList);
        clientInfoListVo.setDbList(dbList);
        // 调用客户信息同步方法
        clientClient.syncByYl(clientInfoListVo);
        return true;
    }


    @Override
    public void syncMarketCloseData(LocalDate startDate, Boolean isOnlyToday) {
        do {
            EodPricesDto eodPricesDto = new EodPricesDto();
            eodPricesDto.setValueDate(startDate);
            List<EodPricesVo> list = ylService.getEodPricesList(eodPricesDto);
            if (!list.isEmpty()) {

                LocalDate finalStartDate = startDate;
                marketClient.loadYlCloseMarketData(CglibUtil.copyList(list, MarketCloseDataSaveDto::new, (vo, db) -> {
                    db.setTradingDay(finalStartDate.format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN)));
                    db.setInstrumentID(vo.getCode());
                    db.setLowestPrice(vo.getLow());
                    db.setHighestPrice(vo.getHigh());
                    db.setClosePrice(vo.getClose());
                    db.setSettlementPrice(vo.getSettle());
                }));
            }
            startDate = startDate.minusDays(-1);
        } while (startDate.isBefore(LocalDate.now()) && !isOnlyToday);
    }

    @Override
    public String syncClientPosition() {
        Map<String, Integer> assetMap = assetUnitClient.getAssetUnitList(new HashSet<>()).stream()
                .collect(Collectors.toMap(AssetunitVo::getName, AssetunitVo::getId));

        Map<String, Integer> traderMap = userClient.getUserList().stream()
                .collect(Collectors.toMap(UserVo::getName, UserVo::getId));
        ClientPositionDto clientPositionDto = new ClientPositionDto();
        clientPositionDto.setPageIndex(1);
        clientPositionDto.setPageSize(500);
        boolean haveNext;
        do {
            PageInfo<ClientPositionVo> pageInfo = ylService.getClientPositionListV3(clientPositionDto);
            haveNext = pageInfo.getPage() < pageInfo.getTotalPages();
            clientPositionDto.setPageIndex(clientPositionDto.getPageIndex() + 1);
            List<ClientPositionVo> list = pageInfo.getRows();
            for (ClientPositionVo clientPositionVo : list) {
                this.syncTradeInfoByTradeCode(assetMap, traderMap, clientPositionVo.getTradeNumber());
            }
        } while (haveNext);

        return "同步成功";
    }

    @Override
    public void syncTradeInfoByTradeCode(Map<String, Integer> assetMap, Map<String, Integer> traderMap, String tradeNumber) {
        //获取交易详情
        OpenCloseInfoVo openCloseInfoVo = orderService.tradeOpenCloseInfos(OpenCloseInfoDto.builder()
                .tradeNumber(tradeNumber).build());
        OpenInfoVo t = openCloseInfoVo.getOpenInfo();
        TradeMngByYlVo vo = new TradeMngByYlVo();
        //获取期权类型
        OptionTypeEnum tradeTypeEnum = getTradeTypeByDesc(openCloseInfoVo.getOpenInfo().getTradeType());
        //获取不到期权类型或者是自定义交易时
        if(tradeTypeEnum == null) {
            log.error("同步交易记录异常,期权类型:{},结构类型:{},详情内容:{}"
                    , openCloseInfoVo.getOpenInfo().getTradeType(), openCloseInfoVo.getOpenInfo().getStructureType()
                    , JSONObject.toJSONString(openCloseInfoVo));
            return;
        }else if (tradeTypeEnum.equals(AICustomPricer)) {
            //自定义交易转换为标准产品
            OptionTypeEnum tempTradeType = getTradeTypeByDesc(openCloseInfoVo.getOpenInfo().getStructureType());
            if (tempTradeType!=null){
                tradeTypeEnum=tempTradeType;
            }else {
                //找不到对应自定义交易时,将镒链的自定义交易存储到系统
                vo.setStructureType(t.getStructureType());
                vo.setExtendInfo(t.getExtendInfo());
            }
        }

        //期权类型
        vo.setOptionType(tradeTypeEnum);
        //看涨看跌
        CallOrPutEnum callOrPutEnum = CallOrPutEnum.getTradeTypeByDesc(t.getCallPut());
        vo.setCallOrPut(callOrPutEnum);
        //设置组合相关属性
        if (StringUtils.isNotBlank(t.getStructureType())
                && !"自由组合".equals(t.getStructureType())
                && !"远期".equals(t.getStructureType())
                && !"自定义交易".equals(t.getTradeType())) {
            OptionCombTypeEnum optionCombTypeEnum = OptionCombTypeEnum.getTradeTypeByDesc(t.getStructureType());
            //无匹配的组合数据忽略不作迁移
            if (optionCombTypeEnum == null) {
                log.error("同步交易记录异常,期权类型:{},结构类型:{},详情内容:{}", t.getTradeType(), t.getStructureType(), JSONObject.toJSONString(t));
                return;
            }
            vo.setOptionCombType(optionCombTypeEnum);
            if ("三领口组合".equals(t.getStructureType())) {
                if (CallOrPutEnum.call.equals(callOrPutEnum)) {
                    vo.setOptionCombType(OptionCombTypeEnum.callTriCollar);
                } else {
                    vo.setOptionCombType(OptionCombTypeEnum.putTriCollar);
                }
            }
            try {
                vo.setCombCode(t.getTradeNumber().substring(0, t.getTradeNumber().lastIndexOf("-")));
                vo.setSort(Integer.parseInt(t.getTradeNumber().substring(t.getTradeNumber().lastIndexOf("-") + 1)) + 1);
            } catch (Exception e) {
                vo.setSort(1);
                vo.setCombCode(t.getTradeNumber());
                log.error("同步交易记录异常,交易编号:{},详情内容:{}", t.getTradeNumber(), JSONObject.toJSONString(t));
            }
        } else {
            vo.setSort(1);
            vo.setCombCode(t.getTradeNumber());
        }
        //簿记ID
        vo.setAssetId(assetMap.get(t.getAssetBookName()));
        //交易员
        String traderName = t.getTraderName();
        if (traderMap.get(traderName) != null) {
            vo.setTraderId(traderMap.get(traderName));
        } else {
            log.warn("交易编号：{}对应的交易员：{}系统中不存在，默认系统交易员，请检查交易是否正常", t.getTradeAmount(), t.getTraderName());
            vo.setTraderId(1);
        }
        //设置交易基础信息
        setBaseTradeInfo(t, vo);
        //设置雪球期权相关信息
        if ("雪球期权".equals(openCloseInfoVo.getOpenInfo().getTradeType())) {
            setSnowBallInfo(openCloseInfoVo.getOpenInfo(), vo);
        }
        //针对亚式处理
        if (vo.getOptionType() == AIAsianPricer) {
            if ("EnhancedArithmeticAverage".equals(t.getPayoffType())) {
                vo.setOptionType(AIEnAsianPricer);
                vo.setSettleType(SettleTypeEnum.physical);
            }
            vo.setStartObsDate(t.getAveragingPeriodStartDate().toLocalDate());
            //亚式期权的观察日处理
            if (vo.getOptionType() == AIEnAsianPricer
                    || vo.getOptionType() == AIAsianPricer) {
                if (vo.getStartObsDate() == null || vo.getMaturityDate() == null) {
                    log.warn("同步交易记录异常,交易编号:{},详情内容:{}", t.getTradeNumber(), JSONObject.toJSONString(t));
                    return;
                }
                MarketCodeDto marketCodeDto = new MarketCodeDto();
                marketCodeDto.setUnderlyingCode(vo.getUnderlyingCode());
                Map<String, BigDecimal> closePriceMap = marketClient.getAllCloseDatePriceByCode(marketCodeDto);
                CalendarStartEndDto calendarStartEndDto = new CalendarStartEndDto();
                calendarStartEndDto.setStartDate(vo.getStartObsDate());
                calendarStartEndDto.setEndDate(vo.getMaturityDate());
                List<LocalDate> dateList = calendarClient.getTradeDateList(calendarStartEndDto);
                vo.setTradeObsDateList(CglibUtil.copyList(dateList, TradeObsDateVO::new, (localDate, o) -> {
                    o.setObsDate(localDate);
                    o.setPrice(closePriceMap.get(o.getObsDate().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN))));
                    o.setUnderlyingCode(t.getUnderlyingCode());
                }));
                vo.setObsNumber(vo.getTradeObsDateList().size());
                vo.setBasicQuantity(vo.getTradeVolume().divide(BigDecimal.valueOf(vo.getObsNumber()), 4, RoundingMode.HALF_UP));
            }
        }

        //累计期权数据整理
        if (vo.getOptionType() == AICallAccPricer ||
                vo.getOptionType() == AIPutAccPricer ||
                vo.getOptionType() == AICallFixAccPricer ||
                vo.getOptionType() == AIPutFixAccPricer ||
                vo.getOptionType() == AIEnCallKOAccPricer ||
                vo.getOptionType() == AIEnPutKOAccPricer ||
                vo.getOptionType() == AICallKOAccPricer ||
                vo.getOptionType() == AIPutKOAccPricer ||
                vo.getOptionType() == AICallFixKOAccPricer ||
                vo.getOptionType() == AIPutFixKOAccPricer) {
            setAccOptionInfo(openCloseInfoVo.getOpenInfo(), vo);
        }
        //存在平仓记录
        if (!openCloseInfoVo.getCloseInfos().isEmpty()) {
            List<TradeCloseMngFeignVo> feignVoList = CglibUtil.copyList(openCloseInfoVo.getCloseInfos(), TradeCloseMngFeignVo::new, (a, b) -> {
                b.setSort(1);
                b.setTradeCode(vo.getTradeCode());
                b.setCloseVolume(a.getTcUnwindTradeAmount());
                b.setCloseDate(a.getTcValueDate().toLocalDate());
                b.setCloseEntryPrice(a.getTcFinalPrice());
                b.setClosePrice(a.getTcUnwindPrice());
                b.setProfitLoss(a.getWinLoss());
                b.setCloseTotalAmount(a.getTcAmount());
                b.setCloseNotionalPrincipal(a.getTcAmount().multiply(vo.getEntryPrice()));
            });
            //先更新交易记录的累计盈亏
            BigDecimal totalPnl = feignVoList.stream().map(TradeCloseMngFeignVo::getProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal closeTotalVolume = feignVoList.stream().map(TradeCloseMngFeignVo::getCloseVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal closeTotalNotionalPrincipal = feignVoList.stream().map(TradeCloseMngFeignVo::getCloseNotionalPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setTotalProfitLoss(totalPnl);
            vo.setAvailableVolume(vo.getTradeVolume().subtract(closeTotalVolume));
            vo.setAvailableNotionalPrincipal(vo.getNotionalPrincipal().subtract(closeTotalNotionalPrincipal));
            boolean isOk = tradeMngCloseClient.saveBatch(feignVoList);
            if (!isOk) {
                log.error("保存平仓记录失败:{}", vo.getTradeCode());
            }
        } else {
            //累计期权的存续数量已经在前面进行衰减了不再处理
            if (vo.getOptionType() != AICallAccPricer &&
                    vo.getOptionType() != AIPutAccPricer &&
                    vo.getOptionType() != AICallFixAccPricer &&
                    vo.getOptionType() != AIPutFixAccPricer) {
                vo.setAvailableVolume(vo.getTradeVolume());
            }
            vo.setTotalProfitLoss(BigDecimal.ZERO);
        }
        //将对象转换为quote的对象
        TradeMngVO tradeMngVO = CglibUtil.copy(vo, TradeMngVO.class);
        tradeMngVO.setOptionCombType(Enums.getIfPresent(org.orient.otc.api.quote.enums.OptionCombTypeEnum.class,
                vo.getOptionCombType() != null ? vo.getOptionCombType().name() : "").orNull());
        tradeMngVO.setOptionType(Enums.getIfPresent(org.orient.otc.api.quote.enums.OptionTypeEnum.class,
                vo.getOptionType() != null ? vo.getOptionType().name() : "").orNull());
        tradeMngVO.setExerciseType(Enums.getIfPresent(org.orient.otc.api.quote.enums.ExerciseTypeEnum.class,
                vo.getExerciseType() != null ? vo.getExerciseType().name() : "").orNull());
        tradeMngVO.setBuyOrSell(Enums.getIfPresent(org.orient.otc.api.quote.enums.BuyOrSellEnum.class,
                vo.getBuyOrSell() != null ? vo.getBuyOrSell().name() : "").orNull());
        tradeMngVO.setCallOrPut(Enums.getIfPresent(org.orient.otc.api.quote.enums.CallOrPutEnum.class,
                vo.getCallOrPut() != null ? vo.getCallOrPut().name() : "").orNull());
        tradeMngVO.setSettleType(Enums.getIfPresent(org.orient.otc.api.quote.enums.SettleTypeEnum.class,
                vo.getSettleType() != null ? vo.getSettleType().name() : "").orNull());
        tradeMngVO.setTradeState(Enums.getIfPresent(org.orient.otc.api.quote.enums.TradeStateEnum.class,
                vo.getTradeState() != null ? vo.getTradeState().name() : "").orNull());
        //保存交易记录
        Boolean result = tradeMngClient.saveTradeByYl(tradeMngVO);
        if (!result) {
            log.error("保存交易记录失败:{}", JSONObject.toJSONString(tradeMngVO));
        }
    }

    /**
     * 设置交易基础信息
     * @param t  镒链交易数据
     * @param vo 系统交易数据
     */
    private void setBaseTradeInfo(OpenInfoVo t, TradeMngByYlVo vo) {
        //同步状态
        vo.setIsSync(1);
        //交易编号
        vo.setTradeCode(t.getTradeNumber());
        //客户ID
        vo.setClientId(t.getClientId());
        //交易日期
        vo.setTradeDate(t.getTradeDate().toLocalDate());
        //到期日期
        vo.setMaturityDate(t.getExerciseDate().toLocalDate());
        //合约代码
        vo.setUnderlyingCode(t.getUnderlyingCode());
        //入场价格=期初标的价格
        vo.setEntryPrice(t.getInitialSpotPrice());
        //交易状态
        vo.setTradeState(TradeStateEnum.getTradeTypeByDesc(t.getTradeStatus()));
        //行权方式
        ExerciseTypeEnum exerciseTypeEnum = ExerciseTypeEnum.getTradeTypeByDesc(t.getExerciseMode());
        vo.setExerciseType(exerciseTypeEnum);
        //行权价格
        if(t.getIsMoneynessOptionData()){
            vo.setStrike(t.getStrike().multiply(t.getInitialSpotPrice()));
        }else {
            vo.setStrike(t.getStrike());
        }

        //买卖方向 镒链给的是交易员方向，需要转换为客户方向
        BuyOrSellEnum buySellEnum = BuyOrSellEnum.getTradeTypeByDesc(t.getBuySell());
        vo.setBuyOrSellDz(buySellEnum);
        //交易符号
        BigDecimal symbol = BigDecimal.ONE;
        if (BuyOrSellEnum.buy.equals(buySellEnum)) {
            vo.setBuyOrSell(BuyOrSellEnum.sell);
            symbol = symbol.negate();

        } else {
            vo.setBuyOrSell(BuyOrSellEnum.buy);
        }
        //期权价格
        vo.setOptionPremium(t.getTradeSinglePrice().multiply(symbol));
        //百分比单价 = 期权价格/入场价格
        vo.setOptionPremiumPercent((vo.getOptionPremium().divide(vo.getEntryPrice(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))));
        //成交总额
        vo.setTotalAmount(t.getTradePrice().multiply(symbol));
        //成交数量
        vo.setTradeVolume(t.getOriginalNotional());
        //存续数量
        vo.setAvailableVolume(t.getOriginalNotional());
        //存续名义本金
        vo.setAvailableNotionalPrincipal(t.getOriginalStockEqvNotional());
        //名义本金
        vo.setNotionalPrincipal(t.getOriginalStockEqvNotional());
        //初始保证金
        vo.setMargin(t.getInitialMargin());
        //交易波动率=成交波动率
        if (t.getTradeOpenVolatility() != null) {
            vo.setTradeVol(t.getTradeOpenVolatility().multiply(new BigDecimal("100")));
        }
        //设置日期信息
        CalendarPropertyQueryDto dto = new CalendarPropertyQueryDto();
        dto.setTradeDate(t.getTradeDate().toLocalDate());
        dto.setMaturityDate(t.getExerciseDate().toLocalDate());
        CalendarProperty calendarProperty = calendarClient.getCalendarProperty(dto);
        vo.setTtm(calendarProperty.getTtm());
        vo.setWorkday(calendarProperty.getWorkday());
        vo.setTradingDay(calendarProperty.getTradingDay());
        vo.setBankHoliday(calendarProperty.getBankHoliday());
        //处理Day1Pnl&&MidVol
        if (t.getMetaDic() != null) {
            vo.setDay1PnL(t.getMetaDic().getDay1Pnl());
            vo.setMidVol(t.getMetaDic().getMidVol());
        }
    }

    /**
     * 设置累计期权属性
     * @param openInfo 镒链开仓信息
     * @param vo       系统交易信息
     */
    private void setAccOptionInfo(OpenInfoVo openInfo, TradeMngByYlVo vo) {
        MarketCodeDto marketCodeDto = new MarketCodeDto();
        marketCodeDto.setUnderlyingCode(vo.getUnderlyingCode());
        Map<String, BigDecimal> closePriceMap = marketClient.getAllCloseDatePriceByCode(marketCodeDto);
        //处理观察日期
        Optional<ExtendData> extendDataOptional = openInfo.getPropertys().stream().filter(e -> "ObservationDates".equals(e.getName())).findFirst();
        if (extendDataOptional.isPresent()) {
            ExtendData extendData = extendDataOptional.get();
            try {
                String value = extendData.getValue();
                value = value.replace("/", "-").replace(" 15:00:00", "").replace("\t", ",").replace(",,", ",");
                List<String> observationDates = Arrays.asList(value.split(","));
                vo.setStartObsDate(LocalDate.parse(observationDates.get(0)));
                vo.setTradeObsDateList(CglibUtil.copyList(observationDates, TradeObsDateVO::new, (localDate, o) -> {
                    o.setObsDate(LocalDate.parse(localDate.replace("/", "-")));
                    o.setPrice(closePriceMap.get(o.getObsDate().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN))));
                    o.setUnderlyingCode(openInfo.getUnderlyingCode());
                }));
                vo.setObsNumber(vo.getTradeObsDateList().size());
                //每日数量=交易数量/观察次数
                vo.setBasicQuantity(vo.getTradeVolume().divide(BigDecimal.valueOf(vo.getObsNumber()), 4, RoundingMode.HALF_UP));
            } catch (Exception e) {
                log.warn("同步交易记录异常,ObservationDates:{},详情内容:{}", extendData.getValue(), JSONObject.toJSONString(openInfo));
                CalendarStartEndDto calendarStartEndDto = new CalendarStartEndDto();
                calendarStartEndDto.setStartDate(vo.getTradeDate());
                calendarStartEndDto.setEndDate(vo.getMaturityDate());
                List<LocalDate> dateList = calendarClient.getTradeDateList(calendarStartEndDto);
                vo.setTradeObsDateList(CglibUtil.copyList(dateList, TradeObsDateVO::new, (localDate, o) -> {
                    o.setObsDate(localDate);
                    o.setPrice(closePriceMap.get(o.getObsDate().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN))));
                    o.setUnderlyingCode(openInfo.getUnderlyingCode());
                }));
                vo.setStartObsDate(dateList.get(0));
                vo.setObsNumber(vo.getTradeObsDateList().size());
                vo.setBasicQuantity(vo.getTradeVolume().divide(BigDecimal.valueOf(vo.getObsNumber()), 4, RoundingMode.HALF_UP));
            }
            long count = vo.getTradeObsDateList().stream().filter(o -> o.getPrice() != null).count();
            //衰减存续数量
            vo.setAvailableVolume(vo.getTradeVolume().subtract(vo.getBasicQuantity().multiply(BigDecimal.valueOf(count))));
        }
        //处理多倍系数
        Optional<ExtendData> leverageOptional = openInfo.getPropertys().stream().filter(e -> "多倍系数".equals(e.getName())).findFirst();
        if (leverageOptional.isPresent()) {
            ExtendData leverage = leverageOptional.get();
            vo.setLeverage(new BigDecimal(leverage.getValue()));
        }
        //处理多倍系数
        Optional<ExtendData> fixedPaymentOptional = openInfo.getPropertys().stream().filter(e -> "固定赔付".equals(e.getName())).findFirst();
        if (fixedPaymentOptional.isPresent()) {
            ExtendData fixedPayment = fixedPaymentOptional.get();
            vo.setFixedPayment(new BigDecimal(fixedPayment.getValue()));
        }
        //处理strikeRamp
        Optional<ExtendData> strikeRampOptional = openInfo.getPropertys().stream().filter(e -> "StrikeRamp".equals(e.getName())).findFirst();
        if (strikeRampOptional.isPresent()) {
            ExtendData strikeRamp = strikeRampOptional.get();
            vo.setStrikeRamp(new BigDecimal(strikeRamp.getValue()));
        }
        //处理BarrierRamp
        Optional<ExtendData> barrierRampOptional = openInfo.getPropertys().stream().filter(e -> "BarrierRamp".equals(e.getName())).findFirst();
        if (barrierRampOptional.isPresent()) {
            ExtendData barrierRamp = barrierRampOptional.get();
            if (StringUtils.isNotBlank(barrierRamp.getValue())) {
                vo.setBarrierRamp(new BigDecimal(barrierRamp.getValue()));
            } else {
                vo.setBarrierRamp(BigDecimal.ZERO);
            }
        }
        //处理isCashSettle
        Optional<ExtendData> isCashSettleOptional = openInfo.getPropertys().stream().filter(e -> "isCashSettle".equals(e.getName())).findFirst();
        if (isCashSettleOptional.isPresent()) {
            ExtendData isCashSettle = isCashSettleOptional.get();
            if (StringUtils.isNotBlank(isCashSettle.getValue())) {
                vo.setSettleType(SettleTypeEnum.getSettleTypeByKey(isCashSettle.getValue()));
            }
        }
        //固定赔付累购
        if (vo.getOptionType() == AICallFixAccPricer) {
            Optional<ExtendData> barrierOptional = openInfo.getPropertys().stream().filter(e -> "固定赔付区间上沿".equals(e.getName())).findFirst();
            if (barrierOptional.isPresent()) {
                ExtendData barrier = barrierOptional.get();
                vo.setBarrier(new BigDecimal(barrier.getValue()));
            }
        }
        //固定赔付累沽
        if (vo.getOptionType() == AIPutFixAccPricer) {
            Optional<ExtendData> barrierOptional = openInfo.getPropertys().stream().filter(e -> "固定赔付区间下沿".equals(e.getName())).findFirst();
            if (barrierOptional.isPresent()) {
                ExtendData barrier = barrierOptional.get();
                vo.setBarrier(new BigDecimal(barrier.getValue()));
            }
        }
        if (vo.getOptionType() == AICallAccPricer
                || vo.getOptionType() == AIPutAccPricer) {
            Optional<ExtendData> barrierOptional = openInfo.getPropertys().stream().filter(e -> "累计敲出价格".equals(e.getName())).findFirst();
            if (barrierOptional.isPresent()) {
                ExtendData barrier = barrierOptional.get();
                if (StringUtils.isNotBlank(barrier.getValue())) {
                    vo.setBarrier(new BigDecimal(barrier.getValue()));
                } else {
                    vo.setBarrier(BigDecimal.ZERO);
                }
            }
        }
    }

    /**
     * 设置雪球期权相关信息
     * @param openInfo 镒链交易记录
     * @param vo       系统交易记录
     */
    private void setSnowBallInfo(OpenInfoVo openInfo, TradeMngByYlVo vo) {
        //转换为系统的期权类型
        switch (openInfo.getKIPayoffType()) {
            case 0:
                if (vo.getCallOrPut() == CallOrPutEnum.call) {
                    vo.setOptionType(AIBreakEvenSnowBallCallPricer);
                } else {
                    vo.setOptionType(AIBreakEvenSnowBallPutPricer);
                }
                break;
            case 1:
                vo.setOptionType(AISnowBallCallPricer);
                break;
            case 2:
                vo.setOptionType(AILimitLossesSnowBallCallPricer);
                break;
            case 3:
                vo.setOptionType(AISnowBallPutPricer);
                break;
            case 4:
                vo.setOptionType(AILimitLossesSnowBallPutPricer);
                break;
        }


        //定价计算方法
        vo.setAlgorithmName("PDE");
        //返息率=保底收益率
        vo.setReturnRateStructValue(openInfo.getPrincipalRate());
        vo.setReturnRateAnnulized(Boolean.TRUE);
        //红利票息
        vo.setBonusRateStructValue(openInfo.getCoupon().multiply(BigDecimal.valueOf(100)));
        vo.setBonusRateAnnulized(openInfo.getIsFixedCoupon());
        //产品开始日期默认为交易日期
        vo.setProductStartDate(openInfo.getTradeDate().toLocalDate());
        //期权费率
        vo.setOptionPremiumPercent(BigDecimalUtil.bigDecimalToPercentage(BigDecimal.valueOf(openInfo.getAnnualizedPremiumRate())));
        vo.setOptionPremiumPercentAnnulized(Boolean.TRUE);
        vo.setReturnRateStructValue(BigDecimalUtil.bigDecimalToPercentage(BigDecimal.valueOf(openInfo.getAnnualizedPremiumRate())));
        vo.setReturnRateAnnulized(Boolean.TRUE);
        //价格是否相对
        if (openInfo.getIsMoneynessOptionData()) {
            vo.setKnockinBarrierRelative(Boolean.TRUE);
            vo.setStrikeOnceKnockedinRelative(Boolean.TRUE);
            vo.setStrike2OnceKnockedinRelative(Boolean.TRUE);
        } else {
            vo.setKnockinBarrierRelative(Boolean.FALSE);
            vo.setStrikeOnceKnockedinRelative(Boolean.FALSE);
            vo.setStrike2OnceKnockedinRelative(Boolean.FALSE);
        }
        //敲入价格
        if (vo.getOptionType() == AILimitLossesSnowBallPutPricer
                || vo.getOptionType() == AILimitLossesSnowBallCallPricer
                || vo.getOptionType() == AISnowBallCallPricer
                || vo.getOptionType() == AISnowBallPutPricer) {
            //敲入价格
            if (vo.getKnockinBarrierRelative()) {
                vo.setKnockinBarrierValue(openInfo.getKIBarrier().multiply(new BigDecimal(100)));
            } else {
                vo.setKnockinBarrierValue(openInfo.getKIBarrier());
            }
            vo.setKnockinBarrierShift(BigDecimal.ZERO);
            //敲入行权价格
            if (vo.getStrikeOnceKnockedinRelative()) {
                vo.setStrikeOnceKnockedinValue(openInfo.getSpreadStrike1().multiply(new BigDecimal(100)));
            } else {
                vo.setStrikeOnceKnockedinValue(openInfo.getSpreadStrike1());
            }
            vo.setStrikeOnceKnockedinShift(BigDecimal.ZERO);
        }
        if (vo.getOptionType() == AILimitLossesSnowBallPutPricer
                || vo.getOptionType() == AILimitLossesSnowBallCallPricer) {
            //敲入行权价格1
            if (vo.getStrike2OnceKnockedinRelative()) {
                vo.setStrike2OnceKnockedinValue(openInfo.getSpreadStrike().multiply(BigDecimal.valueOf(100)));
            } else {
                vo.setStrike2OnceKnockedinValue(openInfo.getSpreadStrike());
            }
            vo.setStrikeOnceKnockedinShift(BigDecimal.ZERO);
        }
        //alreadyKnockedIn
        if (openInfo.getKnockInOutStatus() != null && "KnockedIn".equals(openInfo.getKnockInOutStatus())) {
            vo.setAlreadyKnockedIn(Boolean.TRUE);
        } else {
            vo.setAlreadyKnockedIn(Boolean.FALSE);
        }
        //处理观察日列表
        String[] obsArr = StringUtils.split(openInfo.getKOObservationDates(), ";");
        List<LocalDate> obsDateList = Arrays.stream(StringUtils.split(obsArr[0], ","))
                .map(LocalDate::parse).collect(Collectors.toList());
        List<BigDecimal> barrierList = Arrays.stream(StringUtils.split(obsArr[1], ","))
                .map(BigDecimal::new).collect(Collectors.toList());
        List<BigDecimal> rebateRateList = Arrays.stream(StringUtils.split(obsArr[2], ","))
                .map(BigDecimal::new).collect(Collectors.toList());
        String[] settleDates = StringUtils.split(openInfo.getKOObservationSettleDates(), ",");
        List<LocalDate> settleDateList = Arrays.stream(settleDates).map(LocalDate::parse).collect(Collectors.toList());
        MarketCodeDto marketCodeDto = new MarketCodeDto();
        marketCodeDto.setUnderlyingCode(vo.getUnderlyingCode());
        Map<String, BigDecimal> closePriceMap = marketClient.getAllCloseDatePriceByCode(marketCodeDto);
        List<TradeObsDateVO> tradeObsDateVOList = new ArrayList<>();
        for (int i = 0; i < obsDateList.size(); i++) {
            TradeObsDateVO tradeObsDateVO = new TradeObsDateVO();
            tradeObsDateVO.setUnderlyingCode(vo.getUnderlyingCode());
            tradeObsDateVO.setObsDate(obsDateList.get(i));
            tradeObsDateVO.setSettlementDate(settleDateList.get(i));
            tradeObsDateVO.setBarrierShift(BigDecimal.ZERO);
            tradeObsDateVO.setPrice(closePriceMap.get(tradeObsDateVO.getObsDate().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN))));
            if (openInfo.getIsMoneynessOptionData()) {
                tradeObsDateVO.setBarrier(barrierList.get(i).multiply(BigDecimal.valueOf(100)));
                tradeObsDateVO.setBarrierRelative(Boolean.TRUE);
            } else {
                tradeObsDateVO.setBarrier(barrierList.get(i));
                tradeObsDateVO.setBarrierRelative(Boolean.FALSE);
            }
            tradeObsDateVO.setRebateRate(rebateRateList.get(i).multiply(BigDecimal.valueOf(100)));
            //票息是否年化与镒链相反
            tradeObsDateVO.setRebateRateAnnulized(!openInfo.getIsFixedCoupon());
            tradeObsDateVOList.add(tradeObsDateVO);
        }
        vo.setTradeObsDateList(tradeObsDateVOList);
        //保证金占用
        switch (vo.getOptionType()){
            //雪球期权
            case AILimitLossesSnowBallCallPricer:
            case AILimitLossesSnowBallPutPricer:
                BigDecimal strike = vo.getStrikeOnceKnockedinValue();
                if (!vo.getStrikeOnceKnockedinRelative()) {
                    strike = vo.getStrikeOnceKnockedinValue()
                            .divide(vo.getEntryPrice(), 16, RoundingMode.HALF_UP);
                }
                BigDecimal strike2 = vo.getStrike2OnceKnockedinValue();
                if (!vo.getStrike2OnceKnockedinRelative()) {
                    strike2 = vo.getStrike2OnceKnockedinValue()
                            .divide(vo.getEntryPrice(), 16, RoundingMode.HALF_UP);
                }
                //保证金占用转换为相对值取差
                vo.setUseMargin(BigDecimalUtil.percentageToBigDecimal(strike2.subtract(strike).abs()));
            case AISnowBallCallPricer:
            case AISnowBallPutPricer:
            case AIBreakEvenSnowBallCallPricer:
            case AIBreakEvenSnowBallPutPricer:
                if (vo.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                        vo.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                    vo.setUseMargin(BigDecimal.ONE);
                }
                if (Objects.isNull(vo.getUseMargin())) {
                    vo.setUseMargin(BigDecimal.ZERO);
                }
                break;
        }
        vo.setUseMargin(vo.getUseMargin().multiply(vo.getNotionalPrincipal()));
    }

    /**
     * 同步所有交易记录到镒链
     */
    @Override
    public String syncTradeToYl() {
        StringBuilder msg = new StringBuilder();
        msg.append("本次同步数量：");
        List<TradeMngVO> notSyncList = tradeMngClient.getNotSyncTradeList();
        msg.append(notSyncList.size());
        List<TradeMngByYlVo> list = CglibUtil.copyList(notSyncList, TradeMngByYlVo::new, (quote, yl) -> {
            yl.setOptionCombType(Enums.getIfPresent(OptionCombTypeEnum.class,
                    quote.getOptionCombType() != null ? quote.getOptionCombType().name() : "").orNull());
            yl.setOptionType(Enums.getIfPresent(OptionTypeEnum.class,
                    quote.getOptionType() != null ? quote.getOptionType().name() : "").orNull());
            yl.setExerciseType(Enums.getIfPresent(ExerciseTypeEnum.class,
                    quote.getExerciseType() != null ? quote.getExerciseType().name() : "").orNull());
            yl.setBuyOrSell(Enums.getIfPresent(BuyOrSellEnum.class,
                    quote.getBuyOrSell() != null ? quote.getBuyOrSell().name() : "").orNull());
            yl.setCallOrPut(Enums.getIfPresent(CallOrPutEnum.class,
                    quote.getCallOrPut() != null ? quote.getCallOrPut().name() : "").orNull());
            yl.setSettleType(Enums.getIfPresent(SettleTypeEnum.class,
                    quote.getSettleType() != null ? quote.getSettleType().name() : "").orNull());
            yl.setTradeState(Enums.getIfPresent(TradeStateEnum.class,
                    quote.getTradeState() != null ? quote.getTradeState().name() : "").orNull());
        });
        syncTradeToYl(list, false);
        return msg.toString();
    }

    @Override
    public void syncTradeToYl(List<TradeMngByYlVo> notSyncList, Boolean isUpdate) {
        Map<Boolean, List<TradeMngByYlVo>> notSyncListMap = notSyncList.stream().collect(Collectors.groupingBy(vo -> vo.getOptionCombType() == null));
        //单腿同步
        if (notSyncListMap.get(Boolean.TRUE) != null) {
            for (TradeMngByYlVo vo : notSyncListMap.get(Boolean.TRUE)) {
                vo.setIsSync(0);
                BaseResult<String> isSuccess;
                if (vo.getOptionType()==AIForwardPricer){
                    if (isUpdate) {
                        orderService.tradeInvalid(TradeInvalidDto.builder().tradeNumber(vo.getTradeCode()).build());
                    }
                    isSuccess = orderService.forwardTradeSave(getForwardTradeSaveInfo(vo));
                }else {
                    isSuccess = orderService.option(getOrderOptionInfo(vo, isUpdate));
                }
                SyncUpdateDto dto = new SyncUpdateDto();
                dto.setIds(Collections.singletonList(vo.getId()));
                dto.setSyncStatus(isSuccess.getErrcode() == 0 ? 1 : 2);
                dto.setMsg(isSuccess.getErrmsg());
                Boolean sync = tradeMngClient.updateSync(dto);
                if (!sync) {
                    log.error("更新交易ID为【{}】的同步状态失败", JSONObject.toJSONString(dto));
                }
            }
        }
        //组合同步
        if (notSyncListMap.get(Boolean.FALSE) != null) {
            Map<String, List<TradeMngByYlVo>> combMap = notSyncListMap.get(Boolean.FALSE).stream().collect(Collectors.groupingBy(TradeMngByYlVo::getCombCode));

            for (List<TradeMngByYlVo> combList : combMap.values()) {
                if (combList.size() < 2) {
                    log.error("同步组合出现异常数据,组合应为两腿以上交易异常数据如下：{}", JSONObject.toJSONString(combList));
                }
                if (isUpdate) {
                    for (TradeMngByYlVo vo : combList) {
                        vo.setIsSync(0);
                        BaseResult<String> isSuccess;
                        isSuccess = orderService.option(getOrderOptionInfo(vo, true));
                        SyncUpdateDto dto = new SyncUpdateDto();
                        dto.setIds(Collections.singletonList(vo.getId()));
                        dto.setSyncStatus(isSuccess.getErrcode() == 0 ? 1 : 2);
                        dto.setMsg(isSuccess.getErrmsg());
                        Boolean sync = tradeMngClient.updateSync(dto);
                        if (!sync) {
                            log.error("更新交易ID为【{}】的同步状态失败", JSONObject.toJSONString(dto));
                        }
                    }
                } else {
                    StructureOptionDto structureOptionDto = StructureOptionDto.builder()
                            .structureType(combList.get(0).getOptionCombType().getDesc())
                            .build();
                    List<OrderOptionDto> optionDtoList = new ArrayList<>();
                    for (TradeMngByYlVo vo : combList) {
                        OrderOptionDto dto = getOrderOptionInfo(vo, false);
                        //镒链会自动添加后缀所以只能用组合编号
                        dto.setTradeNumber(vo.getCombCode());
                        optionDtoList.add(dto);
                    }
                    structureOptionDto.setTrades(optionDtoList);
                    BaseResult<String> structureOptions = orderService.structureOptions(structureOptionDto);
                    SyncUpdateDto syncUpdateDto = new SyncUpdateDto();
                    syncUpdateDto.setSyncStatus(structureOptions.getErrcode() == 0 ? 1 : 2);
                    syncUpdateDto.setMsg(structureOptions.getErrmsg());
                    syncUpdateDto.setIds(combList.stream().map(TradeMngByYlVo::getId).collect(Collectors.toList()));
                    Boolean sync = tradeMngClient.updateSync(syncUpdateDto);
                    if (!sync) {
                        log.error("更新交易ID为【{}】的同步状态失败", JSONObject.toJSONString(syncUpdateDto));
                    }
                }
            }
        }
    }

    @Override
    public void syncTradeDel(TradeDelSyncDto tradeDelSyncDto) {

        BaseResult<String> tradedInvalid = orderService.tradeInvalid(TradeInvalidDto.builder().tradeNumber(tradeDelSyncDto.getTradeNumber()).build());
        SyncUpdateDto dto = new SyncUpdateDto();
        dto.setIds(tradeDelSyncDto.getTradeIdList());
        dto.setSyncStatus(tradedInvalid.getErrcode() == 0 ? 1 : 2);
        dto.setMsg(tradedInvalid.getErrmsg());
        tradeMngClient.updateSync(dto);
    }

    @Override
    public String syncTradeCloseToYl() {
        StringBuilder msg = new StringBuilder();

        List<TradeCloseMngFeignVo> notSyncList = tradeMngCloseClient.getNotSyncTradeCloseList();
        msg.append("本次同步数量：");
        msg.append(notSyncList.size());
        for (TradeCloseMngFeignVo vo : notSyncList) {
            syncTradeCloseToYl(vo, "平仓");
        }

        return msg.toString();
    }

    @Override
    public void syncTradeCloseToYl(TradeCloseMngFeignVo vo, String closeType) {
        //四个累计期权的平仓数量修改为成交数量
        TradeMngVO tradeMngFeignVo = tradeMngClient.getTraderByTradeCode(vo.getTradeCode());
        if (org.orient.otc.api.quote.enums.OptionTypeEnum.getAccOption().contains(tradeMngFeignVo.getOptionType())) {
            vo.setCloseVolume(tradeMngFeignVo.getTradeVolume());
        }
        if (tradeMngFeignVo.getUnderlyingCode().contains("JD")){
            //鸡蛋成交数量传给镒链的时候需要除2
            vo.setCloseVolume(vo.getCloseVolume().divide(new BigDecimal(2),2,RoundingMode.HALF_UP));
        }
        TradeCloseDto closeDto = TradeCloseDto.builder()
                .tradeNumber(vo.getTradeCode())
                .closeType(closeType)
                .closeDate(vo.getCloseDate())
                .closeTradeAmount(vo.getCloseVolume())
                .unwindTotalFee(vo.getCloseTotalAmount())
                .underlyingPrice(vo.getCloseEntryPrice())
                .unwindVolatility(vo.getCloseVol() == null ? null : vo.getCloseVol().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .build();

        BaseResult<String> isSuccess = orderService.tradeClose(closeDto);
        SyncUpdateDto dto = new SyncUpdateDto();
        dto.setIds(Collections.singletonList(vo.getId()));
        dto.setSyncStatus(isSuccess.getErrcode() == 0 ? 1 : 2);
        Boolean sync = tradeMngCloseClient.updateSync(dto);
        if (!sync) {
            log.error("更新平仓ID为【{}】的同步状态失败", JSONObject.toJSONString(dto));
        }
    }

    @Override
    public String syncTradeRiskPv(Integer clientId, LocalDate riskDate) {
        List<TradeRiskPVInfoVO> list = tradeRiskInfoClient.getRiskInfoListByRiskDate(clientId, riskDate);
        BussinessException.E_100104.assertTrue(!list.isEmpty(), "找不到风险信息");
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(item -> {
            UpdateCustomTradeRiskDTO marginDTO = UpdateCustomTradeRiskDTO.builder()
                    .pv(item.getAvailableAmount())
                    .delta(item.getDelta())
                    .gamma(item.getGamma())
                    .vega(item.getVega())
                    .theta(item.getTheta())
                    .rho(item.getRho().divide(new BigDecimal(100),6,RoundingMode.HALF_UP))
                    .build();
            marginDTO.setTradeNumber(item.getTradeCode());
            marginDTO.setValueDate(item.getRiskDate());
            marginDTO.setMargin(item.getMargin());
            BaseResult<String>  result =  ylService.updateCustomTradeRisk(marginDTO);
            if (result.getErrcode()!=0){
                stringBuilder.append(result.getErrmsg()).append(";");
            }
        });
        return stringBuilder.toString();
    }

    @Override
    public String syncTradeRiskMargin(Integer clientId, LocalDate riskDate) {
        List<TradeRiskPVInfoVO> list = tradeRiskInfoClient.getRiskInfoListByRiskDate(clientId, riskDate);
        BussinessException.E_100104.assertTrue(!list.isEmpty(), "找不到保证金信息");
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(item -> {
            UpdateTradePositionMarginDTO marginDTO = UpdateTradePositionMarginDTO.builder()
                    .tradeNumber(item.getTradeCode())
                    .valueDate(item.getRiskDate())
                    .margin(item.getMargin())
                    .build();
            BaseResult<String>  result=   ylService.updateTradePositionMargin(marginDTO);
            if (result.getErrcode()!=0){
                stringBuilder.append(result.getErrmsg()).append(";");
            }
        });
        return stringBuilder.toString();
    }

    /**
     * 获取场外期权下单信息
     * @param tradeMngVo 交易记录
     * @return 场外期权API请求对象
     */
    private OrderOptionDto getOrderOptionInfo(TradeMngByYlVo tradeMngVo, boolean isUpdate) {
        //交易员信息
        UserVo userVo = userClient.getUserById(tradeMngVo.getTraderId());

        //获取交易对手编号
        ClientVO clientVo = clientClient.getClientById(tradeMngVo.getClientId());
        //获取对应的薄记账号名称
        AssetunitVo assetunitVo = assetUnitClient.getAssetunitById(tradeMngVo.getAssetId());
        MetaDic metaDic = new MetaDic();
        metaDic.setDay1Pnl(tradeMngVo.getDay1PnL());
        metaDic.setMidVol(tradeMngVo.getMidVol());
        OrderOptionDto orderOptionDto = OrderOptionDto.builder()
                .update(isUpdate)
                .comments("otc客户端同步交易")
                .traderName(userVo.getName())
                .tradeType(tradeMngVo.getOptionType().getDesc())
                .tradeNumber(tradeMngVo.getTradeCode())
                .underlyingCode(tradeMngVo.getUnderlyingCode())
                .exerciseMode(tradeMngVo.getExerciseType() != null ? tradeMngVo.getExerciseType().getDesc() : "")
                .optionType(tradeMngVo.getCallOrPut() != null ? tradeMngVo.getCallOrPut().getDesc() : "")
                .spotPrice(tradeMngVo.getEntryPrice())
                .strike(tradeMngVo.getStrike())
                .tradeDate(tradeMngVo.getTradeDate())
                .exerciseDate(tradeMngVo.getMaturityDate())
                .averagingPeriodStartDate(tradeMngVo.getStartObsDate() == null ? null : tradeMngVo.getStartObsDate().atStartOfDay())
                //交易波动率=成交波动率
                .tradeOpenVolatility(tradeMngVo.getTradeVol().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .metaDic(metaDic)
                .build();

        //雪球期权
        if (tradeMngVo.getOptionType() == AIBreakEvenSnowBallCallPricer
                || tradeMngVo.getOptionType() == AIBreakEvenSnowBallPutPricer
                || tradeMngVo.getOptionType() == AISnowBallPutPricer
                || tradeMngVo.getOptionType() == AISnowBallCallPricer
                || tradeMngVo.getOptionType() == AILimitLossesSnowBallCallPricer
                || tradeMngVo.getOptionType() == AILimitLossesSnowBallPutPricer) {
            setSnowBallInfo(orderOptionDto, tradeMngVo);
        } else {
            //权利金单价=期权价格
            orderOptionDto.setTradeSinglePrice(tradeMngVo.getOptionPremium().abs());
            orderOptionDto.setTradeAmount(tradeMngVo.getTradeVolume());
            if (tradeMngVo.getUnderlyingCode().contains("JD")){
                //鸡蛋成交数量传给镒链的时候需要除2
                orderOptionDto.setTradeAmount(tradeMngVo.getTradeVolume().divide(new BigDecimal(2),2,RoundingMode.HALF_UP));
            }
        }
        //处理特殊期权结构的数据格式
        switch (tradeMngVo.getOptionType()) {
            //累购期权\累沽期权\固定赔付累购\固定赔付累沽的起始观察日期设置为交易日期
            case AICallAccPricer:
            case AIPutAccPricer:
            case AICallFixAccPricer:
            case AIPutFixAccPricer:
            case AICallKOAccPricer:
            case AIPutKOAccPricer:
            case AIEnCallKOAccPricer:
            case AIEnPutKOAccPricer:
            case AICallFixKOAccPricer:
            case AIPutFixKOAccPricer:
                orderOptionDto.setTradeType("自定义交易");
                orderOptionDto.setStructureType(tradeMngVo.getOptionType().getDesc());
                orderOptionDto.setExtendInfo(JSONObject.toJSONString(getExtendInfo(tradeMngVo)));
                break;
            case AIAsianPricer:
                orderOptionDto.setPayoffType("ArithmeticAverage");
                orderOptionDto.setExerciseMode(ExerciseTypeEnum.european.getDesc());
                break;
            case AIEnAsianPricer:
                orderOptionDto.setPayoffType("EnhancedArithmeticAverage");
                orderOptionDto.setEnhancedPrice(tradeMngVo.getStrike());
                orderOptionDto.setExerciseMode(ExerciseTypeEnum.european.getDesc());
                break;
            case AICustomPricer:
                orderOptionDto.setTradeType("自定义交易");
                orderOptionDto.setStructureType(tradeMngVo.getStructureType());
                orderOptionDto.setExtendInfo(tradeMngVo.getExtendInfo());
                break;
            default:
                orderOptionDto.setStructureType(tradeMngVo.getOptionCombType() == null ? "" : tradeMngVo.getOptionCombType().getDesc());
                break;
        }

        if (tradeMngVo.getOptionType() == AIPutAccPricer
                || tradeMngVo.getOptionType() == AIPutFixAccPricer) {
            orderOptionDto.setOptionType(CallOrPutEnum.put.getDesc());
        }
        if (tradeMngVo.getOptionType() == AICallAccPricer
                || tradeMngVo.getOptionType() == AICallFixAccPricer) {
            orderOptionDto.setOptionType(CallOrPutEnum.call.getDesc());
        }
        //买卖方向转换为交易员方向
        if (tradeMngVo.getBuyOrSell().name().equals(BuyOrSellEnum.buy.name())) {
            orderOptionDto.setBuySell(BuyOrSellEnum.sell.getDesc());
        } else {
            orderOptionDto.setBuySell(BuyOrSellEnum.buy.getDesc());
        }
        orderOptionDto.setAssetBookName(assetunitVo.getName());
        orderOptionDto.setClientNumber(clientVo.getCode());
        return orderOptionDto;
    }

    /**
     * 设置雪球相关属性
     * @param orderOptionDto 镒链交易
     * @param tradeMngVo     系统交易
     */
    private void setSnowBallInfo(OrderOptionDto orderOptionDto, TradeMngByYlVo tradeMngVo) {
        orderOptionDto.setStrike(BigDecimal.ZERO);
        orderOptionDto.setTradeSinglePrice(BigDecimal.ZERO);
        orderOptionDto.setStockEqvNotional(tradeMngVo.getNotionalPrincipal());
        //雪球的行权方式为欧式
        orderOptionDto.setExerciseMode(ExerciseTypeEnum.european.getDesc());
        //敲出佩服类别均为票息补偿
        orderOptionDto.setKOPayoffType(0);
        Boolean isRelative = tradeMngVo.getTradeObsDateList().get(0).getBarrierRelative();
        //敲出价格
        orderOptionDto.setKOBarrier(isRelative ? BigDecimalUtil.percentageToBigDecimal(tradeMngVo.getTradeObsDateList().get(0).getBarrier())
                : tradeMngVo.getTradeObsDateList().get(0).getBarrier());
        //敲出票息
        orderOptionDto.setKORebate(isRelative ? BigDecimalUtil.percentageToBigDecimal(tradeMngVo.getTradeObsDateList().get(0).getRebateRate())
                : tradeMngVo.getTradeObsDateList().get(0).getRebateRate());
        orderOptionDto.setPrincipalRate(tradeMngVo.getRebateRate());
        orderOptionDto.setCouponDayCount("Act365");
        //判断如果已经敲入，则将状态设置为敲入状态
        if (tradeMngVo.getAlreadyKnockedIn() != null && tradeMngVo.getAlreadyKnockedIn()) {
            orderOptionDto.setKnockInOutStatus("KnockedIn");
        }
        //红利票息
        orderOptionDto.setIsFixedCoupon(!tradeMngVo.getBonusRateAnnulized());
        orderOptionDto.setCoupon(BigDecimalUtil.percentageToBigDecimal(tradeMngVo.getBonusRateStructValue()));
        orderOptionDto.setIsMoneynessOption(isRelative ? "是" : "否");
        orderOptionDto.setKOObservationDates(getKOObservationDates(tradeMngVo.getTradeObsDateList()));
        orderOptionDto.setKOObservationSettleDates(tradeMngVo.getTradeObsDateList()
                .stream().map(vo -> vo.getSettlementDate().toString()).collect(Collectors.joining(",")));
        if (tradeMngVo.getOptionType() == AILimitLossesSnowBallPutPricer
                || tradeMngVo.getOptionType() == AILimitLossesSnowBallCallPricer
                || tradeMngVo.getOptionType() == AISnowBallCallPricer
                || tradeMngVo.getOptionType() == AISnowBallPutPricer) {
            //敲入价格
            if (tradeMngVo.getStrikeOnceKnockedinRelative()) {
                orderOptionDto.setKIBarrier(BigDecimalUtil.percentageToBigDecimal(tradeMngVo.getKnockinBarrierValue()));
            } else {
                orderOptionDto.setKIBarrier(tradeMngVo.getKnockinBarrierValue());
            }
            //敲入行权价格
            if (tradeMngVo.getStrikeOnceKnockedinRelative()) {
                orderOptionDto.setSpreadStrike1(BigDecimalUtil.percentageToBigDecimal(tradeMngVo.getStrikeOnceKnockedinValue()));
            } else {
                orderOptionDto.setSpreadStrike1(tradeMngVo.getStrikeOnceKnockedinValue());
            }
        }
        if (tradeMngVo.getOptionType() == AILimitLossesSnowBallPutPricer
                || tradeMngVo.getOptionType() == AILimitLossesSnowBallCallPricer) {
            //敲入行权价格1
            if (tradeMngVo.getStrike2OnceKnockedinRelative()) {
                orderOptionDto.setSpreadStrike(BigDecimalUtil.percentageToBigDecimal(tradeMngVo.getStrike2OnceKnockedinValue()));
            } else {
                orderOptionDto.setSpreadStrike(tradeMngVo.getStrike2OnceKnockedinValue());
            }
        }
        switch (tradeMngVo.getOptionType()) {
            case AIBreakEvenSnowBallCallPricer:
                orderOptionDto.setOptionType(CallOrPutEnum.call.getDesc());
                orderOptionDto.setKIPayoffType(0);
                break;
            case AIBreakEvenSnowBallPutPricer:
                orderOptionDto.setOptionType(CallOrPutEnum.put.getDesc());
                orderOptionDto.setKIPayoffType(0);
                break;
            case AISnowBallCallPricer:
                orderOptionDto.setOptionType(CallOrPutEnum.call.getDesc());
                orderOptionDto.setKIPayoffType(1);
                break;
            case AISnowBallPutPricer:
                orderOptionDto.setOptionType(CallOrPutEnum.put.getDesc());
                orderOptionDto.setKIPayoffType(3);
                break;
            case AILimitLossesSnowBallCallPricer:
                orderOptionDto.setOptionType(CallOrPutEnum.call.getDesc());
                orderOptionDto.setKIPayoffType(2);
                break;
            case AILimitLossesSnowBallPutPricer:
                orderOptionDto.setOptionType(CallOrPutEnum.put.getDesc());
                orderOptionDto.setKIPayoffType(4);
                break;
        }
    }

    private String getKOObservationDates(List<TradeObsDateVO> obsDateVOList) {
        StringBuilder dateBuilder = new StringBuilder();
        StringBuilder barrBuilder = new StringBuilder();
        StringBuilder bonsBuilder = new StringBuilder();
        for (TradeObsDateVO vo : obsDateVOList) {
            dateBuilder.append(vo.getObsDate()).append(",");
            if (vo.getBarrierRelative()) {
                barrBuilder.append(BigDecimalUtil.percentageToBigDecimal(vo.getBarrier())).append(",");
            } else {
                barrBuilder.append(vo.getBarrier()).append(",");
            }
            bonsBuilder.append(BigDecimalUtil.percentageToBigDecimal(vo.getRebateRate())).append(",");
        }
        dateBuilder.setLength(dateBuilder.length() - 1);
        barrBuilder.setLength(barrBuilder.length() - 1);
        bonsBuilder.setLength(bonsBuilder.length() - 1);
        dateBuilder.append(";").append(barrBuilder)
                .append(";").append(bonsBuilder);
        return dateBuilder.toString();
    }

    /**
     * 获取自定义结构列表
     * @param tradeMngVo 请求对象
     * @return 自定义结构列表
     */
    private List<ExtendData> getExtendInfo(TradeMngByYlVo tradeMngVo) {
        List<ExtendData> list = new ArrayList<>();
        if (tradeMngVo.getBarrierRamp() != null) {
            list.add(ExtendData.builder().name("BarrierRamp").value(tradeMngVo.getBarrierRamp().toString()).build());
        }
        list.add(ExtendData.builder().name("单倍系数").value("1").build());
        list.add(ExtendData.builder().name("多倍系数").value(tradeMngVo.getLeverage().toString()).build());
        //观察日期
        if (tradeMngVo.getTradeObsDateList() != null && !tradeMngVo.getTradeObsDateList().isEmpty()) {
            list.add(ExtendData.builder().name("ObservationDates")
                    .value(tradeMngVo.getTradeObsDateList().stream().map(a -> a.getObsDate().toString())
                            .collect(Collectors.joining(","))).build());
        }
        //结算方式
        list.add(ExtendData.builder().name("isCashSettle").value(String.valueOf(tradeMngVo.getSettleType().getKey())).build());

        switch (tradeMngVo.getOptionType()) {
            case AICallFixKOAccPricer:
            case AIPutFixKOAccPricer:
                list.add(ExtendData.builder().name("固定赔付").value(tradeMngVo.getFixedPayment().toString()).build());
            case AICallKOAccPricer:
            case AIPutKOAccPricer:
                list.add(ExtendData.builder().name("敲出赔付").value(tradeMngVo.getKnockoutRebate().toString()).build());
            case AIEnCallKOAccPricer:
            case AIEnPutKOAccPricer:
                list.add(ExtendData.builder().name("到期倍数").value(tradeMngVo.getExpireMultiple().toString()).build());
            case AICallAccPricer:
            case AIPutAccPricer:
                list.add(ExtendData.builder().name("累计敲出价格").value(tradeMngVo.getBarrier().toString()).build());
                break;
            case AICallFixAccPricer:
                list.add(ExtendData.builder().name("固定赔付").value(tradeMngVo.getFixedPayment().toString()).build());
                list.add(ExtendData.builder().name("StrikeRamp").value(tradeMngVo.getStrikeRamp().toString()).build());
                list.add(ExtendData.builder().name("固定赔付区间上沿").value(tradeMngVo.getBarrier().toString()).build());
                list.add(ExtendData.builder().name("固定赔付区间下沿").value(tradeMngVo.getStrike().toString()).build());
                break;
            case AIPutFixAccPricer:
                list.add(ExtendData.builder().name("固定赔付").value(tradeMngVo.getFixedPayment().toString()).build());
                list.add(ExtendData.builder().name("StrikeRamp").value(tradeMngVo.getStrikeRamp().toString()).build());
                list.add(ExtendData.builder().name("固定赔付区间上沿").value(tradeMngVo.getStrike().toString()).build());
                list.add(ExtendData.builder().name("固定赔付区间下沿").value(tradeMngVo.getBarrier().toString()).build());
                break;
        }
        return list;
    }

    /**
     * 获取场外期权下单信息
     * @param tradeMngVo 交易记录
     * @return 场外期权API请求对象
     */
    private ForwardTradeSaveDto getForwardTradeSaveInfo(TradeMngByYlVo tradeMngVo) {
        //交易员信息
        UserVo userVo = userClient.getUserById(tradeMngVo.getTraderId());
        //获取交易对手编号
        ClientVO clientVo = clientClient.getClientById(tradeMngVo.getClientId());
        //获取对应的薄记账号名称
        AssetunitVo assetunitVo = assetUnitClient.getAssetunitById(tradeMngVo.getAssetId());
        ForwardTradeSaveDto forwardTradeSaveDto = ForwardTradeSaveDto.builder()
                .tradeNumber(tradeMngVo.getTradeCode())
                .priceModel(1)
                .assetBookName(assetunitVo.getName())
                .traderName(userVo.getName())
                .underlyingCode(tradeMngVo.getUnderlyingCode())
                .optionType("多头")
                .spotPrice(tradeMngVo.getEntryPrice())
                .strike(tradeMngVo.getStrike())
                .tradeDate(tradeMngVo.getTradeDate())
                .exerciseDate(tradeMngVo.getMaturityDate())
                .tradeAmount(tradeMngVo.getTradeVolume())
                //成交金额使用东证方向
                .tradePrice(tradeMngVo.getTotalAmount().negate())
                .initialMargin(tradeMngVo.getMargin())
                .comments("otc客户端同步交易")
                .build();
        //交易方向需要改变
        if (tradeMngVo.getBuyOrSell() == BuyOrSellEnum.buy) {
            forwardTradeSaveDto.setBuySell(BuyOrSellEnum.sell.getDesc());
        } else {
            forwardTradeSaveDto.setBuySell(BuyOrSellEnum.buy.getDesc());
        }
        if (tradeMngVo.getUnderlyingCode().contains("JD")){
            //鸡蛋成交数量传给镒链的时候需要除2
            forwardTradeSaveDto.setTradeAmount(tradeMngVo.getTradeVolume().divide(new BigDecimal(2),2,RoundingMode.HALF_UP));
        }
        forwardTradeSaveDto.setAssetBookName(assetunitVo.getName());
        forwardTradeSaveDto.setClientNumber(clientVo.getCode());
        forwardTradeSaveDto.setClientName(clientVo.getName());
        return forwardTradeSaveDto;
    }
}
