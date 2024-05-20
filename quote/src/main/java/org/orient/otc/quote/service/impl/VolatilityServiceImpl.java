package org.orient.otc.quote.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.StopWatch;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.orient.otc.api.dm.enums.MainContractEnum;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.dto.UnderlyingVolatilityFeignDto;
import org.orient.otc.api.quote.dto.VolatilityQueryDto;
import org.orient.otc.api.quote.dto.VolatityQueryCodeListDto;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.VolTypeEnum;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.exception.BaseException;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.jni.dto.AILinearInterpVolSurfaceRequest;
import org.orient.otc.common.jni.dto.VolSurface;
import org.orient.otc.common.jni.util.JniUtil;
import org.orient.otc.common.jni.vo.AIDeltaVol2StrikeVolResult;
import org.orient.otc.common.jni.vo.AILinearInterpVolSurfaceResult;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.quote.config.multidb.DB;
import org.orient.otc.quote.dto.volatility.*;
import org.orient.otc.quote.entity.Volatility;
import org.orient.otc.quote.entity.VolatilityNew;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.VolatilityMapper;
import org.orient.otc.quote.mapper.VolatilityNewMapper;
import org.orient.otc.quote.service.VolatilityService;
import org.orient.otc.quote.util.VolatilityUtil;
import org.orient.otc.quote.vo.volatility.DeltaVolToStrikeVolVo;
import org.orient.otc.quote.vo.volatility.LinearInterpVolSurfaceVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.orient.otc.quote.util.VolatilityUtil.getValatityDataByOffset;

/**
 * 波动率实现类
 */
@Service
@Slf4j
public class VolatilityServiceImpl extends ServiceImpl<BaseMapper<Volatility>, Volatility> implements VolatilityService {
    @Resource
    private VolatilityMapper volatilityMapper;

    @Resource
    private VolatilityNewMapper volatilityNewMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UnderlyingManagerClient underlyingManagerClient;
    @Resource
    private JniUtil jniUtil;

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private SystemConfigUtil systemConfigUtil;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${isNeedSyncToYl: false}")
    private Boolean isNeedSyncToYl;

    @Value("${isNeedSyncToFinoview: false}")
    private Boolean isNeedSyncToFinoview;

    @Override
    @DB
    public List<Volatility> getVolatility(VolatilityQueryDto volatilityQueryDto) {
        LocalDate tradeDate = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        //如果传入了交易日期并且小于系统交易日则获取历史波动率
        if (Objects.nonNull(volatilityQueryDto.getQuotationDate()) && volatilityQueryDto.getQuotationDate().isBefore(tradeDate)) {
            LambdaQueryWrapper<Volatility> volatilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            volatilityLambdaQueryWrapper.eq(Volatility::getQuotationDate, volatilityQueryDto.getQuotationDate());
            if (Objects.nonNull(volatilityQueryDto.getUnderlyingCode())) {
                volatilityLambdaQueryWrapper.eq(Volatility::getUnderlyingCode, volatilityQueryDto.getUnderlyingCode());
            }
            volatilityLambdaQueryWrapper.eq(Volatility::getIsDeleted, 0);
            return volatilityMapper.selectList(volatilityLambdaQueryWrapper);
        } else {
            //否则查询最新波动率返回
            return getNewVolatility(volatilityQueryDto.getUnderlyingCode() != null ? Collections.singleton(volatilityQueryDto.getUnderlyingCode()) : null, tradeDate);
        }
    }

    @Override
    public List<String> getUnderlyingCodeByVol(LocalDate localDate) {
        LocalDate tradeDate = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        //如果传入了交易日期并且小于系统交易日则获取历史波动率
        if (Objects.nonNull(localDate) && localDate.isBefore(tradeDate)) {
            LambdaQueryWrapper<Volatility> volatilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            volatilityLambdaQueryWrapper.eq(Volatility::getQuotationDate, localDate);
            volatilityLambdaQueryWrapper.eq(Volatility::getIsDeleted, 0);
            volatilityLambdaQueryWrapper.select(Volatility::getUnderlyingCode);
            return volatilityMapper.selectList(volatilityLambdaQueryWrapper).stream()
                    .map(Volatility::getUnderlyingCode).distinct().collect(Collectors.toList());
        } else {
            //否则查询最新波动率返回
            return getNewUnderlyingCodeByVol();
        }
    }

    @Override
    public List<String> getNewUnderlyingCodeByVol() {
        LambdaQueryWrapper<VolatilityNew> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(VolatilityNew::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.select(VolatilityNew::getUnderlyingCode);
        return volatilityNewMapper.selectList(lambdaQueryWrapper).stream()
                .map(VolatilityNew::getUnderlyingCode).distinct().collect(Collectors.toList());
    }

    @Override
    public VolatilityVO getNewVolatilityByType(String underlyingCode, VolTypeEnum type) {
        String volDataStr = stringRedisTemplate.opsForValue().get(RedisAdapter.VOLATILITY + underlyingCode + "_" + type.name());
        if (StringUtils.isNotBlank(volDataStr)) {
            return JSONObject.parseObject(volDataStr, VolatilityVO.class);
        } else {
            LambdaQueryWrapper<VolatilityNew> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(VolatilityNew::getIsDeleted, IsDeletedEnum.NO);
            lambdaQueryWrapper.eq(VolatilityNew::getUnderlyingCode, underlyingCode);
            lambdaQueryWrapper.eq(VolatilityNew::getVolType, type);
            VolatilityNew volatilityNew = volatilityNewMapper.selectOne(lambdaQueryWrapper);
            stringRedisTemplate.opsForValue().set(RedisAdapter.VOLATILITY + underlyingCode + "_" + type.name(), JSONObject.toJSONString(volatilityNew),7,TimeUnit.DAYS);
            return CglibUtil.copy(volatilityNew, VolatilityVO.class, (value, target, context) -> Convert.convertQuietly(target, value));
        }
    }

    @Override
    public VolatilityVO getVolatilityByTypeAndDate(String underlyingCode, LocalDate tradeDate, VolTypeEnum type) {
        if (tradeDate.isBefore(systemConfigUtil.getTradeDay())){
            //当非当前交易日是获取历史当日的波动率
            LambdaQueryWrapper<Volatility> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Volatility::getIsDeleted, IsDeletedEnum.NO);
            lambdaQueryWrapper.eq(Volatility::getUnderlyingCode, underlyingCode);
            lambdaQueryWrapper.eq(Volatility::getVolType, type);
            lambdaQueryWrapper.eq(Volatility::getQuotationDate,tradeDate);
            return this.getVoOne(lambdaQueryWrapper,VolatilityVO.class);
        }else {
            //当是最新交易日时获取最新波动率
            return this.getNewVolatilityByType(underlyingCode,type);
        }
    }

    @Override
    public List<Volatility> getNewVolatility(Set<String> underlyingCodeList, LocalDate tradeDate) {
        LambdaQueryWrapper<VolatilityNew> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(VolatilityNew::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.in(underlyingCodeList != null && !underlyingCodeList.isEmpty(), VolatilityNew::getUnderlyingCode, underlyingCodeList);
        return CglibUtil.copyList(volatilityNewMapper.selectList(lambdaQueryWrapper), Volatility::new, (s, t) -> t.setQuotationDate(tradeDate));
    }

    @Override
    @Transactional
    public String insertOrUpdate(VolatilityListDto volatilityList, Boolean isNeedSync) {
        RLock lock = redissonClient.getLock("insertOrUpdateVolatility");
        lock.lock();
        try {
            for (Volatility volatility : volatilityList.getVolatilityList()) {
                if (Objects.nonNull(volatility.getId())) {
                    volatility.setId(null);
                }
                BussinessException.E_300101.assertTrue(Objects.nonNull(volatility.getUnderlyingCode()), "underlyingCode");
                BussinessException.E_300101.assertTrue(Objects.nonNull(volatility.getVolType()), "volType");
                BussinessException.E_300101.assertTrue(Objects.nonNull(volatility.getQuotationDate()), "quotationDate");
                BussinessException.E_300101.assertTrue(Objects.nonNull(volatility.getData()), "data");
                BussinessException.E_300101.assertTrue(Objects.nonNull(volatility.getInterpolationMethod()), "interpolationMethod");

                LambdaQueryWrapper<VolatilityNew> volatilityLambdaQueryWrapper = new LambdaQueryWrapper<>();
                volatilityLambdaQueryWrapper.eq(VolatilityNew::getUnderlyingCode, volatility.getUnderlyingCode());
                volatilityLambdaQueryWrapper.eq(VolatilityNew::getVolType, volatility.getVolType());
                volatilityLambdaQueryWrapper.eq(VolatilityNew::getIsDeleted, 0);
                VolatilityNew volatilityNew = CglibUtil.copy(volatility, VolatilityNew.class, (value, target, context) -> Convert.convertQuietly(target, value));
                Long count = volatilityNewMapper.selectCount(volatilityLambdaQueryWrapper);
                if (count >= 1) {
                    volatilityNewMapper.update(volatilityNew, volatilityLambdaQueryWrapper);
                } else {
                    volatilityNewMapper.insert(volatilityNew);
                }
                //删除redis数据
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisAdapter.VOLATILITY + volatility.getUnderlyingCode() + "_" + volatility.getVolType().name()))){
                    stringRedisTemplate.expire(RedisAdapter.VOLATILITY + volatility.getUnderlyingCode() + "_" + volatility.getVolType().name(),3,TimeUnit.SECONDS);
                }
            }
            //推送波动率数据到镒链
            if (isNeedSync && isNeedSyncToYl) {
                rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.SYNC_TOPIC_VOL, volatilityList.getVolatilityList());
            }
            //推送波动率数据到繁微
            if (isNeedSync && isNeedSyncToFinoview) {
                rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.VOL_TO_FINOVIEW, volatilityList.getVolatilityList());
            }
        } finally {
            lock.unlock();
        }
        return "insert success";
    }

    @Override
    public LinearInterpVolSurfaceVo linearInterpVolSurface(LinearInterpVolSurfaceDto linearInterpVolSurfaceDto) {
        LocalDate tradeDay = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        if (linearInterpVolSurfaceDto.getTradeDate().isAfter(tradeDay)) {
            linearInterpVolSurfaceDto.setTradeDate(tradeDay);
        }
        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setUnderlyingCode(linearInterpVolSurfaceDto.getUnderlyingCode());
        volatilityQueryDto.setQuotationDate(linearInterpVolSurfaceDto.getTradeDate());
        List<Volatility> volatilityList = this.getVolatility(volatilityQueryDto);
        return linearInterpVol(volatilityList, linearInterpVolSurfaceDto);
    }

    private LinearInterpVolSurfaceVo linearInterpVol(List<Volatility> volatilityList, LinearInterpVolSurfaceDto linearInterpVolSurfaceDto) {
        LinearInterpVolSurfaceVo linearInterpVolSurfaceVo = new LinearInterpVolSurfaceVo();
        List<Volatility> askVolatilityList = volatilityList.stream().filter(a -> a.getVolType() == VolTypeEnum.ask).collect(Collectors.toList());
        BussinessException.E_300103.assertTrue(askVolatilityList.size() == 1, "ask没配置");
        Volatility askVolatility = askVolatilityList.get(0);
        List<Volatility> midVolatilityList = volatilityList.stream().filter(a -> a.getVolType() == VolTypeEnum.mid).collect(Collectors.toList());
        BussinessException.E_300103.assertTrue(midVolatilityList.size() == 1, "mid没配置");
        Volatility midVolatility = midVolatilityList.get(0);
        List<Volatility> bidVolatilityList = volatilityList.stream().filter(a -> a.getVolType() == VolTypeEnum.bid).collect(Collectors.toList());
        BussinessException.E_300103.assertTrue(bidVolatilityList.size() == 1, "bid没配置");
        Volatility bidVolatility = bidVolatilityList.get(0);

        List<VolatityDataDto> volatityDataList = midVolatility.getData();

        VolSurface volSurface = VolatilityUtil.getVolSurface(volatityDataList);

        AILinearInterpVolSurfaceRequest aiLinearInterpVolSurface = getAiLinearInterpVolSurfaceRequest(linearInterpVolSurfaceDto);
        AILinearInterpVolSurfaceResult midAiLinearInterpVolSurfaceResult = jniUtil.AILinearInterpVolSurface(aiLinearInterpVolSurface, volSurface);
        linearInterpVolSurfaceVo.setMidVol(BigDecimal.valueOf(midAiLinearInterpVolSurfaceResult.getVolatility()).multiply(BigDecimal.valueOf(100)));
        //bid，ask波动率插值
        if ( OptionTypeEnum.getNeedGenerateForwardOptionType().contains(linearInterpVolSurfaceDto.getOptionType())) {
            linearInterpVolSurfaceVo.setTradeVol(BigDecimal.valueOf(0));
        } else {
            if (linearInterpVolSurfaceDto.getBuyOrSell() == BuyOrSellEnum.buy) {
                volatityDataList = getValatityDataByOffset(midVolatility.getData(), askVolatility.getData());
            }
            if (linearInterpVolSurfaceDto.getBuyOrSell() == BuyOrSellEnum.sell) {
                volatityDataList = getValatityDataByOffset(midVolatility.getData(), bidVolatility.getData());
            }
            volSurface.setFlattenedVol(volatityDataList.stream().map(a -> a.getVol().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP).doubleValue()).mapToDouble(Double::valueOf).toArray());
            volSurface.setFlattenedVolLength(volatityDataList.size());
            AILinearInterpVolSurfaceResult aiLinearInterpVolSurfaceResult = jniUtil.AILinearInterpVolSurface(aiLinearInterpVolSurface, volSurface);
            linearInterpVolSurfaceVo.setTradeVol(BigDecimal.valueOf(aiLinearInterpVolSurfaceResult.getVolatility()).multiply(BigDecimal.valueOf(100)));
        }
        return linearInterpVolSurfaceVo;
    }

    @Override
    public List<LinearInterpVolSurfaceVo> linearInterpVolSurfaceBatch(List<LinearInterpVolSurfaceDto> list) {
        BussinessException.E_300102.assertTrue(!list.isEmpty(),"波动率插值参数不能为空");
        List<LinearInterpVolSurfaceVo> returnList = new ArrayList<>();
        LocalDate tradeDay = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        if (list.get(0).getTradeDate().isAfter(tradeDay)) {
            for (LinearInterpVolSurfaceDto dto : list) {
                dto.setTradeDate(tradeDay);
            }
        }
        Set<String> underlyingCodeSet = list.stream().map(LinearInterpVolSurfaceDto::getUnderlyingCode).collect(Collectors.toSet());
        List<Volatility> batchVolatilityList = this.getNewVolatility(underlyingCodeSet, tradeDay);
        Map<String, List<Volatility>> map = batchVolatilityList.stream().collect(Collectors.groupingBy(Volatility::getUnderlyingCode));
        // 获取与之对应的入参
        for (LinearInterpVolSurfaceDto linearInterpVolSurfaceDto : list) {
            // 返回值vo
            LinearInterpVolSurfaceVo linearInterpVolSurfaceVo = linearInterpVol(map.get(linearInterpVolSurfaceDto.getUnderlyingCode())
                    , linearInterpVolSurfaceDto);
            // 批量查询插值用no对应回填
            linearInterpVolSurfaceVo.setNo(linearInterpVolSurfaceDto.getNo());
            returnList.add(linearInterpVolSurfaceVo);
        }
        return returnList;
    }

    /**
     * 构造插值请求参数
     * @param linearInterpVolSurfaceDto 插值请求参数
     * @return 请求参数
     */
    @NonNull
    private static AILinearInterpVolSurfaceRequest getAiLinearInterpVolSurfaceRequest(LinearInterpVolSurfaceDto linearInterpVolSurfaceDto) {
        AILinearInterpVolSurfaceRequest aiLinearInterpVolSurface = new AILinearInterpVolSurfaceRequest();
        //雪球期权
        if (OptionTypeEnum.getSnowBall().contains(linearInterpVolSurfaceDto.getOptionType())) {
            aiLinearInterpVolSurface.setDimMoneyness(1);
        } else {
            aiLinearInterpVolSurface.setDimMoneyness(linearInterpVolSurfaceDto.getStrike().divide(linearInterpVolSurfaceDto.getEntryPrice(), 4, RoundingMode.HALF_UP).doubleValue());
        }
        aiLinearInterpVolSurface.setDimTenor((double) linearInterpVolSurfaceDto.getTradeDate().until(linearInterpVolSurfaceDto.getMaturityDate(), ChronoUnit.DAYS));
        return aiLinearInterpVolSurface;
    }

    @Override
    public DeltaVolToStrikeVolVo deltaVolToStrikeVol(DeltaVolToStrikeVolDto data) {
        VolSurface volSurface = new VolSurface();
        double[] tmpMoneyness = {0.8, 0.82, 0.84, 0.86, 0.88, 0.9, 0.92, 0.94, 0.96, 0.98, 1, 1.02, 1.04, 1.06, 1.08, 1.1, 1.12, 1.14, 1.16, 1.18, 1.2};
        volSurface.setVerticalAxis(tmpMoneyness);
        volSurface.setVerticalAxisLength(tmpMoneyness.length);
        double[] tmpttmDays = {1, 7, 14, 30, 60, 90, 183, 365};
        volSurface.setHorizontalAxis(tmpttmDays);
        volSurface.setHorizontalAxisLength(tmpttmDays.length);

        //将bid的delta波动率，mid的delta波动率，ask的delta波动率拼成一个数组
        List<VolatityDeltaDataDto> askDeltaData = data.getAskDeltaData();
        List<VolatityDeltaDataDto> midDeltaData = data.getMidDeltaData();
        List<VolatityDeltaDataDto> bidDeltaData = data.getBidDeltaData();
        BussinessException.E_300102.assertTrue(askDeltaData.size() == 56, "askDeltaData长度必须为56");
        BussinessException.E_300102.assertTrue(midDeltaData.size() == 56, "midDeltaData长度必须为56");
        BussinessException.E_300102.assertTrue(bidDeltaData.size() == 56, "bidDeltaData长度必须为56");
        for (int i = 0; i < 56; i++) {
            BussinessException.E_300102.assertTrue(askDeltaData.get(i).getVol().compareTo(BigDecimal.valueOf(100)) < 0, "ask波动率小于100");
            BussinessException.E_300102.assertTrue((midDeltaData.get(i).getVol().add(bidDeltaData.get(i).getVol())).compareTo(BigDecimal.valueOf(0)) > 0, "mid和bid波动率相加必须大于0");
        }

        List<Double> flattenedVolList = new ArrayList<>();
        flattenedVolList.addAll(midDeltaData.stream().map(a -> a.getVol().doubleValue()).collect(Collectors.toList()));
        flattenedVolList.addAll(bidDeltaData.stream().map(a -> a.getVol().doubleValue()).collect(Collectors.toList()));
        flattenedVolList.addAll(askDeltaData.stream().map(a -> a.getVol().doubleValue()).collect(Collectors.toList()));

        volSurface.setFlattenedVol(flattenedVolList.stream().mapToDouble(Double::valueOf).toArray());
        volSurface.setFlattenedVolLength(flattenedVolList.size());
        log.debug("volSurface={}", JSONObject.toJSONString(volSurface));
        AIDeltaVol2StrikeVolResult aiDeltaVol2StrikeVolResult = jniUtil.AIDeltaVol2StrikeVol(volSurface);
        log.debug("volSurface={}", JSONObject.toJSONString(aiDeltaVol2StrikeVolResult));
        double[] horizontalAxis = aiDeltaVol2StrikeVolResult.getVolSurface().getHorizontalAxis();
        double[] verticalAxis = aiDeltaVol2StrikeVolResult.getVolSurface().getVerticalAxis();
        double[] flattenedVol = aiDeltaVol2StrikeVolResult.getVolSurface().getFlattenedVol();
        //返回的波动率数据，mid,bid,ask是在一起的
        List<VolatityDataDto> midDataVoList = new ArrayList<>();
        List<VolatityDataDto> bidDataVoList = new ArrayList<>();
        List<VolatityDataDto> askDataVoList = new ArrayList<>();
        for (int i = 0; i < flattenedVol.length; i++) {
            int a = i % (verticalAxis.length);//求数据在第几列
            int b = i / (verticalAxis.length);//求数据在第几行
            int c = b % (horizontalAxis.length);//查出在mid，bid，ask中的第几行
            VolatityDataDto volatityDataDto = new VolatityDataDto();
            String format = String.format("%.2f", flattenedVol[i]);//保留两位小数
            double vol = Double.parseDouble(format);
            volatityDataDto.setVol(BigDecimal.valueOf(vol));
            volatityDataDto.setExpire((int) horizontalAxis[c]);
            String format1 = String.format("%.2f", verticalAxis[a] * 100);//保留两位小数
            double strike = Double.parseDouble(format1);
            volatityDataDto.setStrike(BigDecimal.valueOf(strike));
            //将数据拆为三份
            if (b / horizontalAxis.length == 0) {
                midDataVoList.add(volatityDataDto);
            } else if (b / horizontalAxis.length == 1) {
                bidDataVoList.add(volatityDataDto);
            } else if (b / horizontalAxis.length == 2) {
                askDataVoList.add(volatityDataDto);
            }
        }
        DeltaVolToStrikeVolVo deltaVolToStrikeVolVo = new DeltaVolToStrikeVolVo();
        deltaVolToStrikeVolVo.setAskData(askDataVoList);
        deltaVolToStrikeVolVo.setMidData(midDataVoList);
        deltaVolToStrikeVolVo.setBidData(bidDataVoList);
        return deltaVolToStrikeVolVo;
    }

    /**
     * 获取存活的合约列表，并且有波动率的
     * @return 合约代码列表
     */
    @Override
    public List<UnderlyingManagerVO> getUnderlyingCodeListByVol(VolatityQueryCodeListDto dto) {
        LocalDate quotationDate = dto.getQuotationDate();
        if (quotationDate == null) {
            String tradeDayStr = Objects.requireNonNull(stringRedisTemplate.opsForHash()
                    .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString();
            if (StringUtils.isNotBlank(tradeDayStr)) {
                quotationDate = LocalDate.parse(tradeDayStr);
            } else {
                quotationDate = LocalDate.now();
            }
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("getUnderlyingList");
        List<UnderlyingManagerVO> voList = underlyingManagerClient.getUnderlyingList();
        stopWatch.stop();
        VolatilityQueryDto volatilityQueryDto = new VolatilityQueryDto();
        volatilityQueryDto.setQuotationDate(quotationDate);
        stopWatch.start("getUnderlyingCodeByVol");
        List<String> underlyingList = this.getUnderlyingCodeByVol(quotationDate);
        stopWatch.stop();
        log.debug("获取存活合约并且有波动率耗时：{}",stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
        return voList.stream().filter(a -> underlyingList.contains(a.getUnderlyingCode())).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Boolean saveVolToTradeDay(LocalDate tradeDay) {
        BussinessException.E_300103.assertTrue(tradeDay.isEqual(LocalDate.now()), "不能覆盖非当前交易日的波动率");
        //批量保存波动率
        List<Volatility> volatilityList = getNewVolatility(null, tradeDay);
        for (Volatility volatility : volatilityList) {
            LambdaQueryWrapper<Volatility> queryWrapper = new LambdaQueryWrapper<Volatility>()
                    .eq(Volatility::getIsDeleted, IsDeletedEnum.NO)
                    .eq(Volatility::getUnderlyingCode, volatility.getUnderlyingCode())
                    .eq(Volatility::getQuotationDate, volatility.getQuotationDate())
                    .eq(Volatility::getVolType, volatility.getVolType());
            Long count = volatilityMapper.selectCount(queryWrapper);
            if (count == 0) {
                volatility.setId(null);
                volatilityMapper.insert(volatility);
            } else {
                volatilityMapper.update(volatility, queryWrapper);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public String saveVolatility(VolatilityListDto volatilityList) {
        UnderlyingManagerVO underlyingManagerVo = underlyingManagerClient.getUnderlyingByCode(volatilityList.getVolatilityList().get(0).getUnderlyingCode());
        BussinessException.E_300303.assertTrue(Objects.nonNull(underlyingManagerVo), volatilityList.getVolatilityList().get(0).getUnderlyingCode(), "underlyingId不存在");

        if (underlyingManagerVo.getMainContract() == MainContractEnum.yes) {
            List<UnderlyingManagerVO> underlyingManagerVOList = underlyingManagerClient.getUnderlyingListByVarietyId(underlyingManagerVo.getVarietyId());
            underlyingManagerVOList = underlyingManagerVOList.stream().filter(a -> a.getMainContract() != null).collect(Collectors.toList());
            List<UnderlyingVolatilityFeignDto> underlyingVolatilityDtoList = CglibUtil.copyList(underlyingManagerVOList, UnderlyingVolatilityFeignDto::new,
                    (s, t) -> {
                        t.setMainContract(s.getMainContract() == MainContractEnum.yes);
                        t.setVolOffset(s.getVolOffset());
                    });
            //先更新主力合约
            String result = this.insertOrUpdate(volatilityList, Boolean.TRUE);
            if (StringUtils.isNotBlank(result) && this.updateVolByOffset(underlyingVolatilityDtoList)) {
                return "更新成功";
            } else {
                return "更新失败";
            }
        } else if (underlyingManagerVo.getMainContract() == MainContractEnum.no) {
            BussinessException.E_300005.doThrow();
            return null;
        } else {
            return this.insertOrUpdate(volatilityList, Boolean.TRUE);
        }
    }

    @Override
    @Transactional
    public Boolean updateVolByOffset(List<UnderlyingVolatilityFeignDto> underlyingVolatilityDtoList) {
        LocalDate tradeDay = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        //找出主力合约的波动率
        Optional<UnderlyingVolatilityFeignDto> optionalUnderlyingVolatilityFeignDto = underlyingVolatilityDtoList.stream().filter(UnderlyingVolatilityFeignDto::getMainContract).findFirst();
        UnderlyingVolatilityFeignDto u = optionalUnderlyingVolatilityFeignDto.orElseThrow(() -> new BaseException(BussinessException.E_300103, "主力合约不存在"));
        //获取主力合约的波动率
        List<Volatility> mainVolatilityList = getNewVolatility(Collections.singleton(u.getUnderlyingCode()), tradeDay);

        Optional<Volatility> mainAskVolatilityOptional = mainVolatilityList.stream().filter(a -> a.getVolType() == VolTypeEnum.ask).findFirst();
        Volatility mainAskVolatility = mainAskVolatilityOptional.orElseThrow(() -> new BaseException(BussinessException.E_300103, "主力合约ask没配置"));

        Optional<Volatility> mainMidVolatilityOptional = mainVolatilityList.stream().filter(a -> a.getVolType() == VolTypeEnum.mid).findFirst();
        Volatility mainMidVolatility = mainMidVolatilityOptional.orElseThrow(() -> new BaseException(BussinessException.E_300103, "主力合约mid没配置"));

        Optional<Volatility> mainBidVolatilityOptional = mainVolatilityList.stream().filter(a -> a.getVolType() == VolTypeEnum.bid).findFirst();
        Volatility mainBidVolatility = mainBidVolatilityOptional.orElseThrow(() -> new BaseException(BussinessException.E_300103, "主力合约bid没配置"));

        VolatilityListDto volatilityListDto = new VolatilityListDto();
        //给副合约赋值
        for (UnderlyingVolatilityFeignDto underlyingVolatilityDto : underlyingVolatilityDtoList) {
            if (underlyingVolatilityDto.getMainContract()) {
                continue;
            }
            List<Volatility> volatilityList = new ArrayList<>();

            mainAskVolatility.setUnderlyingCode(underlyingVolatilityDto.getUnderlyingCode());
            volatilityList.add(mainAskVolatility);
            mainBidVolatility.setUnderlyingCode(underlyingVolatilityDto.getUnderlyingCode());
            volatilityList.add(mainBidVolatility);

            Volatility midVolatility = JSONObject.parseObject(JSONObject.toJSONString(mainMidVolatility), Volatility.class);
            List<VolatityDataDto> data = new ArrayList<>();
            List<VolatityDeltaDataDto> deltaData = new ArrayList<>();
            if (Objects.nonNull(midVolatility.getData())) {
                for (VolatityDataDto volatityDataDto : midVolatility.getData()) {
                    volatityDataDto.setVol(volatityDataDto.getVol().add(underlyingVolatilityDto.getVolOffset()));
                    data.add(volatityDataDto);
                }
                midVolatility.setData(data);
            }
            if (Objects.nonNull(midVolatility.getDeltaData())) {
                for (VolatityDeltaDataDto volatityDeltaDataDto : midVolatility.getDeltaData()) {
                    volatityDeltaDataDto.setVol(volatityDeltaDataDto.getVol().add(underlyingVolatilityDto.getVolOffset()));
                    deltaData.add(volatityDeltaDataDto);
                }
                midVolatility.setDeltaData(deltaData);
            }
            midVolatility.setUnderlyingCode(underlyingVolatilityDto.getUnderlyingCode());
            volatilityList.add(midVolatility);
            volatilityListDto.setVolatilityList(volatilityList);
            //保存副合约波动率数据
            insertOrUpdate(volatilityListDto, Boolean.TRUE);
        }
        return Boolean.TRUE;
    }
}
