package org.orient.otc.quote.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.quote.vo.TradeCloseMngFeignVo;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.dto.trade.RollbackTradeCloseMngDTO;
import org.orient.otc.quote.dto.trade.TradeCloseDTO;
import org.orient.otc.quote.dto.trade.TradeCloseInsertDTO;
import org.orient.otc.quote.dto.trade.TradeCloseQueryDto;
import org.orient.otc.quote.entity.CapitalRecords;
import org.orient.otc.quote.entity.TradeCloseMng;
import org.orient.otc.quote.entity.TradeMng;
import org.orient.otc.quote.entity.TradeObsDate;
import org.orient.otc.quote.enums.TradeCloseTypeEnum;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.TradeCloseMngMapper;
import org.orient.otc.quote.mapper.TradeMngMapper;
import org.orient.otc.quote.mapper.TradeObsDateMapper;
import org.orient.otc.quote.service.CapitalRecordsService;
import org.orient.otc.quote.service.TradeCloseMngService;
import org.orient.otc.quote.vo.trade.HistoryTradeMngVO;
import org.orient.otc.quote.vo.trade.TradeCloseMngVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易平仓服务实现
 */
@Service
public class TradeCloseMngImpl extends ServiceImpl<TradeCloseMngMapper, TradeCloseMng> implements TradeCloseMngService {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private TradeCloseMngMapper tradeCloseMngMapper;
    @Resource
    private TradeMngMapper tradeMngMapper;

    @Value("${isNeedSyncToYl}")
    private Boolean isNeedSyncToYl;
    @Resource
    private UserClient userClient;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    TradeObsDateMapper tradeObsDateMapper;

    @Resource
    private SystemConfigUtil systemConfigUtil;

    @Resource
    UnderlyingManagerClient underlyingManagerClient;

    @Resource
    private CapitalRecordsService capitalRecordsService;

    @Override
    @Transactional
    public List<TradeCloseMng> insertTradeClose(TradeCloseInsertDTO tradeCloseInsertDto) {
        List<TradeCloseMng> result = new ArrayList<>();
        RLock lock = redissonClient.getLock("lock:insertTradeClose");
        lock.lock();
        try {
            List<TradeCloseDTO> tradeCloseDTOList = tradeCloseInsertDto.getTradeCloseDTOList();
            for (TradeCloseDTO tradeCloseDto : tradeCloseDTOList) {
                TradeMng tradeMng = tradeMngMapper.selectOne(new LambdaQueryWrapper<TradeMng>().eq(TradeMng::getTradeCode, tradeCloseDto.getTradeCode()).eq(TradeMng::getIsDeleted, 0));
                //校验是否满足平仓条件
                checkTradeClose(tradeMng, tradeCloseDto,tradeCloseInsertDto.getTradeCloseType());
                List<TradeCloseMng> tradeCloseMngList = tradeCloseMngMapper.selectList(new LambdaQueryWrapper<TradeCloseMng>()
                        .eq(TradeCloseMng::getTradeCode, tradeCloseDto.getTradeCode())
                        .eq(TradeCloseMng::getIsDeleted, 0).orderByDesc(TradeCloseMng::getSort));
                int sort;
                if (Objects.isNull(tradeCloseMngList) || tradeCloseMngList.isEmpty()) {
                    sort = 1;
                } else {
                    TradeCloseMng tradeCloseMng1 = tradeCloseMngList.get(0);
                    sort = tradeCloseMng1.getSort() + 1;
                }
                TradeCloseMng tradeCloseMng = new TradeCloseMng();
                BeanUtils.copyProperties(tradeCloseDto, tradeCloseMng);
                tradeCloseMng.setSort(sort);
                //累计期权特殊处理
                if (tradeMng.getOptionType() == OptionTypeEnum.AICallAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallFixAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutFixAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AICallFixKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIPutFixKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIEnPutKOAccPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIEnCallKOAccPricer) {
                    tradeCloseMng.setProfitLoss(tradeMng.getOptionPremium().add(tradeCloseMng.getClosePrice()).setScale(2, RoundingMode.HALF_UP));
                    tradeCloseMng.setCloseNotionalPrincipal(tradeCloseMng.getCloseVolume().multiply(tradeMng.getEntryPrice()));
                } else if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                        tradeMng.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                    //雪球期权特殊处理上方客户方向*使用平仓波动率和平仓分红率传入so计算的pv*平仓名义本金
                    tradeCloseMng.setProfitLoss(tradeCloseDto.getCloseTotalAmount());
                    tradeCloseMng.setCloseVolume(tradeCloseMng.getCloseNotionalPrincipal().divide(tradeMng.getEntryPrice(), 2, RoundingMode.HALF_UP));
                } else {
                    tradeCloseMng.setProfitLoss(tradeMng.getOptionPremium().add(tradeCloseMng.getClosePrice()).multiply(tradeCloseMng.getCloseVolume()).setScale(2, RoundingMode.HALF_UP));
                    tradeCloseMng.setCloseNotionalPrincipal(tradeCloseMng.getCloseVolume().multiply(tradeMng.getEntryPrice()));
                }
                tradeCloseMngMapper.insert(tradeCloseMng);
                result.add(tradeCloseMng);
                //防止没值报错start
                if (Objects.isNull(tradeMng.getAvailableVolume())) {
                    tradeMng.setAvailableVolume(tradeMng.getTradeVolume());
                }
                if (Objects.isNull(tradeMng.getNotionalPrincipal())) {
                    tradeMng.setNotionalPrincipal(tradeMng.getTradeVolume().multiply(tradeMng.getEntryPrice()));
                }
                if (Objects.isNull(tradeMng.getAvailableNotionalPrincipal())) {
                    tradeMng.setAvailableNotionalPrincipal(tradeMng.getNotionalPrincipal());
                }
                //防止没值报错end
                tradeMng.setAvailableNotionalPrincipal(tradeMng.getAvailableNotionalPrincipal().subtract(tradeCloseMng.getCloseNotionalPrincipal()));
                tradeMng.setAvailableVolume(tradeMng.getAvailableVolume().subtract(tradeCloseMng.getCloseVolume()));
                tradeMng.setTotalProfitLoss(tradeMng.getTotalProfitLoss().add(tradeCloseMng.getProfitLoss()));
                //到期校验
                switch (tradeCloseInsertDto.getTradeCloseType()) {
                    case close:
                        if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                            //雪球期权特殊处理
                            //平仓校验
                            if (tradeMng.getAvailableNotionalPrincipal().compareTo(BigDecimal.ZERO) > 0) {
                                tradeMng.setTradeState(TradeStateEnum.partClosed);
                            } else {
                                tradeMng.setCloseDate(systemConfigUtil.getTradeDay());
                                tradeMng.setTradeState(TradeStateEnum.closed);
                            }
                        } else {
                            //平仓校验
                            if (tradeMng.getAvailableVolume().compareTo(BigDecimal.ZERO) > 0) {
                                tradeMng.setTradeState(TradeStateEnum.partClosed);
                            } else {
                                tradeMng.setCloseDate(systemConfigUtil.getTradeDay());
                                tradeMng.setTradeState(TradeStateEnum.closed);
                            }
                        }
                        break;
                    case execute:
                        if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
                            //雪球期权特殊处理
                            BussinessException.E_300105.assertTrue(tradeMng.getAvailableNotionalPrincipal().compareTo(BigDecimal.ZERO) == 0);
                        } else {
                            BussinessException.E_300105.assertTrue(tradeMng.getAvailableVolume().compareTo(BigDecimal.ZERO) == 0);
                        }
                        tradeMng.setAvailableVolume(BigDecimal.ZERO);
                        tradeMng.setAvailableNotionalPrincipal(BigDecimal.ZERO);
                        tradeMng.setTradeState(TradeStateEnum.expired);
                        tradeMng.setCloseDate(systemConfigUtil.getTradeDay());
                        break;
                    case knockoutTerminate:
                        // 敲出终止
                        BigDecimal barrier;
                        LambdaQueryWrapper<TradeObsDate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                        //按观察日期查询
                        lambdaQueryWrapper.eq(TradeObsDate::getObsDate, tradeCloseDto.getCloseDate());
                        lambdaQueryWrapper.eq(TradeObsDate::getTradeId, tradeMng.getId());
                        lambdaQueryWrapper.eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO);
                        TradeObsDate tradeObsDate = tradeObsDateMapper.selectOne(lambdaQueryWrapper);
                        BussinessException.E_300102.assertTrue(tradeObsDate != null, "非敲出观察日不允许敲出");
                        assert tradeObsDate != null;
                        if (OptionTypeEnum.getSnowBall().contains(tradeMng.getOptionType())) {
                            if (tradeObsDate.getBarrierRelative() != null && tradeObsDate.getBarrierRelative()) {
                                barrier = tradeMng.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDate.getBarrier()));
                            } else {
                                barrier = tradeObsDate.getBarrier();
                            }
                        } else {
                            barrier = tradeMng.getBarrier();
                        }

                        // 雪球看涨
                        if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AICallKOAccPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AICallFixKOAccPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AIEnCallKOAccPricer) {
                            BussinessException.E_300102.assertTrue(tradeCloseDto.getCloseEntryPrice().compareTo(barrier) >= 0
                                    , "标的价格:" + tradeCloseDto.getCloseEntryPrice() + "小于敲出价格:" + barrier + ",不允许敲出终止");
                        }
                        // 雪球看跌
                        if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallPutPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AIPutKOAccPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AIPutFixKOAccPricer ||
                                tradeMng.getOptionType() == OptionTypeEnum.AIEnPutKOAccPricer) {
                            BussinessException.E_300102.assertTrue(tradeCloseDto.getCloseEntryPrice().compareTo(barrier) <= 0
                                    , "标的价格:" + tradeCloseDto.getCloseEntryPrice() + "大于敲出价格:" + barrier + ",不允许敲出终止");
                        }
                        tradeMng.setTradeState(TradeStateEnum.knockoutTerminate);
                        tradeMng.setAvailableVolume(BigDecimal.ZERO);
                        tradeMng.setAvailableNotionalPrincipal(BigDecimal.ZERO);
                        tradeMng.setCloseDate(systemConfigUtil.getTradeDay());
                        break;
                }
                //资金记录保存
                BigDecimal clientAmount= tradeCloseMng.getCloseTotalAmount().negate();
                CapitalRecords capitalRecords= CapitalRecords.builder()
                        .capitalCode(DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN))
                        .money(clientAmount)
                        //当金额大于或者等于0的时候权为权利金收入
                        .direction(clientAmount.compareTo(BigDecimal.ZERO)>=0? CapitalDirectionEnum.exerciseIn:CapitalDirectionEnum.exerciseOut)
                        .happenTime(LocalDateTime.now())
                        .vestingDate(tradeCloseMng.getCloseDate())
                        .clientId(tradeMng.getClientId())
                        .tradeId(tradeMng.getId())
                        .closeId(tradeCloseMng.getId())
                        .tradeCode(tradeMng.getTradeCode())
                        .underlyingCode(tradeMng.getUnderlyingCode())
                        .capitalStatus(CapitalStatusEnum.confirmed)
                        .build();
                capitalRecordsService.save(capitalRecords);
                //更新交易记录表
                tradeMngMapper.updateById(tradeMng);
            }

            for (TradeCloseMng closeMng : result) {
                //记录平仓总额
                String tradeDayStr = closeMng.getCloseDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Object closeAmountObj = stringRedisTemplate.opsForHash().get(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT + tradeDayStr, closeMng.getTradeCode());
                BigDecimal closeAmount = BigDecimal.ZERO;
                if (closeAmountObj != null) {
                    closeAmount = new BigDecimal(closeAmountObj.toString());
                }
                closeAmount = closeAmount.add(closeMng.getCloseTotalAmount().negate());
                stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT + tradeDayStr, closeMng.getTradeCode(), closeAmount.toString());
            }
            if (isNeedSyncToYl) {
                if (tradeCloseInsertDto.getTradeCloseType() == TradeCloseTypeEnum.execute) {
                    rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.SYNC_TOPIC_TRADE_CLOSE_MNG_END, result);
                } else {
                    rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.SYNC_TOPIC_TRADE_CLOSE_MNG_CLOSE, result);
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * 校验是否满足平仓录入条件
     * @param tradeMng      交易记录
     * @param tradeCloseDTO 平仓数据
     */
    private void checkTradeClose(TradeMng tradeMng, TradeCloseDTO tradeCloseDTO,TradeCloseTypeEnum tradeCloseType) {
        BussinessException.E_300102.assertTrue(Objects.nonNull(tradeMng), "tradeCode不存在");
        if (tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer ||
                tradeMng.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer ||
                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer ||
                tradeMng.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer ||
                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallCallPricer ||
                tradeMng.getOptionType() == OptionTypeEnum.AISnowBallPutPricer) {
            BussinessException.E_300102.assertTrue(tradeMng.getAvailableNotionalPrincipal().compareTo(tradeCloseDTO.getCloseNotionalPrincipal()) >= 0, "平仓名义本金不能大于名义本金");
        } else {
            BussinessException.E_300102.assertTrue(tradeMng.getAvailableVolume().compareTo(tradeCloseDTO.getCloseVolume()) >= 0, "平仓数量不能大于存续数量");
        }
        //累计期权特殊处理
        if (OptionTypeEnum.getAccOption().contains(tradeMng.getOptionType())) {
            BussinessException.E_300102.assertTrue(tradeMng.getAvailableVolume().compareTo(tradeCloseDTO.getCloseVolume()) == 0, "平仓数量必须等于存续数量");
        }
        //敲出终止必须全部终止
        if (tradeCloseType.equals(TradeCloseTypeEnum.knockoutTerminate)){
            if (OptionTypeEnum.getSnowBall().contains(tradeMng.getOptionType())){
                BussinessException.E_300102.assertTrue(tradeMng.getAvailableNotionalPrincipal().compareTo(tradeCloseDTO.getCloseNotionalPrincipal()) == 0,"敲出终止必须全部终止");
            }else {
                BussinessException.E_300102.assertTrue(tradeMng.getAvailableVolume().compareTo(tradeCloseDTO.getCloseVolume()) == 0,"敲出终止必须全部终止");
            }

        }
        //到期执行必须全部终止
        if (tradeCloseType.equals(TradeCloseTypeEnum.execute)){
            if (OptionTypeEnum.getSnowBall().contains(tradeMng.getOptionType())){
                BussinessException.E_300102.assertTrue(tradeMng.getAvailableNotionalPrincipal().compareTo(tradeCloseDTO.getCloseNotionalPrincipal()) == 0,"到期执行必须全部终止");
            }else {
                BussinessException.E_300102.assertTrue(tradeMng.getAvailableVolume().compareTo(tradeCloseDTO.getCloseVolume()) == 0,"到期执行必须全部终止");
            }
        }
    }

    /**
     * 批量保存镒链同步回来的交易记录
     * @param list 镒链转换的对象列表
     */
    @Override
    @Transactional
    public boolean saveOrUpdateByCode(List<TradeCloseMngFeignVo> list) {
        LambdaUpdateWrapper<TradeCloseMng> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TradeCloseMng::getTradeCode, list.get(0).getTradeCode());
        updateWrapper.set(TradeCloseMng::getIsDeleted, IsDeletedEnum.YES);
        List<TradeCloseMng> tradeCloseMngList = CglibUtil.copyList(list, TradeCloseMng::new);
        return this.saveBatch(tradeCloseMngList);
    }

    /**
     * 获取未同步的交易记录
     */
    @Override
    public List<TradeCloseMngFeignVo> queryNotSyncTradeList() {
        LambdaQueryWrapper<TradeCloseMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(TradeCloseMng::getIsSync, 1);
        queryWrapper.eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO);
        return this.listVo(queryWrapper, TradeCloseMngFeignVo.class);
    }

    @Override
    public List<TradeCloseMngVO> getTradeCloseMngInfoByCombCode(TradeCloseQueryDto tradeCloseQueryDto) {
        //通过组合代码获取交易代码列表
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getCombCode, tradeCloseQueryDto.getCombCode());
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.select(TradeMng::getTradeCode);
        List<TradeMng> tradeMngList = tradeMngMapper.selectList(tradeMngLambdaQueryWrapper);
        BussinessException.E_300211.assertTrue(!tradeMngList.isEmpty());
        //通过交易代码获取平仓列表
        LambdaQueryWrapper<TradeCloseMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TradeCloseMng::getTradeCode, tradeMngList.stream().map(TradeMng::getTradeCode).collect(Collectors.toList()));
        queryWrapper.eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.orderByAsc(TradeCloseMng::getId);
        List<TradeCloseMngVO> list = this.listVo(queryWrapper, TradeCloseMngVO.class);
        Set<Integer> traderIdSet = list.stream().map(TradeCloseMngVO::getCreatorId).collect(Collectors.toSet());
        Map<Integer, String> traderMap = userClient.getUserMapByIds(traderIdSet);
        for (TradeCloseMngVO vo : list) {
            vo.setCloseUserName(traderMap.get(vo.getCreatorId()));
        }
        return list;
    }

    @Override
    public List<TradeCloseMng> getTradeCloseMngByDate(LocalDate closeDate) {
        LambdaQueryWrapper<TradeCloseMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeCloseMng::getCloseDate, closeDate);
        queryWrapper.eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO);
        return tradeCloseMngMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public String rollbackTradeCloseMng(RollbackTradeCloseMngDTO rollbackTradeCloseMngDto) {
        //查询平仓记录信息
        LambdaQueryWrapper<TradeCloseMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeCloseMng::getId, rollbackTradeCloseMngDto.getTradeCloseMngId());
        queryWrapper.eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO);
        TradeCloseMng tradeCloseMng = tradeCloseMngMapper.selectOne(queryWrapper);
        if (tradeCloseMng == null) {
            BussinessException.E_300300.assertTrue(false);
            return null;
        }

        LocalDate localDate = LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        BussinessException.E_300301.assertTrue(localDate.isEqual(tradeCloseMng.getCloseDate()));
        //删除平仓记录
        tradeCloseMng.setIsDeleted(IsDeletedEnum.YES.getFlag());
        tradeCloseMngMapper.update(tradeCloseMng, queryWrapper);
        String tradeDayStr = tradeCloseMng.getCloseDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //回退的平仓日期为当前交易日时要刷新平仓金额
        if (tradeCloseMng.getCloseDate().isEqual(LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString()))) {
            Object closeAmountObj = stringRedisTemplate.opsForHash().get(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT + tradeDayStr, tradeCloseMng.getTradeCode());
            if (closeAmountObj != null) {
                BigDecimal closeAmount = new BigDecimal(closeAmountObj.toString()).subtract(tradeCloseMng.getCloseTotalAmount().negate());
                stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_CLOSE_TRADE_AMOUNT + tradeDayStr, tradeCloseMng.getTradeCode(), closeAmount.toString());
            }
        }
        LambdaUpdateWrapper<TradeMng> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
        lambdaQueryWrapper.eq(TradeMng::getTradeCode, tradeCloseMng.getTradeCode()).eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        TradeMng tradeMng = tradeMngMapper.selectOne(lambdaQueryWrapper);
        //存续名义本金
        tradeMng.setAvailableNotionalPrincipal(tradeMng.getAvailableNotionalPrincipal().add(tradeCloseMng.getCloseNotionalPrincipal()));
        //存续数量
        tradeMng.setAvailableVolume(tradeMng.getAvailableVolume().add(tradeCloseMng.getCloseVolume()));
        //累计盈亏
        tradeMng.setTotalProfitLoss(tradeMng.getTotalProfitLoss().subtract(tradeCloseMng.getProfitLoss()));
        //取消交易的平仓日期
        lambdaQueryWrapper.set(TradeMng::getCloseDate, null);
        if (OptionTypeEnum.getSnowBall().contains(tradeMng.getOptionType())) {
            //雪球期权特殊处理
            //如果存在数量回退后等于交易数量则更新为交易待确认
            if (tradeMng.getAvailableNotionalPrincipal().compareTo(tradeMng.getNotionalPrincipal()) == 0) {
                lambdaQueryWrapper.set(TradeMng::getTradeState, TradeStateEnum.confirmed);
            } else {
                lambdaQueryWrapper.set(TradeMng::getTradeState, TradeStateEnum.partClosed);
            }
        } else {
            //如果存在数量回退后等于交易数量则更新为交易待确认
            if (tradeMng.getAvailableVolume().compareTo(tradeMng.getTradeVolume()) == 0) {
                lambdaQueryWrapper.set(TradeMng::getTradeState, TradeStateEnum.confirmed);
            } else {
                lambdaQueryWrapper.set(TradeMng::getTradeState, TradeStateEnum.partClosed);
            }
        }
        //资金记录删除
        CapitalRecords capitalRecords= new CapitalRecords();
        capitalRecords.setIsDeleted(IsDeletedEnum.YES.getFlag());
        LambdaQueryWrapper<CapitalRecords> capitalRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        capitalRecordsLambdaQueryWrapper.eq(CapitalRecords::getCloseId,tradeCloseMng.getId());
        capitalRecordsLambdaQueryWrapper.eq(CapitalRecords::getTradeCode,tradeCloseMng.getTradeCode());
        capitalRecordsService.update(capitalRecords,capitalRecordsLambdaQueryWrapper);
        //更新主交易状态
        tradeMngMapper.update(tradeMng, lambdaQueryWrapper);

        return "交易回退成功";
    }

    @Override
    public Page<HistoryTradeMngVO> historyTradeByPage(SettlementReportDTO dto) {

        //查询存在平仓的交易记录
        Page<HistoryTradeMngVO> page =  tradeMngMapper.selectCloseTradeByDateAndClient(new Page<>(dto.getPageNo(),dto.getPageSize()),dto.getStartDate(),dto.getEndDate(), Collections.singletonList(dto.getClientId()));;

        // 获取合约信息
        Set<String> underlyingCodeSet = page.getRecords().stream().map(HistoryTradeMngVO::getUnderlyingCode).collect(Collectors.toSet());
        List<UnderlyingManagerVO> underlyingManagerVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(underlyingCodeSet)) {
            underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet);
        }
        // key = 合约代码 , value = 合约obj
        Map<String, UnderlyingManagerVO> underlyingManagerVOMap = underlyingManagerVOList.stream().collect(Collectors.toMap(item -> item.getUnderlyingCode().toUpperCase(), item -> item, (v1, v2) -> v2));
        //配置数据
        changeDataList(page.getRecords(), underlyingManagerVOMap, new HashMap<>());
        return page;
    }

    @Override
    public List<HistoryTradeMngVO> historyTrade(SettlementReportDTO dto) {
        //查询存在平仓的交易记录
        List<HistoryTradeMngVO> list =  tradeMngMapper.selectCloseTradeByDateAndClient(dto.getStartDate(),dto.getEndDate(), Collections.singletonList(dto.getClientId()));
        if (list.isEmpty()){
            return new ArrayList<>();
        }
        Set<Integer> tradeIdSet = list.stream().map(HistoryTradeMngVO::getId).collect(Collectors.toSet());
        List<TradeObsDate> tradeObsDatesList = tradeObsDateMapper.selectList(new LambdaQueryWrapper<TradeObsDate>()
                .in(TradeObsDate::getTradeId, tradeIdSet)
                .eq(TradeObsDate::getIsDeleted, 0)
                .orderByAsc(TradeObsDate::getObsDate));
        Map<Integer,List<TradeObsDate>> tradeObsDateMap=tradeObsDatesList.stream().collect(Collectors.groupingBy(TradeObsDate::getTradeId));
        // 获取合约信息
        Set<String> underlyingCodeSet = list.stream().map(HistoryTradeMngVO::getUnderlyingCode).collect(Collectors.toSet());
        List<UnderlyingManagerVO> underlyingManagerVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(underlyingCodeSet)) {
            underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet);
        }
        // key = 合约代码 , value = 合约obj
        Map<String, UnderlyingManagerVO> underlyingManagerVOMap = underlyingManagerVOList.stream().collect(Collectors.toMap(item -> item.getUnderlyingCode().toUpperCase(), item -> item, (v1, v2) -> v2));
        //配置数据
        changeDataList(list, underlyingManagerVOMap, tradeObsDateMap);
        return list;
    }

    private void changeDataList(List<HistoryTradeMngVO> list, Map<String, UnderlyingManagerVO> underlyingManagerVOMap, Map<Integer, List<TradeObsDate>> tradeObsDateMap) {
        //参数转换
        list.forEach(item -> {
            //设置标的名称信息
            item.setUnderlyingName(underlyingManagerVOMap.getOrDefault(item.getUnderlyingCode(), new UnderlyingManagerVO()).getUnderlyingName());
            //设置观察日属性
            item.setObsDateList(CglibUtil.copyList(tradeObsDateMap.getOrDefault(item.getId(), new ArrayList<>()), TradeObsDateVO::new));
            item.setOptionTypeName(item.getOptionType().getDesc());
            item.setBuyOrSellName(item.getBuyOrSell().getDesc());
            //平仓盈亏数据库中存储的是我们的盈亏，需要转换为客户的盈亏
            item.setProfitLoss(item.getProfitLoss().negate());
            //成交金额需要做方向处理
            item.setTotalAmount(item.getTotalAmount().negate());
            item.setCloseTotalAmount(item.getCloseTotalAmount().negate());
            item.setOptionCombTypeName(item.getOptionCombType() != null ? item.getOptionCombType().getDesc() : "");
            item.setSettleTypeName(item.getSettleType() != null ? item.getSettleType().getDesc() : "");
            item.setTradeStateName(item.getTradeState() != null ? item.getTradeState().getDesc() : "");
            item.setCallOrPutName(item.getCallOrPut() != null ? item.getCallOrPut().getDesc() : "");
        });
    }
}
