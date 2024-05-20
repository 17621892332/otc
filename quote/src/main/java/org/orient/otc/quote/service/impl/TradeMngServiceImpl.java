package org.orient.otc.quote.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import feign.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.orient.otc.api.client.feign.BankCardInfoClient;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.feign.ClientDutyClient;
import org.orient.otc.api.client.vo.BankCardInfoVO;
import org.orient.otc.api.client.vo.ClientDutyVO;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.file.dto.DownloadDTO;
import org.orient.otc.api.file.enums.FileTypeEnum;
import org.orient.otc.api.file.feign.FileClient;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.orient.otc.api.quote.dto.CloseProfitLossDTO;
import org.orient.otc.api.quote.dto.ProfitLossAppraisementDto;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.api.quote.vo.*;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDTO;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.api.system.feign.DictionaryClient;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.orient.otc.api.user.feign.AssetUnitClient;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.quote.config.multidb.DB;
import org.orient.otc.quote.dto.confirmbook.*;
import org.orient.otc.quote.dto.quote.QuoteMakeUpTotalDTO;
import org.orient.otc.quote.dto.quote.TradeObsDateDto;
import org.orient.otc.quote.dto.settlementbook.BuildSettlementConfirmBookDTO;
import org.orient.otc.quote.dto.settlementbook.DownloadSettlementConfirmBookDTO;
import org.orient.otc.quote.dto.settlementbook.SettlementBook;
import org.orient.otc.quote.dto.settlementbook.SettlementBookData;
import org.orient.otc.quote.dto.trade.*;
import org.orient.otc.quote.entity.*;
import org.orient.otc.quote.enums.SettlementBookTypeEnum;
import org.orient.otc.quote.enums.TradeTypeEnum;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.*;
import org.orient.otc.quote.service.CapitalRecordsService;
import org.orient.otc.quote.service.TradeMngService;
import org.orient.otc.quote.util.BuildTradeConfirmBook;
import org.orient.otc.quote.util.BuildTradeSettlementBook;
import org.orient.otc.quote.util.HutoolUtil;
import org.orient.otc.quote.util.WordToPdfUtil;
import org.orient.otc.quote.vo.daily.TradeMngByDailyVO;
import org.orient.otc.quote.vo.quote.QuoteMakeUpTotalVo;
import org.orient.otc.quote.vo.trade.SettlementConfirmBookVO;
import org.orient.otc.quote.vo.trade.TradeConfirmBookVO;
import org.orient.otc.quote.vo.trade.TradeMngExportVo;
import org.orient.otc.quote.vo.trade.TradeProfitLossByClientVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.orient.otc.quote.util.QuoteUtil.getMakeUpTotal;

/**
 * 交易记录服务实现
 */
@Service
@Slf4j
public class TradeMngServiceImpl extends ServiceImpl<BaseMapper<TradeMng>, TradeMng> implements TradeMngService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ClientClient clientClient;

    @Resource
    private UnderlyingManagerClient underlyingManagerClient;

    @Resource
    private AssetUnitClient assetUnitClient;

    @Resource
    private UserClient userClient;

    @Resource
    private FileClient fileClient;

    @Resource
    private DictionaryClient dictionaryClient;

    @Resource
    private BankCardInfoClient bankCardInfoClient;

    @Resource
    private ClientDutyClient clientDutyClient;
    @Resource
    private TradeMngMapper tradeMngMapper;

    @Resource
    private TradeCloseMngMapper tradeCloseMngMapper;

    @Resource
    private TradeObsDateMapper tradeObsDateMapper;

    @Resource
    private TradeRiskInfoMapper tradeRiskInfoMapper;

    @Resource
    private TradeSnowballOptionMapper tradeSnowballOptionMapper;

    @Resource
    private TradeContractDocumentMapper tradeContractDocumentMapper;

    @Resource
    private TradeContractRelMapper tradeContractRelMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    @Resource
    private CapitalRecordsService capitalRecordsService;


    @Value("${isNeedSyncToYl}")
    private Boolean isNeedSyncToYl;

    @Resource
    private  SystemConfigUtil systemConfigUtil;

    @Resource
    private  BuildTradeConfirmBook buildTradeConfirmBook;
    @Resource
    private BuildTradeSettlementBook buildTradeSettlementBook;

    @Resource
    private  RiskEarlyWarningMapper riskEarlyWarningMapper;
    @Resource
    private SystemDataChangeRecordClient systemDataChangeRecordClient;
    @Resource
    private  CapitalRecordsMapper capitalRecordsMapper;

    @Resource
    private ObjectEqualsUtil objectEqualsUtil;

    @Resource
    private WordToPdfUtil wordToPdfUtil;


    @Override
    @Transactional
    public List<TradeMngVO> insertTrade(TradeInsertDTO tradeInsertDto) {
        //如果是网页端新增的交易，需要转换交易方向
        if (tradeInsertDto.getTradeDirection()!=null && tradeInsertDto.getTradeDirection()==1){
            for (TradeMngDTO tradeMngDTO: tradeInsertDto.getTradeList()){
                //买卖方向转换
                tradeMngDTO.setBuyOrSell(tradeMngDTO.getBuyOrSell()==BuyOrSellEnum.buy?BuyOrSellEnum.sell:BuyOrSellEnum.buy);
                //期权价格
                if (tradeMngDTO.getOptionPremium()!=null){
                    tradeMngDTO.setOptionPremium(tradeMngDTO.getOptionPremium().negate());
                }
                //期权费率
                if (tradeMngDTO.getOptionPremiumPercent()!=null){
                    tradeMngDTO.setOptionPremiumPercent(tradeMngDTO.getOptionPremiumPercent().negate());
                }
                //成交金额
                if (tradeMngDTO.getTotalAmount()!=null){
                    tradeMngDTO.setTotalAmount(tradeMngDTO.getTotalAmount().negate());
                }
            }
        }
        List<TradeMngDTO> tradeMngDTOList = tradeInsertDto.getTradeList();
        RLock lock = redissonClient.getLock("lock:insertTrade");
        lock.lock();
        try {
            String tradeCode = tradeInsertDto.getTradeCode();
            Integer assetId = tradeInsertDto.getAssetId();
            Integer traderId = tradeInsertDto.getTraderId();
            if (tradeInsertDto.getTradeType() == TradeTypeEnum.makeUp) {
                checkMakeUp(tradeMngDTOList);
                //如果是组合的话，comCode自定义时，每个交易的comCode必须一致（目前一次只能提交一个组合）
                for (int i = 0; i < tradeMngDTOList.size(); i++) {
                    TradeMngDTO trade = tradeMngDTOList.get(i);
                    String combCode;
                    if (StringUtils.isBlank(tradeCode)) {
                        //计算组合编号 20230407-DZRH-01
                        combCode = getCombCode(trade.getTradeDate(), trade.getClientId());
                    } else {
                        combCode = tradeCode;
                    }
                    trade.setCombCode(combCode);
                    trade.setTradeCode(combCode + "-" + (i + 1));
                    trade.setTraderId(traderId);
                    trade.setAssetId(assetId);

                    checkTradeInfo(trade, Boolean.TRUE);
                }
                for (TradeMngDTO dto : tradeMngDTOList) {
                    TradeMng trade = CglibUtil.copy(dto, TradeMng.class);
                    trade.setTotalProfitLoss(BigDecimal.ZERO);
                    trade.setTradeState(TradeStateEnum.confirmed);
                    trade.setAvailableVolume(dto.getTradeVolume());
                    trade.setAvailableNotionalPrincipal(dto.getNotionalPrincipal());
                    tradeMngMapper.insert(trade);
                    dto.setId(trade.getId());
                }
                // 保存风险预警信息 (组合只插一条)
                TradeMngDTO firstDto = tradeMngDTOList.get(0);
                if (StringUtils.isNotBlank(firstDto.getWarningMsg())) {
                    RiskEarlyWarning entity = new RiskEarlyWarning();
                    entity.setTraderId(firstDto.getTraderId());
                    entity.setClientId(firstDto.getClientId());
                    // 前段未传组合信息时 , 风险预警中的期权类型默认为自定义
                    if (firstDto.getOptionCombType() == null) {
                        entity.setOptionType(OptionCombTypeEnum.customize.name());
                    } else {
                        entity.setOptionType(firstDto.getOptionCombType().name());
                    }
                    entity.setUnderlyingCode(firstDto.getUnderlyingCode());
                    entity.setWarningText(firstDto.getWarningMsg());
                    entity.setType("交易录入");
                    entity.setWaringStatus(0);
                    entity.setWaringTime(LocalDateTime.now());
                    riskEarlyWarningMapper.insert(entity);
                }

            } else if (tradeInsertDto.getTradeType() == TradeTypeEnum.singleLeg) {
                String tradeCodeBak = tradeCode;
                for (int i = 0; i < tradeMngDTOList.size(); i++) {
                    TradeMngDTO tradeMngDTO = tradeMngDTOList.get(i);
                    if (StringUtils.isBlank(tradeCode)) {
                        //计算组合编号 20230407-DZRH-01(单腿组合编码和交易编码一致)
                        tradeCodeBak = getCombCode(tradeMngDTO.getTradeDate(), tradeMngDTO.getClientId());
                    } else {
                        //如果保存多笔单腿并且自定义交易编码，那么就在自定义的交易编码后面带上_i
                        if (tradeMngDTOList.size() > 1) {
                            tradeCodeBak = tradeCode + "_" + (i + 1);
                        }
                    }
                    tradeMngDTO.setCombCode(tradeCodeBak);
                    tradeMngDTO.setTradeCode(tradeCodeBak);
                    tradeMngDTO.setTraderId(traderId);
                    tradeMngDTO.setAssetId(assetId);
                    checkTradeInfo(tradeMngDTO, Boolean.TRUE);
                    TradeMng trade = CglibUtil.copy(tradeMngDTO, TradeMng.class);
                    trade.setTotalProfitLoss(BigDecimal.ZERO);
                    trade.setTradeState(TradeStateEnum.confirmed);
                    trade.setAvailableVolume(tradeMngDTO.getTradeVolume());
                    trade.setAvailableNotionalPrincipal(tradeMngDTO.getNotionalPrincipal());
                    tradeMngMapper.insert(trade);
                    tradeMngDTO.setId(trade.getId());


                    // 保存风险预警信息 (单腿循环插入)
                    if (StringUtils.isNotBlank(tradeMngDTO.getWarningMsg())) {
                        RiskEarlyWarning entity = new RiskEarlyWarning();
                        entity.setTraderId(tradeMngDTO.getTraderId());
                        entity.setClientId(tradeMngDTO.getClientId());
                        entity.setOptionType(tradeMngDTO.getOptionType().name());
                        entity.setUnderlyingCode(tradeMngDTO.getUnderlyingCode());
                        entity.setWarningText(tradeMngDTO.getWarningMsg());
                        entity.setType("交易录入");
                        entity.setWaringStatus(0);
                        entity.setWaringTime(LocalDateTime.now());
                        riskEarlyWarningMapper.insert(entity);
                    }

                }
            }
            for (TradeMngDTO tradeMngDTO : tradeMngDTOList) {
                //资金记录保存
               BigDecimal clientAmount= tradeMngDTO.getTotalAmount().negate();
                //暂时还是用0因为雪球的平仓时间是不固定的，只能到平仓的时候才能知道对应费率是多少
//                if (tradeMngDTO.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer || tradeMngDTO.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer) {
//                    //成交金额=如果是年化期权费率，则相应金额等于期权费率*名义本金*期权期限的日历日天数/365:如果是绝对期权费率，则相应金额等于期权费率*名义本金
//                    clientAmount = BigDecimalUtil.percentageToBigDecimal(tradeMngDTO.getOptionPremiumPercent()).multiply(tradeMngDTO.getNotionalPrincipal()).multiply(
//                            tradeMngDTO.getOptionPremiumPercentAnnulized() ? tradeMngDTO.getTtm().divide(new BigDecimal(365),6, RoundingMode.HALF_UP) : BigDecimal.ONE
//                    );
//                    //如果是客户买入的时候期权费率为正时，客户应支付权利金
//                    if (tradeMngDTO.getBuyOrSell()==BuyOrSellEnum.buy){
//                        clientAmount=clientAmount.negate();
//                    }
//
//                }
                CapitalRecords capitalRecords= CapitalRecords.builder()
                        .capitalCode(DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN))
                        .money(clientAmount)
                        //当金额大于或者等于0的时候权为权利金收入
                        .direction(clientAmount.compareTo(BigDecimal.ZERO)>=0?CapitalDirectionEnum.premiumIn:CapitalDirectionEnum.premiumOut)
                        .happenTime(LocalDateTime.now())
                        .vestingDate(tradeMngDTO.getTradeDate())
                        .clientId(tradeMngDTO.getClientId())
                        .tradeId(tradeMngDTO.getId())
                        .tradeCode(tradeMngDTO.getTradeCode())
                        .underlyingCode(tradeMngDTO.getUnderlyingCode())
                        .capitalStatus(CapitalStatusEnum.confirmed)
                        .build();
                capitalRecordsService.save(capitalRecords);
                //处理观察日历
                if (Objects.nonNull(tradeMngDTO.getTradeObsDateList()) && !tradeMngDTO.getTradeObsDateList().isEmpty()) {
                    for (TradeObsDateDto tradeObsDateDto : tradeMngDTO.getTradeObsDateList()) {
                        TradeObsDate tradeObsDate = new TradeObsDate();
                        BeanUtils.copyProperties(tradeObsDateDto, tradeObsDate);
                        tradeObsDate.setTradeId(tradeMngDTO.getId());
                        tradeObsDate.setUnderlyingCode(tradeMngDTO.getUnderlyingCode());
                        tradeObsDateMapper.insert(tradeObsDate);
                    }
                }
                //保存雪球相关信息
                if (OptionTypeEnum.getSnowBall().contains(tradeMngDTO.getOptionType())) {
                    saveSnowBallInfo(tradeMngDTO);
                }
                //记录今日开仓金额
                String tradeDayStr = tradeMngDTO.getTradeDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT + tradeDayStr, tradeMngDTO.getTradeCode(), tradeMngDTO.getTotalAmount().negate().toString());
            }
            if (isNeedSyncToYl) {
                rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.SYNC_TOPIC_TRADE_MNG_ADD, tradeMngDTOList);
            }
        } finally {
            lock.unlock();
        }
        return CglibUtil.copyList(tradeMngDTOList, TradeMngVO::new);
    }

    /**
     * 保存雪球相关信息
     * @param tradeMngDTO 主的DTO信息
     */
    private void saveSnowBallInfo(TradeMngDTO tradeMngDTO) {
        TradeSnowballOption tradeSnowballOption = CglibUtil.copy(tradeMngDTO, TradeSnowballOption.class);
        tradeSnowballOption.setId(null);
        tradeSnowballOption.setTradeId(tradeMngDTO.getId());
        //保本雪球返息率等于期权费率取反，并且均为年化
        if (tradeMngDTO.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer
        ||tradeMngDTO.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer) {
            tradeSnowballOption.setReturnRateStructValue(BigDecimalUtil.bigDecimalToPercentage(tradeMngDTO.getOptionPremiumPercent()).negate());
            tradeSnowballOption.setReturnRateAnnulized(Boolean.TRUE);
        }
        tradeSnowballOptionMapper.insert(tradeSnowballOption);
    }

    private void checkMakeUp(List<TradeMngDTO> tradeMngDTOList) {
        BussinessException.E_300102.assertTrue(tradeMngDTOList.stream().map(TradeMngDTO::getOptionCombType).distinct().count() == 1, "不允许混合录入组合");
        OptionCombTypeEnum combTypeEnum = tradeMngDTOList.get(0).getOptionCombType();
        if (combTypeEnum == null) {
            return;
        }
        switch (combTypeEnum) {
            case bullMarketSpread:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 2 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1, "牛市价差看涨看跌或客户买入卖出选择错误");
                break;
            case bearMarketSpread:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 2 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1, "熊市价差看涨看跌或客户买入卖出选择错误");
                break;
            case collarSpread:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 2 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1, "领式结构看涨看跌或客户买入卖出选择错误");
                break;
            case straddle:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 2 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put).count() == 1 && (tradeMngDTOList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 2 || tradeMngDTOList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 2), "跨式结构看涨看跌或客户买入卖出选择错误");
            case wideStrangle:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 2 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put).count() == 1 && (tradeMngDTOList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 2 || tradeMngDTOList.stream().filter(a -> a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 2), "宽跨式结构看涨看跌或客户买入卖出选择错误");
                break;
            case callTriCollar:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 3 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1, "看涨海鸥看涨看跌或客户买入卖出选择错误");
                break;
            case putTriCollar:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 3 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1, "看跌海鸥看涨看跌或客户买入卖出选择错误");
                break;
            case butterflySpread:
                BussinessException.E_300102.assertTrue(tradeMngDTOList.size() == 4 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.put && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.buy).count() == 1 && tradeMngDTOList.stream().filter(a -> a.getCallOrPut() == CallOrPutEnum.call && a.getBuyOrSell() == BuyOrSellEnum.sell).count() == 1, "蝶式结构看涨看跌或客户买入卖出选择错误");
                break;
        }
    }

    /**
     * 校验交易参数是否合法
     * @param dto   交易信息
     * @param isAdd 是否新增交易
     */
    private void checkTradeInfo(TradeMngDTO dto, Boolean isAdd) {
        //合约代码修改为大写
        dto.setUnderlyingCode(dto.getUnderlyingCode().toUpperCase());
        //校验交易日期是否大于到期日期
        BussinessException.E_300109.assertTrue(!dto.getTradeDate().isAfter(dto.getMaturityDate()));

        if (dto.getOptionType() != OptionTypeEnum.AIForwardPricer) {
            BussinessException.E_300102.assertTrue(dto.getMidVol() != null, "MidVol不能为空");
        }
        if (dto.getSettleType() == SettleTypeEnum.mix) {
            BussinessException.E_300102.assertTrue(OptionTypeEnum.checkHaveMix(dto.getOptionType()), "不支持混合方式结算");
        }
        if (OptionTypeEnum.getHaveObsType().contains(dto.getOptionType())) {
            BussinessException.E_300101.assertTrue(Objects.nonNull(dto.getTradeObsDateList()) && !dto.getTradeObsDateList().isEmpty(), "观察日历不能为空");
        }
        switch (dto.getOptionType()) {
            case AICallAccPricer:
            case AICallFixAccPricer:
            case AICallKOAccPricer:
            case AICallFixKOAccPricer:
                if(dto.getBarrier()==null){
                    dto.setBarrier(BigDecimal.ZERO);
                }
                BussinessException.E_300102.assertTrue( dto.getBarrier().equals(BigDecimal.ZERO)
                                || dto.getBarrier().compareTo(dto.getStrike()) > 0,
                        "敲出价格必须为空或者大于行权价格");
                break;
            case AIPutAccPricer:
            case AIPutKOAccPricer:
            case AIPutFixKOAccPricer:
            case AIPutFixAccPricer:
                if(dto.getBarrier()==null){
                    dto.setBarrier(BigDecimal.ZERO);
                }
                BussinessException.E_300102.assertTrue(dto.getBarrier().equals(BigDecimal.ZERO)
                                || dto.getBarrier().compareTo(dto.getStrike()) < 0,
                        "敲出价格必须为空或者小于行权价格");
                break;
            case AILimitLossesSnowBallCallPricer:
                BigDecimal callStrike2 = dto.getStrike2OnceKnockedinValue();
                if (dto.getStrike2OnceKnockedinRelative()) {
                    callStrike2 = BigDecimalUtil.percentageToBigDecimal(dto.getStrike2OnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BigDecimal callStrike = dto.getStrikeOnceKnockedinValue();
                if (dto.getStrikeOnceKnockedinRelative()) {
                    callStrike = BigDecimalUtil.percentageToBigDecimal(dto.getStrikeOnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BussinessException.E_300102.assertTrue(callStrike2.compareTo(callStrike) <= 0, "看涨雪球行权价格2必须小于或等于行权价格1");
                break;
            case AILimitLossesSnowBallPutPricer:
                BigDecimal putStrike2 = dto.getStrike2OnceKnockedinValue();
                if (dto.getStrike2OnceKnockedinRelative()) {
                    putStrike2 = BigDecimalUtil.percentageToBigDecimal(dto.getStrike2OnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BigDecimal putStrike = dto.getStrikeOnceKnockedinValue();
                if (dto.getStrikeOnceKnockedinRelative()) {
                    putStrike = BigDecimalUtil.percentageToBigDecimal(dto.getStrikeOnceKnockedinValue()).multiply(dto.getEntryPrice());
                }
                BussinessException.E_300102.assertTrue(putStrike2.compareTo(putStrike) >= 0, "看跌雪球行权价格2必须大于或等于行权价格1");
                break;
        }
        //校验合约信息
        UnderlyingManagerVO underlyingManagerVo = underlyingManagerClient.getUnderlyingByCode(dto.getUnderlyingCode());
        if (underlyingManagerVo.getExpireDate() != null) {
            BussinessException.E_300106.assertTrue(!dto.getMaturityDate().isAfter(underlyingManagerVo.getExpireDate()), underlyingManagerVo.getExpireDate());
        }
        if (isAdd) {
            //组合编号是否重复
            Long count = tradeMngMapper.selectCount(new LambdaQueryWrapper<TradeMng>().eq(TradeMng::getCombCode, dto.getCombCode()).eq(TradeMng::getIsDeleted, 0));
            BussinessException.E_300102.assertTrue(count == 0, "comCode不能重复");
        }
        if (dto.getOptionType() == OptionTypeEnum.AISnowBallCallPricer || dto.getOptionType() == OptionTypeEnum.AISnowBallPutPricer || dto.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer || dto.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer || dto.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer || dto.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer) {
            //雪球的成交金额设置为0
            dto.setTotalAmount(BigDecimal.ZERO);
//            BussinessException.E_300102.assertTrue(checkRelative(dto), "所有价格必须都为相对或绝对");
        } else {
//            //除了雪球期权，其它期权的行权价格均需要大于0
//            if (dto.getOptionType() != OptionTypeEnum.AIForwardPricer) {
//                BussinessException.E_300102.assertTrue(dto.getStrike().compareTo(BigDecimal.ZERO) > 0, "行权价格必须大于0");
//            }
            //名义本金=成交数量*入场价格
            dto.setNotionalPrincipal(dto.getTradeVolume().multiply(dto.getEntryPrice()));
        }
        //熔断累计期权校验
        if (OptionTypeEnum.getOrdinaryKOOptionType().contains(dto.getOptionType())) {
            BussinessException.E_300102.assertTrue(dto.getKnockoutRebate() != null, "熔断累计期权敲出赔付不能为空");
            BussinessException.E_300102.assertTrue(dto.getExpireMultiple() != null, "熔断累计期权到期倍数不能为空");
        }
        //观察日历校验
        if (dto.getTradeObsDateList() != null && !dto.getTradeObsDateList().isEmpty()) {
            BussinessException.E_300102.assertTrue(!dto.getTradeDate().isAfter(dto.getTradeObsDateList().get(0).getObsDate()), "第一个观察日必须大于或等于交易日");
            BussinessException.E_300102.assertTrue(!dto.getMaturityDate().isBefore(dto.getTradeObsDateList().get(dto.getTradeObsDateList().size() - 1).getObsDate()), "最后一个观察日必须小于或等于到期日");
        }
    }

    /**
     * 校验是否都为相对或者绝对
     * @param quoteDto 计算参数
     * @return true 校验通过 false校验失败
     */
    private Boolean checkRelative(TradeMngDTO quoteDto) {
        List<Boolean> relativeList = quoteDto.getTradeObsDateList().stream().map(TradeObsDateDto::getBarrierRelative).collect(Collectors.toList());
        relativeList.add(quoteDto.getKnockinBarrierRelative());
        relativeList.add(quoteDto.getStrikeOnceKnockedinRelative());
        relativeList.add(quoteDto.getStrike2OnceKnockedinRelative());
        relativeList = relativeList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        long count = relativeList.stream().distinct().count();
        return count == 1;
    }

    @Override
    @Transactional
    public String updateTrade(TradeUpdateDTO tradeUpdateDTO) {
        List<TradeMngDTO> tradeMngDTOList = tradeUpdateDTO.getTradeList();
        //如果是网页端新增的交易，需要转换交易方向
        if (tradeUpdateDTO.getTradeDirection()!=null && tradeUpdateDTO.getTradeDirection()==1) {
            for (TradeMngDTO tradeMngDTO : tradeMngDTOList) {
                //买卖方向转换
                tradeMngDTO.setBuyOrSell(tradeMngDTO.getBuyOrSell() == BuyOrSellEnum.buy ? BuyOrSellEnum.sell : BuyOrSellEnum.buy);
                //期权价格
                if (tradeMngDTO.getOptionPremium() != null) {
                    tradeMngDTO.setOptionPremium(tradeMngDTO.getOptionPremium().negate());
                }
                //期权费率
                if (tradeMngDTO.getOptionPremiumPercent() != null) {
                    tradeMngDTO.setOptionPremiumPercent(tradeMngDTO.getOptionPremiumPercent().negate());
                }
                //成交金额
                if (tradeMngDTO.getTotalAmount() != null) {
                    tradeMngDTO.setTotalAmount(tradeMngDTO.getTotalAmount().negate());
                }
            }
        }

        for (TradeMngDTO tradeMngDTO : tradeMngDTOList) {
            BussinessException.E_300202.assertTrue(systemConfigUtil.getTradeDay().isEqual(tradeMngDTO.getTradeDate()));
            BussinessException.E_300102.assertTrue(Objects.nonNull(tradeMngDTO.getTradeCode()), "tradeCode不能为空");
            BussinessException.E_300201.assertTrue(tradeMngDTO.getTradeObsDateList() == null || tradeMngDTO.getTradeObsDateList().isEmpty()
                    || tradeMngDTO.getTradeObsDateList().stream().map(TradeObsDateDto::getPrice).collect(Collectors.toList()).stream().noneMatch(Objects::nonNull));
            //如果还有平仓不允许修改
            List<TradeCloseMng> tradeCloseMngList = tradeCloseMngMapper.selectList(new LambdaQueryWrapper<TradeCloseMng>().select(TradeCloseMng::getId).eq(TradeCloseMng::getTradeCode, tradeMngDTO.getTradeCode()).eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO));
            if (!tradeCloseMngList.isEmpty()) {
                BussinessException.E_300107.doThrow();
            }
            if (tradeMngDTO.getOptionCombType() != null) {
                checkMakeUp(tradeMngDTOList);
            }
            //校验交易记录是否符合录入规则
            checkTradeInfo(tradeMngDTO, Boolean.FALSE);
            LambdaQueryWrapper<TradeMng> eq = new LambdaQueryWrapper<TradeMng>().eq(TradeMng::getTradeCode, tradeMngDTO.getTradeCode()).eq(TradeMng::getIsDeleted, 0);
            TradeMng tm = tradeMngMapper.selectOne(eq);
            //系统的ID
            tradeMngDTO.setId(tm.getId());
            TradeMng tradeMng = CglibUtil.copy(tradeMngDTO, TradeMng.class);
            //计算存续数量金额
            tradeMng.setAvailableVolume(tradeMngDTO.getTradeVolume());
            tradeMng.setAvailableNotionalPrincipal(tradeMngDTO.getNotionalPrincipal());
            tradeMng.setTotalProfitLoss(BigDecimal.ZERO);
            tradeMngMapper.update(tradeMng, eq);
            // 添加交易修改内容
            List<DiffObjectVO> diffObjectVOList=  objectEqualsUtil.equalsObjectField(tm,tradeMng);

            //设置雪球相关属性
            if (OptionTypeEnum.getSnowBall().contains(tradeMng.getOptionType())) {
                LambdaQueryWrapper<TradeSnowballOption> snowballOptionLambdaQueryWrapper = new LambdaQueryWrapper<TradeSnowballOption>()
                        .eq(TradeSnowballOption::getTradeId, tradeMngDTO.getId())
                        .eq(TradeSnowballOption::getIsDeleted, IsDeletedEnum.NO);
                TradeSnowballOption db = tradeSnowballOptionMapper.selectOne(snowballOptionLambdaQueryWrapper);
                TradeSnowballOption tradeSnowballOption = new TradeSnowballOption();
                BeanUtil.copyProperties(tradeMngDTO, tradeSnowballOption);
                tradeSnowballOptionMapper.update(tradeSnowballOption, snowballOptionLambdaQueryWrapper);
                //添加雪球修改内容
                diffObjectVOList.addAll(objectEqualsUtil.equalsObjectField(db,tradeSnowballOption));
            }
            //处理交易日历
            if (tradeMngDTO.getTradeObsDateList() != null && !tradeMngDTO.getTradeObsDateList().isEmpty()) {
                LambdaQueryWrapper<TradeObsDate> tradeObsDateLambdaQueryWrapper = new LambdaQueryWrapper<>();
                tradeObsDateLambdaQueryWrapper.eq(TradeObsDate::getTradeId, tradeMngDTO.getId());
                tradeObsDateLambdaQueryWrapper.eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO);
                List<TradeObsDate> tradeObsDateList = tradeObsDateMapper.selectList(tradeObsDateLambdaQueryWrapper);
                List<LocalDate> dbObsDateList = tradeObsDateList.stream().map(TradeObsDate::getObsDate).collect(Collectors.toList());
                List<LocalDate> dtoObsDateList = tradeMngDTO.getTradeObsDateList().stream().map(TradeObsDateDto::getObsDate).collect(Collectors.toList());
                //剔除可能删除的列表
                List<Integer> delIdList = tradeObsDateList.stream().filter(tradeObsDate -> !dtoObsDateList.contains(tradeObsDate.getObsDate())).map(TradeObsDate::getId).collect(Collectors.toList());
                if (!delIdList.isEmpty()) {
                    LambdaQueryWrapper<TradeObsDate> delIdWrapper = new LambdaQueryWrapper<>();
                    delIdWrapper.eq(TradeObsDate::getTradeId, tradeMngDTO.getId());
                    delIdWrapper.in(TradeObsDate::getId, delIdList);
                    delIdWrapper.eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO);
                    TradeObsDate del = new TradeObsDate();
                    del.setIsDeleted(IsDeletedEnum.YES.getFlag());
                    tradeObsDateMapper.update(del, delIdWrapper);
                }
                //新增的交易日列表
                List<TradeObsDateDto> addTradeObsDateList = tradeMngDTO.getTradeObsDateList().stream().filter(tradeObsDate -> !dbObsDateList.contains(tradeObsDate.getObsDate())).collect(Collectors.toList());
                for (TradeObsDateDto tradeObsDateDto : addTradeObsDateList) {
                    TradeObsDate tradeObsDate = new TradeObsDate();
                    BeanUtils.copyProperties(tradeObsDateDto, tradeObsDate);
                    tradeObsDate.setTradeId(tradeMngDTO.getId());
                    tradeObsDate.setUnderlyingCode(tradeMngDTO.getUnderlyingCode());
                    tradeObsDateMapper.insert(tradeObsDate);
                }
                //更新的交易日列表
                tradeMngDTO.getTradeObsDateList().removeAll(addTradeObsDateList);
                for (TradeObsDateDto obsDateDto : tradeMngDTO.getTradeObsDateList()) {
                    LambdaQueryWrapper<TradeObsDate> updateWrapper = new LambdaQueryWrapper<>();
                    updateWrapper.eq(TradeObsDate::getTradeId, tradeMngDTO.getId());
                    updateWrapper.eq(TradeObsDate::getObsDate, obsDateDto.getObsDate());
                    updateWrapper.eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO);
                    TradeObsDate update = new TradeObsDate();
                    BeanUtils.copyProperties(obsDateDto, update);
                    tradeObsDateMapper.update(update, updateWrapper);
                }
            }

            //添加更新记录
            TradeDataChangeRecordDTO tradeDataChangeRecordDTO = new TradeDataChangeRecordDTO();
            tradeDataChangeRecordDTO.setTradeCode(tm.getTradeCode());
            tradeDataChangeRecordDTO.setClientId(tm.getClientId());
            tradeDataChangeRecordDTO.setAssetunitId(tm.getAssetId());
            tradeDataChangeRecordDTO.setChangeType(DataChangeTypeEnum.update);
            tradeDataChangeRecordDTO.setChangeFields(JSONObject.toJSONString(diffObjectVOList));
            systemDataChangeRecordClient.addTradeDataChangeRecord(tradeDataChangeRecordDTO);
        }

        for (TradeMngDTO tradeMngDTO : tradeMngDTOList) {
            //资金记录保存
            BigDecimal clientAmount= tradeMngDTO.getTotalAmount().negate();
            CapitalRecords capitalRecords= CapitalRecords.builder()
                    .capitalCode(DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN))
                    .money(clientAmount)
                    //当金额大于或者等于0的时候权为权利金收入
                    .direction(clientAmount.compareTo(BigDecimal.ZERO)>=0?CapitalDirectionEnum.premiumIn:CapitalDirectionEnum.premiumOut)
                    .happenTime(LocalDateTime.now())
                    .vestingDate(tradeMngDTO.getTradeDate())
                    .clientId(tradeMngDTO.getClientId())
                    .tradeId(tradeMngDTO.getId())
                    .tradeCode(tradeMngDTO.getTradeCode())
                    .underlyingCode(tradeMngDTO.getUnderlyingCode())
                    .capitalStatus(CapitalStatusEnum.confirmed)
                    .build();
            LambdaQueryWrapper<CapitalRecords> capitalRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            capitalRecordsLambdaQueryWrapper.eq(CapitalRecords::getTradeCode,tradeMngDTO.getTradeCode());
            capitalRecordsLambdaQueryWrapper.eq(CapitalRecords::getTradeId,tradeMngDTO.getId());
            capitalRecordsLambdaQueryWrapper.eq(CapitalRecords::getIsDeleted,IsDeletedEnum.NO);
            capitalRecordsService.update(capitalRecords,capitalRecordsLambdaQueryWrapper);
            //记录今日开仓金额(如交易日期为当前交易日时)
            if (tradeMngDTO.getTradeDate().isEqual(LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString()))) {
                String tradeDayStr = tradeMngDTO.getTradeDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                stringRedisTemplate.opsForHash().put(RedisAdapter.TODAY_OPEN_TRADE_AMOUNT + tradeDayStr, tradeMngDTO.getTradeCode(), tradeMngDTO.getTotalAmount().negate().toString());
            }
        }
        if (isNeedSyncToYl) {
            final CopyOptions copyOptions = CopyOptions.create();
            copyOptions.setOverride(false);
            copyOptions.setIgnoreNullValue(true);
            List<TradeMng> tradeMngList = tradeMngMapper.selectList(new LambdaQueryWrapper<TradeMng>().in(TradeMng::getTradeCode, tradeMngDTOList.stream().map(TradeMngDTO::getTradeCode).collect(Collectors.toList())).eq(TradeMng::getIsDeleted, IsDeletedEnum.NO));
            List<TradeMngVO> tradeMngVOList = CglibUtil.copyList(tradeMngList, TradeMngVO::new, (s, t) -> {
                //设置观察日属性
                List<TradeObsDate> tradeObsDates = tradeObsDateMapper.selectList(new LambdaQueryWrapper<TradeObsDate>().eq(TradeObsDate::getTradeId, s.getId()).eq(TradeObsDate::getIsDeleted, 0).orderByAsc(TradeObsDate::getObsDate));
                t.setTradeObsDateList(CglibUtil.copyList(tradeObsDates, TradeObsDateVO::new));
                //设置雪球相关属性
                if (OptionTypeEnum.getSnowBall().contains(s.getOptionType())) {
                    TradeSnowballOption tradeSnowballOption = tradeSnowballOptionMapper.selectOne(new LambdaQueryWrapper<TradeSnowballOption>().eq(TradeSnowballOption::getTradeId, s.getId()).eq(TradeSnowballOption::getIsDeleted, IsDeletedEnum.NO));
                    BeanUtil.copyProperties(tradeSnowballOption, t, copyOptions);

                }
            });
            rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.SYNC_TOPIC_TRADE_MNG_UPDATE, tradeMngVOList);
        }
        return "update success";
    }

    private Map<Integer, TradeSnowballOption> getSnowballMapByTradeIdList(List<Integer> tradeIdList) {
        List<TradeSnowballOption> tradeSnowballOptionList = tradeSnowballOptionMapper.selectList(new LambdaQueryWrapper<TradeSnowballOption>()
                .in(TradeSnowballOption::getTradeId, tradeIdList)
                .eq(TradeSnowballOption::getIsDeleted, IsDeletedEnum.NO));
        return tradeSnowballOptionList.stream().collect(Collectors.toMap(TradeSnowballOption::getTradeId, Function.identity()));
    }

    @Override
    public List<TradeMngVO> getTradeInfo(CombCodeDTO combCodeDTO) {
        List<TradeMngVO> result = new ArrayList<>();
        List<TradeMngVO> list = this.listVo(new LambdaQueryWrapper<TradeMng>().eq(TradeMng::getCombCode, combCodeDTO.getCombCode()).eq(TradeMng::getIsDeleted, 0), TradeMngVO.class);
        BussinessException.E_300211.assertTrue(!list.isEmpty());
        if (list.size() > 1) {
            //组合求和
            TradeMngVO trade = new TradeMngVO();
            BeanUtils.copyProperties(list.get(0), trade);
            List<QuoteMakeUpTotalDTO> quoteMakeUpTotalDTOS = JSONArray.parseArray(JSONArray.toJSONString(list), QuoteMakeUpTotalDTO.class);
            QuoteMakeUpTotalVo makeUpTotal = getMakeUpTotal(quoteMakeUpTotalDTOS);
            BeanUtils.copyProperties(makeUpTotal, trade);

            //sort为1表示汇总的数据，前端要求这样
            trade.setSort(1);
            //前端要求组合多加一组求和数据
            result.add(trade);

            result.addAll(list);
        } else {
            //单腿
            result.add(list.get(0));
        }
        setQuoteFiledName(result, Boolean.FALSE);
        return result;
    }

    /**
     * 根据交易编号查详情 得结果A
     *  若A的组合编号不为空;再根据A的组合编号查数据并返回
     *  否则返回A
     * @param dto 入参
     * @return 返回交易详情
     */
    @Override
    public TradeMngDetailVO getByTradeCode(TradeDetailDto dto) {
        List<TradeMngVO> result = new ArrayList<>();
        TradeMngVO vo = this.getVoOne(
                new LambdaQueryWrapper<TradeMng>()
                .eq(TradeMng::getTradeCode, dto.getTradeCode())
                .eq(TradeMng::getIsDeleted, 0), TradeMngVO.class);
        BussinessException.E_300211.assertTrue(vo!=null);
        String combCode = vo.getCombCode();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(combCode)){
            List<TradeMngVO> list = this.listVo(
                    new LambdaQueryWrapper<TradeMng>()
                            .eq(TradeMng::getCombCode, combCode)
                            .eq(TradeMng::getIsDeleted, 0),
                    TradeMngVO.class);
            BussinessException.E_300211.assertTrue(!list.isEmpty());
            result.addAll(list);
            setQuoteFiledName(result, Boolean.FALSE);
        } else {
            result.add(vo);
        }
        setQuoteFiledName(result, Boolean.FALSE);
        Set<String> underlyingCodeSet = result.stream().map(TradeMngVO::getUnderlyingCode).collect(Collectors.toSet());
        List<UnderlyingManagerVO>  underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet);
        // key=标的代码(大写) , value=交易单位
        Map<String,String> underlyingCodeUnitMap = underlyingManagerVOList.stream().collect(Collectors.toMap(item->item.getUnderlyingCode().toUpperCase(), UnderlyingManagerVO::getQuoteUnit,(v1, v2)->v2));
        // 取东证方向
        for (TradeMngVO tradeMngVO : result){
            if (BuyOrSellEnum.sell==tradeMngVO.getBuyOrSell()){
                tradeMngVO.setBuyOrSell(BuyOrSellEnum.buy);
                tradeMngVO.setBuyOrSellName(BuyOrSellEnum.buy.getDesc());
            } else {
                tradeMngVO.setBuyOrSell(BuyOrSellEnum.sell);
                tradeMngVO.setBuyOrSellName(BuyOrSellEnum.sell.getDesc());
            }
            tradeMngVO.setQuoteUnit(underlyingCodeUnitMap.get(tradeMngVO.getUnderlyingCode()).toUpperCase());
            // vue端, 成交金额取反
            if (tradeMngVO.getTotalAmount() != null) {
                tradeMngVO.setTotalAmount(tradeMngVO.getTotalAmount().negate());
            }
            // vue端, 期权价格取反
            if (tradeMngVO.getOptionPremium() != null){
                tradeMngVO.setOptionPremium(tradeMngVO.getOptionPremium().negate());
            }
            // vue端, 期权费率取反
            if (tradeMngVO.getOptionPremiumPercent()!=null) {
                tradeMngVO.setOptionPremiumPercent(tradeMngVO.getOptionPremiumPercent().negate());
            }
        }
        TradeMngDetailVO returnVO = new TradeMngDetailVO();
        returnVO.setTradeMngVOList(result); // 交易记录
        // 根据交易编号查询平仓记录
        LambdaQueryWrapper<TradeCloseMng> tradeCloseMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeCloseMngLambdaQueryWrapper.eq(TradeCloseMng::getIsDeleted,IsDeletedEnum.NO);
        tradeCloseMngLambdaQueryWrapper.eq(TradeCloseMng::getTradeCode,dto.getTradeCode());
        List<TradeCloseMng> tradeCloseMngList = tradeCloseMngMapper.selectList(tradeCloseMngLambdaQueryWrapper);
        List<TradeCloseMngVO> tradeCloseMngVOList = tradeCloseMngList.stream().map(item->{
            TradeCloseMngVO tradeCloseMngVO = new TradeCloseMngVO();
            BeanUtils.copyProperties(item,tradeCloseMngVO);
            return tradeCloseMngVO;
        }).collect(Collectors.toList());
        returnVO.setTradeCloseMngVOList(tradeCloseMngVOList); // 平仓记录
        // 根据交易编号查询资金记录
        LambdaQueryWrapper<CapitalRecords> capitalRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        capitalRecordsLambdaQueryWrapper.eq(CapitalRecords::getIsDeleted,IsDeletedEnum.NO);
        capitalRecordsLambdaQueryWrapper.eq(CapitalRecords::getTradeCode,dto.getTradeCode());
        List<CapitalRecords> capitalRecordsList = capitalRecordsMapper.selectList(capitalRecordsLambdaQueryWrapper);
        // 组装资金记录信息
        if (!capitalRecordsList.isEmpty()) {
            Set<Integer> clientIdSet = capitalRecordsList.stream().map(CapitalRecords::getClientId).collect(Collectors.toSet());
            // 客户信息 key = 客户ID , value = 客户obj
            Map<Integer, ClientVO> clientMap;
            if(!clientIdSet.isEmpty()) {
                // 获取客户信息
                clientMap = clientClient.getClientListByIds(clientIdSet).stream().collect(Collectors.toMap(ClientVO::getId, item -> item, (v1, v2) -> v2));
            } else {
                clientMap = new HashMap<>();
            }
            // 处理创建人和更新人
            Set<Integer> creatorIdSet = capitalRecordsList.stream().map(CapitalRecords::getCreatorId).collect(Collectors.toSet());
            Set<Integer> updatorIdSet = capitalRecordsList.stream().map(CapitalRecords::getUpdatorId).collect(Collectors.toSet());
            Set<Integer> userIsSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(creatorIdSet)){
                userIsSet.addAll(creatorIdSet);
            }
            if (CollectionUtils.isNotEmpty(updatorIdSet)){
                userIsSet.addAll(updatorIdSet);
            }
            // key = id , value = name
            Map<Integer,String> userMap = userClient.getUserMapByIds(userIsSet);
            List<CapitalRecordsVO> capitalRecordsVOList = capitalRecordsList.stream().map(item->{
                CapitalRecordsVO capitalRecordsVO = new CapitalRecordsVO();
                BeanUtils.copyProperties(item,capitalRecordsVO);
                if (clientMap.containsKey(item.getClientId())) {
                    ClientVO client = clientMap.get(item.getClientId());
                    capitalRecordsVO.setClientName(client.getName());
                }
                capitalRecordsVO.setCreatorName(userMap.get(item.getCreatorId()));
                capitalRecordsVO.setUpdatorName(userMap.get(item.getUpdatorId()));
                return capitalRecordsVO;
            }).collect(Collectors.toList());
            returnVO.setCapitalRecordsVOList(capitalRecordsVOList);
        }
        return returnVO;
    }

    @Override
    public List<TradeMngVO> queryTradeListByTradeCodeList(TradeCodeQueryDTO dto) {

        LambdaQueryWrapper<TradeMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        //合约代码
        if (dto.getTradeCodeList() != null && !dto.getTradeCodeList().isEmpty()) {
            queryWrapper.in(TradeMng::getTradeCode, dto.getTradeCodeList());
        }
        List<TradeMngVO> list = this.listVo(queryWrapper, TradeMngVO.class);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        setQuoteFiledName(list, true);
        return list;
    }

    /**
     * 获取交易记录查询条件构造器
     * @param tradeQueryDto 查询条件
     * @return 构造器
     */
    public LambdaQueryWrapper<TradeMng> getTradeMngQueryWrapper(TradeQueryDTO tradeQueryDto) {
        LambdaQueryWrapper<TradeMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        //组合代码
        queryWrapper.like(StringUtils.isNotBlank(tradeQueryDto.getCombCode()), TradeMng::getCombCode, tradeQueryDto.getCombCode());

        //交易编号列表
        queryWrapper.like(StringUtils.isNotBlank(tradeQueryDto.getTradeCode()), TradeMng::getTradeCode, tradeQueryDto.getTradeCode());
        //客户ID
        if (tradeQueryDto.getClientIdList() != null && !tradeQueryDto.getClientIdList().isEmpty()) {
            queryWrapper.in(TradeMng::getClientId, tradeQueryDto.getClientIdList());
        }
        //开始时间
        queryWrapper.ge(tradeQueryDto.getStartTradeDate() != null, TradeMng::getTradeDate, tradeQueryDto.getStartTradeDate());
        queryWrapper.ge(tradeQueryDto.getStartMaturityDate() != null, TradeMng::getMaturityDate, tradeQueryDto.getStartMaturityDate());
        //结束时间
        queryWrapper.le(tradeQueryDto.getEndTradeDate() != null, TradeMng::getTradeDate, tradeQueryDto.getEndTradeDate());
        queryWrapper.le(tradeQueryDto.getEndMaturityDate() != null, TradeMng::getMaturityDate, tradeQueryDto.getEndMaturityDate());

        //交易状态
        if (tradeQueryDto.getTradeStateList() != null && !tradeQueryDto.getTradeStateList().isEmpty()) {
            queryWrapper.in(TradeMng::getTradeState, tradeQueryDto.getTradeStateList());
        }
        //期权类型
        if (tradeQueryDto.getOptionTypeList() != null && !tradeQueryDto.getOptionTypeList().isEmpty()) {
            queryWrapper.in(TradeMng::getOptionType, tradeQueryDto.getOptionTypeList());
        }
        // 风险警告信息
        if (StringUtils.isNotBlank(tradeQueryDto.getWarningMsg())) {
            if ("1".equals(tradeQueryDto.getWarningMsg())) {
                queryWrapper.isNotNull(TradeMng::getWarningMsg);
            } else if ("0".equals(tradeQueryDto.getWarningMsg())) {
                queryWrapper.isNull(TradeMng::getWarningMsg);
            }
        }
        //标的代码和合约编号 都不为空
        if (CollectionUtils.isNotEmpty(tradeQueryDto.getUnderlyingCodeList())) {
            queryWrapper.in(TradeMng::getUnderlyingCode, tradeQueryDto.getUnderlyingCodeList());
        }
        //品种筛选
        if (CollectionUtils.isNotEmpty(tradeQueryDto.getVarietyIdList())) {
            List<UnderlyingManagerVO> underlyingManagerVOList = underlyingManagerClient.getUnderlyingListByVarietyIds(tradeQueryDto.getVarietyIdList());
            if (CollectionUtils.isNotEmpty(underlyingManagerVOList)) {
                queryWrapper.in(TradeMng::getUnderlyingCode, underlyingManagerVOList.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toList()));
            }
        }
        // 簿记账户
        if (CollectionUtils.isNotEmpty(tradeQueryDto.getAssetIdList())) {
            queryWrapper.in(TradeMng::getAssetId, tradeQueryDto.getAssetIdList());
        }
        // 簿记账户组
        if (CollectionUtils.isNotEmpty(tradeQueryDto.getAssetGroupIdList())) {
            List<AssetunitVo> assetunitVoList = assetUnitClient.getAssetunitByGroupIds(new HashSet<>(tradeQueryDto.getAssetGroupIdList()));
            Set<Integer> ids = assetunitVoList.stream().map(AssetunitVo::getId).collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(ids)) { // ids为返回一个查不到任何数据的queryWrapper
                queryWrapper.in(TradeMng::getAssetId, Collections.singletonList(-1));
            } else {
                queryWrapper.in(TradeMng::getAssetId, ids);
            }
        }
        // 交易方向/客户方向
        if (StringUtils.isNotBlank(tradeQueryDto.getBuySell())) {
            queryWrapper.eq(TradeMng::getBuyOrSell, tradeQueryDto.getBuySell());
        }
        // 行权方式
        queryWrapper.eq(tradeQueryDto.getExerciseType() != null, TradeMng::getExerciseType, tradeQueryDto.getExerciseType());
        // 交易员
        queryWrapper.in(CollectionUtils.isNotEmpty(tradeQueryDto.getTraderIdList()), TradeMng::getTraderId, tradeQueryDto.getTraderIdList());

        //敲出日期查询
        if (tradeQueryDto.getStartKnockOutDate() != null || tradeQueryDto.getEndKnockOutDate() != null) {
            LambdaQueryWrapper<TradeObsDate> tradeObsDateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tradeObsDateLambdaQueryWrapper.ge(tradeQueryDto.getStartKnockOutDate() != null, TradeObsDate::getObsDate, tradeQueryDto.getStartKnockOutDate());
            tradeObsDateLambdaQueryWrapper.le(tradeQueryDto.getEndKnockOutDate() != null, TradeObsDate::getObsDate, tradeQueryDto.getEndKnockOutDate());
            tradeObsDateLambdaQueryWrapper.eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO);
            tradeObsDateLambdaQueryWrapper.select(TradeObsDate::getTradeId);
            tradeObsDateLambdaQueryWrapper.orderByAsc(TradeObsDate::getObsDate);
            List<TradeObsDate> tradeObsDateList = tradeObsDateMapper.selectList(tradeObsDateLambdaQueryWrapper);
            if (!tradeObsDateList.isEmpty()) {
                queryWrapper.in(TradeMng::getId, tradeObsDateList.stream().map(TradeObsDate::getTradeId).distinct().collect(Collectors.toList()));
                queryWrapper.in(TradeMng::getOptionType, OptionTypeEnum.getHaveKnockOut());
                //敲出日期开始时间不为空时过滤掉之前平仓的数据
                if (tradeQueryDto.getStartKnockOutDate() != null){
                    queryWrapper.and(wrapper -> wrapper.isNull(TradeMng::getCloseDate).or()
                            .ge(TradeMng::getCloseDate,tradeQueryDto.getStartKnockOutDate()));
                }
            } else {
                queryWrapper.last(" and 1=0");
            }
        }
        AuthorizeInfo authorizeInfo= ThreadContext.getAuthorizeInfo();
        //客户端过滤自定义期权
        if (authorizeInfo!=null && authorizeInfo.getLoginForm()==0){
            queryWrapper.ne(TradeMng::getOptionType,OptionTypeEnum.AICustomPricer);
        }
        //看涨看跌条件
        if (tradeQueryDto.getCallOrPut() != null) {
            queryWrapper.and(query -> query.eq(TradeMng::getCallOrPut, tradeQueryDto.getCallOrPut())
                    .or().in(TradeMng::getOptionType
                            , tradeQueryDto.getCallOrPut().equals(CallOrPutEnum.call) ?
                                    OptionTypeEnum.getCallOptionType() : OptionTypeEnum.getPutOptionType()
                    ));
        }
        return queryWrapper;
    }

    @Override
    public IPage<TradeMngVO> queryTradeList(TradeQueryDTO tradeQueryDTO) {
        //如果前端未传分页参数时设置默认分页参数
        if (tradeQueryDTO.getPageNo() == null || tradeQueryDTO.getPageSize() == null) {
            tradeQueryDTO.setPageNo(1);
            tradeQueryDTO.setPageSize(100);
        }
        LambdaQueryWrapper<TradeMng> queryWrapper = getTradeMngQueryWrapper(tradeQueryDTO);
        //平仓日期
        if (tradeQueryDTO.getStartCloseDate() != null || tradeQueryDTO.getEndCloseDate() != null) {
            LambdaQueryWrapper<TradeCloseMng> tradeCloseMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tradeCloseMngLambdaQueryWrapper.eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO);
            tradeCloseMngLambdaQueryWrapper.select(TradeCloseMng::getTradeCode);
            tradeCloseMngLambdaQueryWrapper.ge(tradeQueryDTO.getStartCloseDate() != null, TradeCloseMng::getCloseDate, tradeQueryDTO.getStartCloseDate());
            tradeCloseMngLambdaQueryWrapper.le(tradeQueryDTO.getEndCloseDate() != null, TradeCloseMng::getCloseDate, tradeQueryDTO.getEndCloseDate());
            List<TradeCloseMng> closeMngList = tradeCloseMngMapper.selectList(tradeCloseMngLambdaQueryWrapper);
            if (!closeMngList.isEmpty()) {
                queryWrapper.in(TradeMng::getTradeCode, closeMngList.stream().distinct().map(TradeCloseMng::getTradeCode).collect(Collectors.toList()));
            } else {
                queryWrapper.eq(TradeMng::getTradeCode, "返回空的构造器");
            }
        }

        IPage<TradeMng> ipage = this.page(new Page<>(tradeQueryDTO.getPageNo(), tradeQueryDTO.getPageSize()), queryWrapper);
        if (ipage.getTotal() == 0) {
            return ipage.convert(db -> new TradeMngVO());
        }
        IPage<TradeMngVO> returnPage = ipage.convert(db -> {
            TradeMngVO vo = new TradeMngVO();
            BeanUtils.copyProperties(db, vo);
            if (db.getBuyOrSell() == BuyOrSellEnum.buy) {
                vo.setBuyOrSell(BuyOrSellEnum.sell);
            } else {
                vo.setBuyOrSell(BuyOrSellEnum.buy);
            }
            if ((Objects.nonNull(vo.getTotalAmount()))) {
                vo.setTotalAmount(vo.getTotalAmount().negate());
            }
            if (Objects.nonNull(vo.getOptionPremium())) {
                vo.setOptionPremium(vo.getOptionPremium().negate());
            }
            return vo;
        });
        setQuoteFiledName(returnPage.getRecords(), true);
        return returnPage;
    }

    private void setQuoteFiledName(List<TradeMngVO> list, Boolean isList) {
        final CopyOptions copyOptions = CopyOptions.create();
        copyOptions.setOverride(false);
        copyOptions.setIgnoreNullValue(true);
        //获取雪球信息
        List<Integer> snowballTradeIdList = list.stream().filter(vo -> OptionTypeEnum.getSnowBall().contains(vo.getOptionType())).map(TradeMngVO::getId).collect(Collectors.toList());
        Map<Integer, TradeSnowballOption> snowballOptionMap = new HashMap<>();
        if (!snowballTradeIdList.isEmpty()) {
            snowballOptionMap = getSnowballMapByTradeIdList(snowballTradeIdList);
        }

        //获取观察日列表
        List<Integer> obsDateTradeIdList = list.stream().filter(vo -> vo.getOptionType() != OptionTypeEnum.AIVanillaPricer && vo.getOptionType() != OptionTypeEnum.AIForwardPricer).map(TradeMngVO::getId).collect(Collectors.toList());
        Map<Integer, List<TradeObsDate>> tradeObsDateMap = new HashMap<>();
        if (!obsDateTradeIdList.isEmpty()) {
            List<TradeObsDate> tradeObsDates = tradeObsDateMapper.selectList(new LambdaQueryWrapper<TradeObsDate>()
                    .in(TradeObsDate::getTradeId, obsDateTradeIdList)
                    .eq(TradeObsDate::getIsDeleted, 0)
                    .orderByAsc(TradeObsDate::getObsDate));
            tradeObsDateMap = tradeObsDates.stream().collect(Collectors.groupingBy(TradeObsDate::getTradeId));
        }

        //薄记账户
        Set<Integer> assetIdSet = list.stream().map(TradeMngVO::getAssetId).collect(Collectors.toSet());
        Map<Integer, String> assetUnitMap = assetUnitClient.getAssetUnitMapByIds(assetIdSet);
        //交易员
        Set<Integer> traderIdSet = list.stream().map(TradeMngVO::getTraderId).collect(Collectors.toSet());
        Map<Integer, String> traderMap = userClient.getUserMapByIds(traderIdSet);
        //客户名称
        Set<Integer> clientIdSet = list.stream().map(TradeMngVO::getClientId).collect(Collectors.toSet());
        Map<Integer, String> clientMap = clientClient.getClientMapByIds(clientIdSet);

        LocalDate tradeDay = systemConfigUtil.getTradeDay();
        for (TradeMngVO vo : list) {
            vo.setAssetName(assetUnitMap.get(vo.getAssetId()));
            vo.setTraderName(traderMap.get(vo.getTraderId()));
            vo.setUpdatorName(traderMap.get(vo.getUpdatorId()));
            vo.setClientName(clientMap.get(vo.getClientId()));
            vo.setOptionTypeName(vo.getOptionType() == null ? "" : vo.getOptionType().getDesc());
            vo.setBuyOrSellName(vo.getBuyOrSell() == null ? "" : vo.getBuyOrSell().getDesc());
            vo.setExerciseTypeName(vo.getExerciseType() == null ? "" : vo.getExerciseType().getDesc());
            vo.setOptionTypeName(vo.getOptionType() == null ? "" : vo.getOptionType().getDesc());
            vo.setSettleTypeName(vo.getSettleType() == null ? "" : vo.getSettleType().getDesc());
            vo.setOptionCombTypeName(vo.getOptionCombType() == null ? "" : vo.getOptionCombType().getDesc());
            vo.setCallOrPutName(vo.getCallOrPut() == null ? "" : vo.getCallOrPut().getDesc());
            vo.setTradeStateName(vo.getTradeState() == null ? "" : vo.getTradeState().getDesc());
            vo.setCeilFloorName(vo.getCeilFloor() == null ? "" : vo.getCeilFloor().getDesc());
            //处理观察日历
            List<TradeObsDateVO> tradeObsDateDtoList = JSONArray.parseArray(JSONArray.toJSONString(tradeObsDateMap.get(vo.getId())), TradeObsDateVO.class);
            vo.setTradeObsDateList(tradeObsDateDtoList);

            //雪球期权的敲出价格单独设置
            if (OptionTypeEnum.getSnowBall().contains(vo.getOptionType())) {
                BeanUtil.copyProperties(snowballOptionMap.get(vo.getId()), vo, copyOptions);
                if (isList) {
                    if (vo.getKnockinBarrierRelative() != null && vo.getKnockinBarrierValue() != null && vo.getKnockinBarrierRelative()) {
                        vo.setKnockinBarrierValue(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(vo.getKnockinBarrierValue())));
                    }
                    if (vo.getStrikeOnceKnockedinRelative() != null && vo.getStrikeOnceKnockedinValue() != null && vo.getStrikeOnceKnockedinRelative()) {
                        vo.setStrikeOnceKnockedinValue(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(vo.getStrikeOnceKnockedinValue())));
                    }
                    if (vo.getStrike2OnceKnockedinRelative() != null && vo.getStrike2OnceKnockedinValue() != null && vo.getStrike2OnceKnockedinRelative()) {
                        vo.setStrike2OnceKnockedinValue(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(vo.getStrike2OnceKnockedinValue())));
                    }
                    //敲出价格单独设置
                    TradeObsDateVO tradeObsDateVO = vo.getTradeObsDateList().stream().filter(a -> !a.getObsDate().isBefore(tradeDay)).findFirst().orElse(new TradeObsDateVO());
                    if (tradeObsDateVO.getBarrierRelative() != null && tradeObsDateVO.getBarrierRelative()) {
                        vo.setBarrier(vo.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDateVO.getBarrier())));
                    } else {
                        vo.setBarrier(tradeObsDateVO.getBarrier());
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public Boolean saveTradeMngByYl(TradeMngVO tradeMngVo) {
        LambdaQueryWrapper<TradeMng> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TradeMng::getTradeCode,tradeMngVo.getTradeCode());
        lambdaQueryWrapper.eq(TradeMng::getIsDeleted,IsDeletedEnum.NO);
        long count = this.count(lambdaQueryWrapper);
        if (count>0){
            return true;
        }
        TradeMng db = CglibUtil.copy(tradeMngVo, TradeMng.class);
        this.save(db);
        tradeMngVo.setId(db.getId());
        //保存雪球相关信息
        if (tradeMngVo.getOptionType() == OptionTypeEnum.AISnowBallCallPricer || tradeMngVo.getOptionType() == OptionTypeEnum.AISnowBallPutPricer || tradeMngVo.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallCallPricer || tradeMngVo.getOptionType() == OptionTypeEnum.AIBreakEvenSnowBallPutPricer || tradeMngVo.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallCallPricer || tradeMngVo.getOptionType() == OptionTypeEnum.AILimitLossesSnowBallPutPricer) {
            saveSnowBallInfo(CglibUtil.copy(tradeMngVo, TradeMngDTO.class));
        }
        //保存观察日数据
        if (tradeMngVo.getTradeObsDateList() != null) {
            for (TradeObsDateVO obsDateVo : tradeMngVo.getTradeObsDateList()) {
                obsDateVo.setTradeId(db.getId());
                tradeObsDateMapper.insert(CglibUtil.copy(obsDateVo, TradeObsDate.class));
            }
        }
        return true;
    }


    @Override
    @DB
    public List<TradeMng> queryNotSyncTradeList() {
        LambdaQueryWrapper<TradeMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(TradeMng::getIsSync, 1);
        queryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        List<TradeMng> list = tradeMngMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            List<Integer> tradeIdList = list.stream().map(TradeMng::getId).collect(Collectors.toList());
            List<TradeObsDate> tradeObsDates = tradeObsDateMapper.selectList(new LambdaQueryWrapper<TradeObsDate>()
                    .in(TradeObsDate::getTradeId, tradeIdList)
                    .eq(TradeObsDate::getIsDeleted, IsDeletedEnum.NO)
                    .orderByAsc(TradeObsDate::getObsDate));
            if (!tradeObsDates.isEmpty()) {
                Map<Integer, List<TradeObsDate>> integerListMap = tradeObsDates.stream().collect(Collectors.groupingBy(TradeObsDate::getTradeId));
                list = list.stream().peek(tradeMng -> {
                    if (integerListMap.containsKey(tradeMng.getId())) {
                        tradeMng.setTradeObsDateList(CglibUtil.copyList(integerListMap.get(tradeMng.getId()), TradeObsDate::new));
                    }
                }).collect(Collectors.toList());
            }
        }
        return list;
    }


    @Override
    @DB
    public List<TradeMngVO> getSurvivalTradeByTradeDay(LocalDate tradeDay) {

        List<TradeMngVO> tradeMngVOList = this.listVo(new LambdaQueryWrapper<TradeMng>()
                .in(TradeMng::getTradeState, TradeStateEnum.getLiveStateList())
                .ne(TradeMng::getOptionType,OptionTypeEnum.AICustomPricer)
                .le(TradeMng::getTradeDate, tradeDay)
                .eq(TradeMng::getIsDeleted, 0), TradeMngVO.class);
        return getTradeMngVOS(tradeMngVOList);
    }

    @NonNull
    private List<TradeMngVO> getTradeMngVOS(List<TradeMngVO> tradeMngVOList) {
        List<Integer> collect = tradeMngVOList.stream().filter(item->OptionTypeEnum.getHaveObsType().contains(item.getOptionType()))
                .map(TradeMngVO::getId).collect(Collectors.toList());
        if (!collect.isEmpty()) {
            List<TradeObsDate> tradeObsDatesList = tradeObsDateMapper.selectList(new LambdaQueryWrapper<TradeObsDate>()
                    .in(TradeObsDate::getTradeId, collect)
                    .eq(TradeObsDate::getIsDeleted, 0)
                    .orderByAsc(TradeObsDate::getObsDate));
            Map<Integer, List<TradeObsDate>> tradeObsDateMap = tradeObsDatesList.stream().collect(Collectors.groupingBy(TradeObsDate::getTradeId));
            //获取雪球信息
            List<Integer> snowballTradeIdList = tradeMngVOList.stream().filter(vo -> OptionTypeEnum.getSnowBall().contains(vo.getOptionType())).map(TradeMngVO::getId).collect(Collectors.toList());
            Map<Integer, TradeSnowballOption> snowballOptionMap = new HashMap<>();
            if (!snowballTradeIdList.isEmpty()) {
                snowballOptionMap = getSnowballMapByTradeIdList(snowballTradeIdList);
            }
            final CopyOptions copyOptions = CopyOptions.create();
            copyOptions.setOverride(false);
            copyOptions.setIgnoreNullValue(true);
            for (TradeMngVO vo : tradeMngVOList) {
                List<TradeObsDateVO> tradeObsDateDtoList = JSONArray.parseArray(JSONArray.toJSONString(tradeObsDateMap.get(vo.getId())), TradeObsDateVO.class);
                vo.setTradeObsDateList(tradeObsDateDtoList);
                //雪球期权的敲出价格单独设置
                if (OptionTypeEnum.getSnowBall().contains(vo.getOptionType())) {
                    BeanUtil.copyProperties(snowballOptionMap.get(vo.getId()), vo, copyOptions);
                }
            }
        }
        return tradeMngVOList;
    }

    @Override
    public List<TradeMngVO> getSurvivalTradeByTradeDayAndClient(Set<Integer> clientIdList, LocalDate tradeDay) {
        List<TradeMngVO> tradeMngVOList = this.listVo(new LambdaQueryWrapper<TradeMng>().in(TradeMng::getTradeState, TradeStateEnum.getLiveStateList()).in(clientIdList != null && !clientIdList.isEmpty(), TradeMng::getClientId, clientIdList).le(TradeMng::getTradeDate, tradeDay).eq(TradeMng::getIsDeleted, 0), TradeMngVO.class);
        return getTradeMngVOS(tradeMngVOList);
    }

    @Override
    @DB
    public List<TradeMngVO> getCloseTradeByTradeDay(LocalDate tradeDay) {
        List<TradeMngVO> tradeMngVOList = this.listVo(new LambdaQueryWrapper<TradeMng>()
                .ne(TradeMng::getOptionType,OptionTypeEnum.AICustomPricer)
                .eq(TradeMng::getCloseDate, tradeDay)
                .eq(TradeMng::getIsDeleted, 0), TradeMngVO.class);
        return getTradeMngVOS(tradeMngVOList);
    }

    @Override
    @Transactional
    public String delete(CombCodeDTO combCodeDTO) {
        List<TradeMng> tradeMngList = tradeMngMapper.selectList(new LambdaQueryWrapper<TradeMng>().eq(TradeMng::getCombCode, combCodeDTO.getCombCode()).eq(TradeMng::getIsDeleted, 0));
        BussinessException.E_300100.assertTrue(!tradeMngList.isEmpty());
        List<TradeCloseMng> tradeCloseMngList = tradeCloseMngMapper.selectList(new LambdaQueryWrapper<TradeCloseMng>().eq(TradeCloseMng::getIsDeleted, 0).in(TradeCloseMng::getTradeCode, tradeMngList.stream().map(TradeMng::getTradeCode).collect(Collectors.toList())));
        BussinessException.E_300108.assertTrue(tradeCloseMngList.isEmpty());
        List<Integer> tradeIdList = tradeMngList.stream().map(TradeMng::getId).collect(Collectors.toList());
        //删除观察日数据
        LambdaQueryWrapper<TradeObsDate> tradeObsDateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeObsDateLambdaQueryWrapper.in(TradeObsDate::getTradeId, tradeIdList);
        TradeObsDate tradeObsDate = new TradeObsDate();
        tradeObsDate.setIsDeleted(IsDeletedEnum.YES.getFlag());
        tradeObsDateMapper.update(tradeObsDate, tradeObsDateLambdaQueryWrapper);

        //删除风险数据
        LambdaQueryWrapper<TradeRiskInfo> tradeRiskInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeRiskInfoLambdaQueryWrapper.in(TradeRiskInfo::getCombCode, combCodeDTO.getCombCode()).eq(TradeRiskInfo::getIsDeleted, IsDeletedEnum.NO);
        TradeRiskInfo tradeRiskInfo = new TradeRiskInfo();
        tradeRiskInfo.setIsDeleted(IsDeletedEnum.YES.getFlag());
        tradeRiskInfoMapper.update(tradeRiskInfo, tradeRiskInfoLambdaQueryWrapper);
        //删除资金记录
        LambdaQueryWrapper<CapitalRecords> capitalRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        capitalRecordsLambdaQueryWrapper.in(CapitalRecords::getTradeId, tradeIdList);
        CapitalRecords capitalRecords = new CapitalRecords();
        capitalRecords.setIsDeleted(IsDeletedEnum.YES.getFlag());
        capitalRecordsService.update(capitalRecords, capitalRecordsLambdaQueryWrapper);
        //删除主交易信息
        TradeMng tradeMng = new TradeMng();
        tradeMng.setIsDeleted(1);
        tradeMngMapper.update(tradeMng, new LambdaQueryWrapper<TradeMng>().eq(TradeMng::getCombCode, combCodeDTO.getCombCode()).eq(TradeMng::getIsDeleted, 0));
        // 添加删除交易记录
        addTradeUpdateRecord(tradeMngList);
        for (TradeMng db : tradeMngList) {
            stringRedisTemplate.opsForHash().delete(RedisAdapter.TRADE_RISK_RESULT, db.getTradeCode());
        }
        if (isNeedSyncToYl) {
            rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.SYNC_TOPIC_TRADE_MNG_DEL, tradeMngList);
        }
        return "delete success";
    }

    /**
     * 添加交易删除的变更记录
     * @param tradeMngList       交易列表
     */
    public void addTradeUpdateRecord(List<TradeMng> tradeMngList){
        for (TradeMng tm : tradeMngList){
            List<DiffObjectVO> diffObjectVOList = new ArrayList<>();
            DiffObjectVO tradeDiffVO = objectEqualsUtil.buildDiffObjectVO("交易状态",IsDeletedEnum.NO.getDesc(),IsDeletedEnum.YES.getDesc());
            diffObjectVOList.add(tradeDiffVO);
            DiffObjectVO obsDateDiffVO = objectEqualsUtil.buildDiffObjectVO("观察日状态",IsDeletedEnum.NO.getDesc(),IsDeletedEnum.YES.getDesc());
            diffObjectVOList.add(obsDateDiffVO);
            DiffObjectVO riskDiffVO = objectEqualsUtil.buildDiffObjectVO("风险状态",IsDeletedEnum.NO.getDesc(),IsDeletedEnum.YES.getDesc());
            diffObjectVOList.add(riskDiffVO);
            DiffObjectVO capitalDiffVO = objectEqualsUtil.buildDiffObjectVO("资金状态",IsDeletedEnum.NO.getDesc(),IsDeletedEnum.YES.getDesc());
            diffObjectVOList.add(capitalDiffVO);
            TradeDataChangeRecordDTO tradeDataChangeRecordDTO = new TradeDataChangeRecordDTO();
            tradeDataChangeRecordDTO.setTradeCode(tm.getTradeCode());
            tradeDataChangeRecordDTO.setClientId(tm.getClientId());
            tradeDataChangeRecordDTO.setAssetunitId(tm.getAssetId());
            tradeDataChangeRecordDTO.setChangeType(DataChangeTypeEnum.delete);
            tradeDataChangeRecordDTO.setChangeFields(JSONObject.toJSONString(diffObjectVOList));
            systemDataChangeRecordClient.addTradeDataChangeRecord(tradeDataChangeRecordDTO);
        }
    }


    private String getCombCode(LocalDate tradeDate, Integer clienId) {
        String tradeDateStr = tradeDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        ClientVO client = clientClient.getClientById(clienId);
        int size = tradeMngMapper.selectList(new LambdaQueryWrapper<TradeMng>().select(TradeMng::getCombCode).eq(TradeMng::getTradeDate, tradeDate).eq(TradeMng::getClientId, clienId).likeRight(TradeMng::getCombCode, tradeDateStr).groupBy(TradeMng::getCombCode)).size();

        return tradeDateStr + "-" + client.getCode() + "-" + String.format("%02d", size + 1);
    }

    /**
     * 获取查询条件构造器
     * @param tradeQueryDto 查询条件
     * @return 构造器
     */
    public LambdaQueryWrapper<TradeMng> getQueryWrapper(TradeQueryPageDto tradeQueryDto) {
        LambdaQueryWrapper<TradeMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        //组合代码
        queryWrapper.like(StringUtils.isNotBlank(tradeQueryDto.getCombCode()), TradeMng::getCombCode, tradeQueryDto.getCombCode());

        //交易编号列表
        queryWrapper.like(StringUtils.isNotBlank(tradeQueryDto.getTradeCode()), TradeMng::getCombCode, tradeQueryDto.getTradeCode());
        //合约代码
        if (tradeQueryDto.getUnderlyingCodeList() != null && !tradeQueryDto.getUnderlyingCodeList().isEmpty()) {
            queryWrapper.in(TradeMng::getUnderlyingCode, tradeQueryDto.getUnderlyingCodeList());
        }
        //簿记ID
        if (tradeQueryDto.getAssetIdList() != null && !tradeQueryDto.getAssetIdList().isEmpty()) {
            queryWrapper.in(TradeMng::getAssetId, tradeQueryDto.getAssetIdList());
        }
        //客户ID
        if (tradeQueryDto.getClientIdList() != null && !tradeQueryDto.getClientIdList().isEmpty()) {
            queryWrapper.in(TradeMng::getClientId, tradeQueryDto.getClientIdList());
        }
        //开始时间
        queryWrapper.ge(tradeQueryDto.getStartTradeDate() != null, TradeMng::getTradeDate, tradeQueryDto.getStartTradeDate());
        queryWrapper.ge(tradeQueryDto.getStartMaturityDate() != null, TradeMng::getMaturityDate, tradeQueryDto.getStartMaturityDate());
        //结束时间
        queryWrapper.le(tradeQueryDto.getEndTradeDate() != null, TradeMng::getTradeDate, tradeQueryDto.getEndTradeDate());
        queryWrapper.le(tradeQueryDto.getEndMaturityDate() != null, TradeMng::getMaturityDate, tradeQueryDto.getEndMaturityDate());
        //平仓日期
        if (tradeQueryDto.getStartCloseDate() != null || tradeQueryDto.getEndCloseDate() != null) {
            LambdaQueryWrapper<TradeCloseMng> tradeCloseMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tradeCloseMngLambdaQueryWrapper.eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO);
            tradeCloseMngLambdaQueryWrapper.select(TradeCloseMng::getTradeCode);
            tradeCloseMngLambdaQueryWrapper.ge(tradeQueryDto.getStartCloseDate() != null, TradeCloseMng::getCloseDate, tradeQueryDto.getStartCloseDate());
            tradeCloseMngLambdaQueryWrapper.le(tradeQueryDto.getEndCloseDate() != null, TradeCloseMng::getCloseDate, tradeQueryDto.getEndCloseDate());
            List<TradeCloseMng> closeMngList = tradeCloseMngMapper.selectList(tradeCloseMngLambdaQueryWrapper);
            if (!closeMngList.isEmpty()) {
                queryWrapper.in(TradeMng::getTradeCode, closeMngList.stream().distinct().map(TradeCloseMng::getTradeCode).collect(Collectors.toList()));
            } else {
                return null;
            }
        }
        //交易状态
        if (tradeQueryDto.getTradeStateList() != null && !tradeQueryDto.getTradeStateList().isEmpty()) {
            queryWrapper.in(TradeMng::getTradeState, tradeQueryDto.getTradeStateList());
        }
        //期权类型
        if (tradeQueryDto.getOptionTypeList() != null && !tradeQueryDto.getOptionTypeList().isEmpty()) {
            queryWrapper.in(TradeMng::getOptionType, tradeQueryDto.getOptionTypeList());
        }
        // 风险警告信息
        if (StringUtils.isNotBlank(tradeQueryDto.getWarningMsg())) {
            if ("1".equals(tradeQueryDto.getWarningMsg())) {
                queryWrapper.isNotNull(TradeMng::getWarningMsg);
                queryWrapper.ne(TradeMng::getWarningMsg, "");
            } else if ("0".equals(tradeQueryDto.getWarningMsg())) {
                queryWrapper.and(wrapper -> wrapper.isNull(TradeMng::getWarningMsg).or().eq(TradeMng::getWarningMsg, ""));
            }
        }
        return queryWrapper;
    }

    @Override
    public IPage<TradeMngVO> getListBypage(TradeQueryPageDto tradeQueryDto) {
        LambdaQueryWrapper<TradeMng> queryWrapper = getQueryWrapper(tradeQueryDto);
        /*//增加交易数量限制
        queryWrapper.last(" limit 100");
        List<TradeMng> dbList = this.list(queryWrapper);*/
        IPage<TradeMng> ipage = this.page(new Page<>(tradeQueryDto.getPageNo(), tradeQueryDto.getPageSize()), queryWrapper);
        IPage<TradeMngVO> returnIpage = ipage.convert(item -> {
            TradeMngVO vo = new TradeMngVO();
            BeanUtils.copyProperties(item, vo);
            if (item.getBuyOrSell() == BuyOrSellEnum.buy) {
                vo.setBuyOrSell(BuyOrSellEnum.sell);
            } else {
                vo.setBuyOrSell(BuyOrSellEnum.buy);
            }
            vo.setTotalAmount(vo.getTotalAmount().negate());
            if (Objects.nonNull(vo.getOptionPremium())) {
                vo.setOptionPremium(vo.getOptionPremium().negate());
            }
            return vo;
        });
        setQuoteFiledName(returnIpage.getRecords(), true);
        return returnIpage;
    }

    @Override
    public void export(TradeQueryPageDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LambdaQueryWrapper<TradeMng> queryWrapper = getQueryWrapper(dto);
        List<TradeMng> list = this.list(queryWrapper);
        List<TradeMngVO> voList = list.stream().map(item -> {
            TradeMngVO vo = new TradeMngVO();
            BeanUtils.copyProperties(item, vo);
            if (item.getBuyOrSell() == BuyOrSellEnum.buy) {
                vo.setBuyOrSell(BuyOrSellEnum.sell);
            } else {
                vo.setBuyOrSell(BuyOrSellEnum.buy);
            }
            vo.setTotalAmount(vo.getTotalAmount().negate());
            if (Objects.nonNull(vo.getOptionPremium())) {
                vo.setOptionPremium(vo.getOptionPremium().negate());
            }
            return vo;
        }).collect(Collectors.toList());
        setQuoteFiledName(voList, true);
        Set<String> underlyingCodeSet = voList.stream().map(TradeMngVO::getUnderlyingCode).collect(Collectors.toSet());
        List<UnderlyingManagerVO> underlyingManagerVOList = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet);
        // key = 标的代码 value=品种ID
        Map<String,UnderlyingManagerVO> underlyingMap = underlyingManagerVOList.stream().collect(Collectors.toMap(item->item.getUnderlyingCode().toUpperCase(),Function.identity()));

        List<TradeMngExportVo> exportList = CglibUtil.copyList(voList, TradeMngExportVo::new,(vo,exportVo)->{
            exportVo.setAssetName(vo.getAssetName());
            exportVo.setClientName(vo.getClientName());
            if (vo.getTradeState() != null) {
                exportVo.setTradeState(vo.getTradeState().getDesc());
            }
            if(vo.getOptionCombType()!=null){
                exportVo.setOptionCombTypeName(vo.getOptionCombType().getDesc());
            }
            if (vo.getOptionType() != null){
                exportVo.setOptionTypeName(vo.getOptionType().getDesc());
            }
            if (vo.getCallOrPut() != null) {
                exportVo.setCallOrPutName(vo.getCallOrPut().getDesc());
            }
            if (vo.getBuyOrSell() != null) {
                if (vo.getBuyOrSell() == BuyOrSellEnum.buy) {
                    exportVo.setBuyOrSellName(BuyOrSellEnum.sell.getDesc());
                } else {
                    exportVo.setBuyOrSellName(BuyOrSellEnum.buy.getDesc());
                }
            }
            exportVo.setVarietyName(underlyingMap.getOrDefault(vo.getUnderlyingCode(),new UnderlyingManagerVO()).getVarietyName());
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        // 文件名称 = 风险导出+时间戳
        String fileName = "交易信息" + sdf.format(calendar.getTime()) + calendar.get(Calendar.MILLISECOND);
        Map<Integer,Integer> columnWidthMap = new HashMap<>();
        columnWidthMap.put(20,20);
        columnWidthMap.put(21,20);
        HutoolUtil.export(exportList, fileName, "交易信息", TradeMngExportVo.class, request, response,columnWidthMap);
    }

    @Override
    public IPage<TradeConfirmBookVO> tradeConfirmBookSelectByPage(TradeConfirmBookQueryDTO queryDTO) {
        Page<TradeConfirmBookVO> page = tradeMngMapper.selectTradeConfirmBook(new Page<>(queryDTO.getPageNo(), queryDTO.getPageSize()), queryDTO);
        //客户名称
        Set<Integer> clientIdSet = page.getRecords().stream().map(TradeConfirmBookVO::getClientId).collect(Collectors.toSet());
        Map<Integer, String> clientMap = clientClient.getClientMapByIds(clientIdSet);
        //薄记账户
        Set<Integer> assetIdSet = page.getRecords().stream().map(TradeConfirmBookVO::getAssetId).collect(Collectors.toSet());
        Map<Integer, String> assetUnitMap = assetUnitClient.getAssetUnitMapByIds(assetIdSet);
        Map<Integer, String> traderMap = userClient.getUserMapByIds(new HashSet<>());
        page.getRecords().forEach(item -> {
            item.setTradeStateName(item.getTradeState().getDesc());
            item.setClientName(clientMap.get(item.getClientId()));
            item.setAssetName(assetUnitMap.get(item.getAssetId()));
            item.setTraderName(traderMap.get(item.getTraderId()));
            item.setTradeAddName(traderMap.get(item.getTradeAddId()));
            item.setCreatorName(traderMap.get(item.getCreatorId()));
            item.setTradeStateName(item.getTradeState().getDesc());
            item.setOptionTypeName(item.getOptionType().getDesc());
            item.setOptionCombTypeName(item.getOptionCombType() != null ? item.getOptionCombType().getDesc() : "");
        });
        return page;
    }

    /**
     * 生成交易确认书 累计期权 : 上界价格 : 行权价格 (累沽期权) , 敲出价格(累购期权) 下界价格 : 敲出价格 (累沽期权) , 行权价格(累购期权)
     * @throws Exception 系统异常
     */
    @Override
    public  List<MinioUploadVO>  buildTradeConfirmBook(BuildTradeConfirmBookDto dto) throws Exception {
        LambdaQueryWrapper<TradeMng> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(TradeMng::getTradeCode, dto.getTradeCodeList());
        lambdaQueryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        List<TradeMng> list = this.list(lambdaQueryWrapper);
        // 数据库中查出来的交易和入参的的交易编号长度不一致
        if (list.size() != dto.getTradeCodeList().size()) {
            // 不在入参中的交易编号
            List<String> notExisttradeCodeList = dto.getTradeCodeList().stream()
                    .filter(tradeCode -> !list.stream().map(TradeMng::getTradeCode)
                            .collect(Collectors.toList()).contains(tradeCode))
                    .collect(Collectors.toList());
            log.error("生成交易确认书数量和预期不一样,缺少的部分交易编号={}", JSON.toJSONString(notExisttradeCodeList));
            BussinessException.E_300102.assertTrue(false, notExisttradeCodeList);
        }
        List<MinioUploadVO> minioUploadVOList = new ArrayList<>();
        // 生成确认书列表数据
        List<TradeConfirmBookDTO> confirmBookDTOList = setTradeConfirmBook(list);
        //香草期权
        List<List<TradeConfirmBookDTO>> vanillaList = groupTradeConfirmBook(confirmBookDTOList.stream().filter(mng -> OptionTypeEnum.AIVanillaPricer == mng.getOptionType()).collect(Collectors.toList()));
        for (List<TradeConfirmBookDTO> vanilla : vanillaList) {
            //按照交易编号升序
            vanilla.sort(Comparator.comparing(TradeConfirmBookDTO::getTradeCode));
            TradeContractDocument document = new TradeContractDocument();
            //按照交易编号升序
            //交易确认书第一笔交易
            String firstTradeCode =vanilla.get(0).getTradeCode();
            String tradeSettlementCode = "OT-" + firstTradeCode;
            document.setContractCode(tradeSettlementCode);
            document.setFileType(FileTypeEnum.tradeConfirm);

            String fileName = tradeSettlementCode + "+" + vanilla.get(0).getTradeDate().format(DatePattern.PURE_DATE_FORMATTER);
            String fileFormat = ".docx";
            //生成确认书
            ByteArrayInputStream inputStream = buildTradeConfirmBook.generateVanillaAndForwardPricer(CglibUtil.copyList(vanilla, VanillaPricerConfirmBookDTO::new));
            if (dto.getIsPdf() == 1) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                wordToPdfUtil.word2Pdf(inputStream, outputStream);
                inputStream.close();
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                fileFormat = ".pdf";
            }
            fileName = fileName + fileFormat;
            //上传文件到文件服务器
            MinioUploadVO minioUploadVO = fileClient.upload(new MockMultipartFile(fileName, fileName
                    , ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream), FileTypeEnum.tradeConfirm);
            minioUploadVOList.add(minioUploadVO);
            document.setUrl(minioUploadVO.getUrl());
            document.setPath(minioUploadVO.getPath());
            document.setFileName(minioUploadVO.getName());
            //保存文件信息到数据库
            tradeContractDocumentMapper.insert(document);
            for (TradeConfirmBookDTO data : vanilla) {
                TradeContractRel rel = new TradeContractRel();
                rel.setFileId(document.getId());
                rel.setContractCode(tradeSettlementCode);
                rel.setTradeClearCode(tradeSettlementCode);
                rel.setFileType(FileTypeEnum.tradeConfirm);
                rel.setTradeId(data.getId());
                rel.setTradeCode(data.getTradeCode());
                tradeContractRelMapper.insert(rel);
            }
        }
        //远期
        List<List<TradeConfirmBookDTO>> forwardList = groupTradeConfirmBook(confirmBookDTOList.stream().filter(mng -> OptionTypeEnum.AIForwardPricer == mng.getOptionType()).collect(Collectors.toList()));
        for (List<TradeConfirmBookDTO> forward : forwardList) {
            //按照交易编号升序
            forward.sort(Comparator.comparing(TradeConfirmBookDTO::getTradeCode));
            TradeContractDocument document = new TradeContractDocument();
            //按照交易编号升序
            //交易确认书第一笔交易
            String firstTradeCode = forward.get(0).getTradeCode();
            String tradeSettlementCode = "OT-" + firstTradeCode;
            document.setContractCode(tradeSettlementCode);
            document.setFileType(FileTypeEnum.tradeConfirm);

            String fileName = tradeSettlementCode + "+" + forward.get(0).getTradeDate().format(DatePattern.PURE_DATE_FORMATTER);
            String fileFormat = ".docx";
            //生成确认书
            ByteArrayInputStream inputStream = buildTradeConfirmBook.generateVanillaAndForwardPricer(CglibUtil.copyList(forward, VanillaPricerConfirmBookDTO::new));
            if (dto.getIsPdf() == 1) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                wordToPdfUtil.word2Pdf(inputStream, outputStream);
                inputStream.close();
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                fileFormat = ".pdf";
            }
            fileName = fileName + fileFormat;
            //上传文件到文件服务器
            MinioUploadVO minioUploadVO = fileClient.upload(new MockMultipartFile(fileName, fileName
                    , ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream), FileTypeEnum.tradeConfirm);
            minioUploadVOList.add(minioUploadVO);
            document.setUrl(minioUploadVO.getUrl());
            document.setPath(minioUploadVO.getPath());
            document.setFileName(minioUploadVO.getName());
            //保存文件信息到数据库
            tradeContractDocumentMapper.insert(document);
            for (TradeConfirmBookDTO data : forward) {
                TradeContractRel rel = new TradeContractRel();
                rel.setFileId(document.getId());
                rel.setContractCode(tradeSettlementCode);
                rel.setTradeClearCode(tradeSettlementCode);
                rel.setFileType(FileTypeEnum.tradeConfirm);
                rel.setTradeId(data.getId());
                rel.setTradeCode(data.getTradeCode());
                tradeContractRelMapper.insert(rel);
            }
        }
        //亚式期权
        List<TradeConfirmBookDTO> asianList = confirmBookDTOList.stream().filter(mng -> OptionTypeEnum.getAsianOptionType().contains(mng.getOptionType())).collect(Collectors.toList());
        for (TradeConfirmBookDTO confirmBookDTO : asianList) {
            String tradeConfirmCode = "OT-" + confirmBookDTO.getTradeCode();
            String fileName = tradeConfirmCode + "+" + confirmBookDTO.getTradeDate().format(DatePattern.PURE_DATE_FORMATTER);
            String fileFormat = ".docx";
            AsianPricerConfirmBookDTO asianPricerConfirmBookDTO = CglibUtil.copy(confirmBookDTO, AsianPricerConfirmBookDTO.class);
            ByteArrayInputStream inputStream = buildTradeConfirmBook.generateAsianPricer(asianPricerConfirmBookDTO);
            TradeContractDocument document = new TradeContractDocument();
            document.setContractCode(tradeConfirmCode);
            document.setFileType(FileTypeEnum.tradeConfirm);
            if (dto.getIsPdf() == 1) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                wordToPdfUtil.word2Pdf(inputStream, outputStream);
                inputStream.close();
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                fileFormat = ".pdf";
            }
            fileName = fileName + fileFormat;
            //上传文件到文件服务器
            MinioUploadVO minioUploadVO = fileClient.upload(new MockMultipartFile(fileName, fileName
                    , ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream), FileTypeEnum.tradeConfirm);
            log.info("generateAsianPricer:{}", JSONObject.toJSONString(minioUploadVO));
            document.setUrl(minioUploadVO.getUrl());
            document.setPath(minioUploadVO.getPath());
            document.setFileName(minioUploadVO.getName());
            //保存文件信息到数据库
            tradeContractDocumentMapper.insert(document);
            TradeContractRel rel = new TradeContractRel();
            rel.setFileId(document.getId());
            rel.setContractCode(tradeConfirmCode);
            rel.setTradeClearCode(tradeConfirmCode);
            rel.setFileType(FileTypeEnum.tradeConfirm);
            rel.setTradeId(confirmBookDTO.getId());
            rel.setTradeCode(confirmBookDTO.getTradeCode());
            tradeContractRelMapper.insert(rel);
            minioUploadVOList.add(minioUploadVO);
        }
        //累计期权
        List<TradeConfirmBookDTO> accList = confirmBookDTOList.stream().filter(c -> OptionTypeEnum.getAccOption().contains(c.getOptionType())).collect(Collectors.toList());
        for (TradeConfirmBookDTO confirmBookDTO : accList) {
            String tradeConfirmCode = "OT-" + confirmBookDTO.getTradeCode();
            String fileName = tradeConfirmCode + "+" + confirmBookDTO.getTradeDate().format(DatePattern.PURE_DATE_FORMATTER);
            String fileFormat = ".docx";
            AccPricerConfirmBookDTO accPricerConfirmBookDTO = CglibUtil.copy(confirmBookDTO, AccPricerConfirmBookDTO.class);
            ByteArrayInputStream inputStream = buildTradeConfirmBook.generateAccPricer(accPricerConfirmBookDTO);
            TradeContractDocument document = new TradeContractDocument();
            document.setContractCode(tradeConfirmCode);
            document.setFileType(FileTypeEnum.tradeConfirm);
            if (dto.getIsPdf() == 1) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                wordToPdfUtil.word2Pdf(inputStream, outputStream);
                inputStream.close();
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                fileFormat = ".pdf";
            }
            fileName = fileName + fileFormat;
            //上传文件到文件服务器
            MinioUploadVO minioUploadVO = fileClient.upload(new MockMultipartFile(fileName, fileName
                    , ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream), FileTypeEnum.tradeConfirm);
            log.info("minioUploadVO:{}", JSONObject.toJSONString(minioUploadVO));
            document.setUrl(minioUploadVO.getUrl());
            document.setPath(minioUploadVO.getPath());
            document.setFileName(minioUploadVO.getName());
            //保存文件信息到数据库
            tradeContractDocumentMapper.insert(document);
            TradeContractRel rel = new TradeContractRel();
            rel.setFileId(document.getId());
            rel.setContractCode(tradeConfirmCode);
            rel.setTradeClearCode(tradeConfirmCode);
            rel.setFileType(FileTypeEnum.tradeConfirm);
            rel.setTradeId(confirmBookDTO.getId());
            rel.setTradeCode(confirmBookDTO.getTradeCode());
            tradeContractRelMapper.insert(rel);
            minioUploadVOList.add(minioUploadVO);
        }
        //雪球期权
        List<TradeConfirmBookDTO> snowList = confirmBookDTOList.stream().filter(c -> OptionTypeEnum.getSnowBall().contains(c.getOptionType())).collect(Collectors.toList());
        if (!snowList.isEmpty()) {
            // 获取雪球期权的交易ID
            Set<Integer> tradeIdSet = snowList.stream().map(TradeConfirmBookDTO::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<TradeSnowballOption> snowballOptionLambdaQueryWrapper = new LambdaQueryWrapper<>();
            snowballOptionLambdaQueryWrapper.eq(TradeSnowballOption::getIsDeleted, IsDeletedEnum.NO);
            snowballOptionLambdaQueryWrapper.in(TradeSnowballOption::getTradeId, tradeIdSet);
            List<TradeSnowballOption> tradeSnowballOptionList = tradeSnowballOptionMapper.selectList(snowballOptionLambdaQueryWrapper);
            // key = 交易ID , value = TradeSnowballOption对象
            Map<Integer, TradeSnowballOption> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(tradeSnowballOptionList)) {
                map = tradeSnowballOptionList.stream().collect(Collectors.toMap(TradeSnowballOption::getTradeId, item -> item, (v1, v2) -> v2));
            }
            for (TradeConfirmBookDTO confirmBookDTO : snowList) {
                String tradeConfirmCode = "OT-" + confirmBookDTO.getTradeCode();
                String fileName = tradeConfirmCode + "+" + confirmBookDTO.getTradeDate().format(DatePattern.PURE_DATE_FORMATTER);
                String fileFormat = ".docx";
                SnowPricerConfirmBookDTO bookDTO = CglibUtil.copy(confirmBookDTO, SnowPricerConfirmBookDTO.class);
                // 获取客户银行账户信息
                List<BankCardInfoVO> bankCardInfoVOList = bankCardInfoClient.getBankCardInfoByClientId(bookDTO.getClientId());
                // 设置客户银行信息
                if (CollectionUtils.isNotEmpty(bankCardInfoVOList)) {
                    // 取第一条设置银行账户信息
                    BankCardInfoVO bankCardInfo = bankCardInfoVOList.get(0);
                    bookDTO.setBankAccountName(bankCardInfo.getAccountName());
                    bookDTO.setBankOpenBank(bankCardInfo.getOpenBank());
                    bookDTO.setBankAccount(bankCardInfo.getBankAccount());
                    bookDTO.setLargeBankAccount(bankCardInfo.getLargeBankAccount());
                }
                /*
                获取客户联系人: 只取一个, 优先顺序
                联系人，授权签署人，法人，交易下达人
                 */
                List<ClientDutyVO> clientDutyList = clientDutyClient.getClientDutyByClientId(bookDTO.getClientId());
                // 联系人
                List<ClientDutyVO> clientDutyListContact = clientDutyList.stream().filter(item -> item.getContactTypeId().contains("1")).collect(Collectors.toList());
                if (clientDutyListContact.isEmpty()) {
                    // 授权签署人
                    clientDutyListContact = clientDutyList.stream().filter(item -> item.getContactTypeId().contains("4")).collect(Collectors.toList());
                    if (clientDutyListContact.isEmpty()) {
                        // 法定代表人
                        clientDutyListContact = clientDutyList.stream().filter(item -> item.getContactTypeId().contains("2")).collect(Collectors.toList());
                        if (clientDutyListContact.isEmpty()) {
                            // 交易下达人
                            clientDutyListContact = clientDutyList.stream().filter(item -> item.getContactTypeId().contains("3")).collect(Collectors.toList());
                        }
                    }
                }
                // 设置客户联系人信息
                if (!clientDutyListContact.isEmpty()) {
                    ClientDutyVO clientDutyVO = clientDutyListContact.get(0);
                    bookDTO.setClientContactName(clientDutyVO.getContactName());
                    bookDTO.setClientContactAddress(clientDutyVO.getAddress());
                    bookDTO.setClientContactEmail(clientDutyVO.getEmail());
                    bookDTO.setClientContactPhone(clientDutyVO.getPhoneNumber());
                    bookDTO.setClientContactFax(clientDutyVO.getFax());
                }
                ByteArrayInputStream inputStream = buildTradeConfirmBook.generateSnowBallPricer(bookDTO, map.get(confirmBookDTO.getId()));
                TradeContractDocument document = new TradeContractDocument();
                document.setContractCode(tradeConfirmCode);
                document.setFileType(FileTypeEnum.tradeConfirm);
                if (dto.getIsPdf() == 1) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    wordToPdfUtil.word2Pdf(inputStream, outputStream);
                    inputStream.close();
                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                    fileFormat = ".pdf";
                }
                fileName = fileName + fileFormat;
                //上传文件到文件服务器
                MinioUploadVO minioUploadVO = fileClient.upload(new MockMultipartFile(fileName, fileName
                        , ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream), FileTypeEnum.tradeConfirm);
                log.info("generateSnowBallPricer:{}", JSONObject.toJSONString(minioUploadVO));
                document.setUrl(minioUploadVO.getUrl());
                document.setPath(minioUploadVO.getPath());
                document.setFileName(minioUploadVO.getName());
                //保存文件信息到数据库
                tradeContractDocumentMapper.insert(document);
                TradeContractRel rel = new TradeContractRel();
                rel.setFileId(document.getId());
                rel.setContractCode(tradeConfirmCode);
                rel.setTradeClearCode(tradeConfirmCode);
                rel.setFileType(FileTypeEnum.tradeConfirm);
                rel.setTradeId(confirmBookDTO.getId());
                rel.setTradeCode(confirmBookDTO.getTradeCode());
                tradeContractRelMapper.insert(rel);
                minioUploadVOList.add(minioUploadVO);
            }
        }
        return minioUploadVOList;
    }

    @Override
    public void batchDownloadTradeConfirmBook(DownloadTradeConfirmBookDTO dto, HttpServletResponse response) {
        //获取对应交易的文件ID
        Set<Integer> relList = tradeContractRelMapper.selectList(new LambdaQueryWrapper<TradeContractRel>()
                        .eq(TradeContractRel::getIsDeleted, IsDeletedEnum.NO)
                        .in(TradeContractRel::getTradeId, dto.getTradeIdList())
                        .eq(TradeContractRel::getFileType, FileTypeEnum.tradeConfirm))
                .stream().map(TradeContractRel::getFileId).collect(Collectors.toSet());
        BussinessException.E_300100.assertTrue(!relList.isEmpty(), "请先生成交易确认书");
        //获取对应的文件信息
        List<TradeContractDocument> documentList = tradeContractDocumentMapper.selectBatchIds(relList);
        String zipName = "交易确认书_" + System.currentTimeMillis();
        try (ServletOutputStream outputStream = response.getOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            String fileName = URLEncoder.encode(zipName + ".zip", "UTF-8");
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            for (TradeContractDocument document : documentList) {
                ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
                DownloadDTO downloadDTO = new DownloadDTO();
                downloadDTO.setPath(document.getPath());
                Response fileResponse = fileClient.getFile(downloadDTO);
                Response.Body body = fileResponse.body();
                InputStream fileInputStream;
                try {
                    fileInputStream = body.asInputStream();

                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(bytes)) != -1) {
                        dataOutputStream.write(bytes, 0, len);
                    }
                    fileInputStream.close();
                    dataOutputStream.close();
                    dataOutputStream.flush();
                } catch (Exception e) {
                    log.error("获取文件失败:{}",document.getPath(),e);
                }
                zipOutputStream.putNextEntry(new ZipEntry(document.getFileName()));
                zipOutputStream.write(dataOutputStream.toByteArray());
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
            }
        } catch (Exception e) {
            log.error("文件导出失败", e);
        }
    }

    /**
     * 分组整理交易数据
     * @param confirmBookDTOList 交易数据
     * @return 分组后的结果
     */
    private List<List<TradeConfirmBookDTO>> groupTradeConfirmBook(List<TradeConfirmBookDTO> confirmBookDTOList) {
        List<List<TradeConfirmBookDTO>> groupList = new ArrayList<>();
        //按照客户分组
        Map<Integer, List<TradeConfirmBookDTO>> clientDataMap = confirmBookDTOList.stream().collect(Collectors.groupingBy(TradeConfirmBookDTO::getClientId));
        for (Map.Entry<Integer, List<TradeConfirmBookDTO>> entry : clientDataMap.entrySet()) {
            //再按照开仓日期分组
            Map<LocalDate, List<TradeConfirmBookDTO>> dateListMap = entry.getValue().stream().collect(Collectors.groupingBy(TradeConfirmBookDTO::getTradeDate));
            groupList.addAll(dateListMap.values());
        }
        return groupList;
    }
    private List<TradeConfirmBookDTO> setTradeConfirmBook(List<TradeMng> list) {
        //客户ID集合
        Set<Integer> clientIdSet = list.stream().map(TradeMng::getClientId).collect(Collectors.toSet());
        //客户信息
        Map<Integer, ClientVO> clientMap = clientClient.getClientListByIds(clientIdSet).stream().collect(Collectors.toMap(ClientVO::getId, item -> item, (v1, v2) -> v2));
        //合约代码集合
        Set<String> underlyingCodeSet = list.stream().map(TradeMng::getUnderlyingCode).collect(Collectors.toSet());
        Map<String, UnderlyingManagerVO> underlyingManagerMap = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet).stream().collect(Collectors.toMap(UnderlyingManagerVO::getUnderlyingCode, item -> item, (v1, v2) -> v2));
        //标的资产类型
        Map<String, String> assetTypeMap = dictionaryClient.getDictionaryMapByIds("AssetType");
        //获取交易所
        Map<String, String> exchangeMap = dictionaryClient.getDictionaryMapByIds("Exchange");
        //获取交易日数据
        List<TradeObsDate> tradeObsDatesList = tradeObsDateMapper.selectList(new LambdaQueryWrapper<TradeObsDate>()
                .in(TradeObsDate::getTradeId, list.stream().map(TradeMng::getId).collect(Collectors.toSet()))
                .eq(TradeObsDate::getIsDeleted, 0)
                .orderByAsc(TradeObsDate::getObsDate));
        Map<Integer, List<TradeObsDate>> tradeObsDateMap = tradeObsDatesList.stream().collect(Collectors.groupingBy(TradeObsDate::getTradeId));
        List<TradeConfirmBookDTO> confirmBookDTOList = new ArrayList<>();
        list.forEach(tradeMng -> {
            TradeConfirmBookDTO bookDTO = new TradeConfirmBookDTO();
            bookDTO.setId(tradeMng.getId());
            //交易编号
            bookDTO.setTradeCode(tradeMng.getTradeCode());
            //客户信息
            bookDTO.setClientId(tradeMng.getClientId());
            bookDTO.setClientName(clientMap.get(bookDTO.getClientId()).getName());
            // 设置产品卖方和买方名称
            if (tradeMng.getBuyOrSell() != null) {
                bookDTO.setBuyOrSell(tradeMng.getBuyOrSell());
                if (bookDTO.getBuyOrSell() == BuyOrSellEnum.buy) {
                    bookDTO.setProductSellName("甲方");
                    bookDTO.setProductBuyName("乙方");
                } else {
                    bookDTO.setProductSellName("乙方");
                    bookDTO.setProductBuyName("甲方");
                }
            }
            //行权方式
            if (tradeMng.getExerciseType() != null) {
                bookDTO.setExerciseType(tradeMng.getExerciseType());
                bookDTO.setExerciseTypeName(tradeMng.getExerciseType().getDesc());
            }
            //看涨看跌
            if (tradeMng.getCallOrPut() != null) {
                bookDTO.setCallOrPut(tradeMng.getCallOrPut());
                bookDTO.setCallOrPutName(tradeMng.getCallOrPut().getDesc());
            }
            //获取交易对应的合约信息
            UnderlyingManagerVO underlyingManagerVO = underlyingManagerMap.get(tradeMng.getUnderlyingCode());
            BussinessException.E_300100.assertTrue(underlyingManagerVO != null, tradeMng.getUnderlyingCode() + "不存在");
            assert underlyingManagerVO != null;
            //标的资产交易场所
            bookDTO.setExchange(exchangeMap.getOrDefault(underlyingManagerVO.getExchange(), underlyingManagerVO.getExchange()));
            //几个指数的单独处理
            if (!underlyingManagerVO.getUnderlyingCode().contains(".SH")) {
                bookDTO.setUnderlyingCodeByExchange(underlyingManagerVO.getUnderlyingCode() + "." + bookDTO.getExchange());
            } else {
                bookDTO.setUnderlyingCodeByExchange(underlyingManagerVO.getUnderlyingCode());
            }
            bookDTO.setAssetTyp(assetTypeMap.get(underlyingManagerVO.getUnderlyingAssetType()));
            //报价单位
            bookDTO.setQuoteUnit(underlyingManagerVO.getQuoteUnit());
            //权利金单价
            bookDTO.setOptionPremium(BigDecimalUtil.getBigDecimalScale(tradeMng.getOptionPremium(), 2));
            // 成交数量保留四舍五入两位小数
            bookDTO.setTradeVolume(BigDecimalUtil.getBigDecimalScale(tradeMng.getTradeVolume(), 2));
            bookDTO.setTotalAmount(BigDecimalUtil.getBigDecimalScale(tradeMng.getTotalAmount(), 2));
            //名义本金
            bookDTO.setNotionalPrincipal(BigDecimalUtil.getBigDecimalScale(tradeMng.getNotionalPrincipal(), 2));
            // 买卖方向=客户卖出,成交总额取反
            if (bookDTO.getBuyOrSell() == BuyOrSellEnum.sell) {
                bookDTO.setOptionPremium(bookDTO.getOptionPremium().negate());
                bookDTO.setTotalAmount(bookDTO.getTotalAmount().negate());
            }
            //每日数量
            if (tradeMng.getBasicQuantity() != null) {
                bookDTO.setBasicQuantity(BigDecimalUtil.getBigDecimalScale(tradeMng.getBasicQuantity(), 2));
            }
            //固定赔付
            bookDTO.setFixedPayment(BigDecimalUtil.getBigDecimalScale(tradeMng.getFixedPayment() == null ? BigDecimal.ZERO : tradeMng.getFixedPayment(), 2));
            //杠杆系数
            if (tradeMng.getLeverage() != null) {
                bookDTO.setLeverage(tradeMng.getLeverage().stripTrailingZeros());
            }
            //到期倍数
            if (tradeMng.getExpireMultiple() != null) {
                bookDTO.setExpireMultiple(tradeMng.getExpireMultiple().stripTrailingZeros());
            }
            //敲出赔付
            bookDTO.setKnockoutRebate(BigDecimalUtil.getBigDecimalScale(tradeMng.getKnockoutRebate() == null ? BigDecimal.ZERO : tradeMng.getKnockoutRebate(), 2));
            //结算方式
            if (tradeMng.getSettleType() != null) {
                bookDTO.setSettleType(tradeMng.getSettleType());
                bookDTO.setSettleTypeString(tradeMng.getSettleType().getDesc());
            }

            //日期信息
            bookDTO.setTradeDate(tradeMng.getTradeDate());
            bookDTO.setProductStartDate(tradeMng.getProductStartDate());
            bookDTO.setMaturityDate(tradeMng.getMaturityDate());
            //设置价格
            bookDTO.setEntryPrice(BigDecimalUtil.getBigDecimalScale(tradeMng.getEntryPrice(), 2));
            if (tradeMng.getStrike() != null) {
                bookDTO.setStrike(BigDecimalUtil.getBigDecimalScale(tradeMng.getStrike(), 2));
            }
            //敲出价格
            bookDTO.setBarrier(BigDecimalUtil.getBigDecimalScale(tradeMng.getBarrier() == null ? BigDecimal.ZERO : tradeMng.getBarrier(), 2));
            //观察日信息
            if (tradeObsDateMap.containsKey(bookDTO.getId()) && CollectionUtils.isNotEmpty(tradeObsDateMap.get(bookDTO.getId()))) {
                List<TradeObsDate> tradeObsDateListTemp = tradeObsDateMap.get(bookDTO.getId());
                tradeObsDateListTemp.sort(Comparator.comparing(TradeObsDate::getObsDate));
                bookDTO.setObsNumber(tradeObsDateListTemp.size());
                if (tradeObsDateListTemp.get(0).getObsDate() != null) {
                    bookDTO.setStartObsDate(tradeObsDateListTemp.get(0).getObsDate());
                }
                if (tradeObsDateListTemp.get(tradeObsDateListTemp.size() - 1) != null) {
                    bookDTO.setEndObsDate(tradeObsDateListTemp.get(tradeObsDateListTemp.size() - 1).getObsDate());
                }
                bookDTO.setObsDateList(tradeObsDateListTemp);
            }
            //设置期权类型信息
            bookDTO.setOptionType(tradeMng.getOptionType());
            if (tradeMng.getOptionType() == OptionTypeEnum.AIVanillaPricer) {
                bookDTO.setOptionTypeName(tradeMng.getExerciseType().getDesc() + tradeMng.getCallOrPut().getDesc());
            } else {
                bookDTO.setOptionTypeName(bookDTO.getOptionType().getDesc());
            }
            confirmBookDTOList.add(bookDTO);
        });
        return confirmBookDTOList;
    }
    @Override
    public IPage<SettlementConfirmBookVO> settlementConfirmBookSelectByPage(TradeSettlementConfirmBookQueryDTO dto) {
        Page<SettlementConfirmBookVO> page = tradeCloseMngMapper.selectSettlementConfirmBook(new Page<>(dto.getPageNo(), dto.getPageSize()), dto);
        //客户名称
        Set<Integer> clientIdSet = page.getRecords().stream().map(SettlementConfirmBookVO::getClientId).collect(Collectors.toSet());
        Map<Integer, String> clientMap = clientClient.getClientMapByIds(clientIdSet);
        //薄记账户
        Set<Integer> assetIdSet = page.getRecords().stream().map(SettlementConfirmBookVO::getAssetId).collect(Collectors.toSet());
        Map<Integer, String> assetUnitMap = assetUnitClient.getAssetUnitMapByIds(assetIdSet);
        Map<Integer, String> traderMap = userClient.getUserMapByIds(new HashSet<>());
        page.getRecords().forEach(item -> {
            item.setClientName(clientMap.get(item.getClientId()));
            item.setAssetName(assetUnitMap.get(item.getAssetId()));
            item.setTraderName(traderMap.get(item.getTraderId()));
            item.setTradeAddName(traderMap.get(item.getTradeAddId()));
            item.setCreatorName(traderMap.get(item.getCreatorId()));
            item.setTradeStateName(item.getTradeState().getDesc());
            item.setOptionTypeName(item.getOptionType().getDesc());
            item.setOptionCombTypeName(item.getOptionCombType()!=null?item.getOptionCombType().getDesc():"");
        });
        return page;
    }

    @Override
    @Transactional
    public List<MinioUploadVO> buildSettlementConfirmBook(BuildSettlementConfirmBookDTO dto) throws Exception {
        List<TradeCloseMng> tradeCloseMngList = tradeCloseMngMapper.selectList(new LambdaQueryWrapper<TradeCloseMng>()
                .in(TradeCloseMng::getId, dto.getCloseIdList())
                .eq(TradeCloseMng::getIsDeleted, IsDeletedEnum.NO));
        BussinessException.E_300100.assertTrue(!tradeCloseMngList.isEmpty(), "无匹配的平仓记录");
        Set<String> tradeCodeSet = tradeCloseMngList.stream().map(TradeCloseMng::getTradeCode).collect(Collectors.toSet());
        //删除原本的结算确认书数据
        TradeContractRel tradeContractRel = new TradeContractRel();
        tradeContractRel.setIsDeleted(IsDeletedEnum.YES.getFlag());
        tradeContractRelMapper.update(tradeContractRel, new LambdaQueryWrapper<TradeContractRel>()
                .eq(TradeContractRel::getIsDeleted, IsDeletedEnum.NO)
                .in(TradeContractRel::getTradeId, dto.getCloseIdList())
                .in(TradeContractRel::getTradeCode, tradeCodeSet)
                .eq(TradeContractRel::getFileType, FileTypeEnum.settlementConfirm)
        );
        //获取平仓交易对应的交易记录

        List<TradeMng> tradeMngList = tradeMngMapper.selectList(new LambdaQueryWrapper<TradeMng>()
                .eq(TradeMng::getIsDeleted, IsDeletedEnum.NO)
                .in(TradeMng::getTradeCode, tradeCodeSet));
        Map<String, TradeMng> tradeMngMap = tradeMngList.stream().collect(Collectors.toMap(TradeMng::getTradeCode, Function.identity()));

        //获取对应的客户名称
        Set<Integer> clientIdSet = tradeMngList.stream().map(TradeMng::getClientId).collect(Collectors.toSet());
        Map<Integer, String> clientMap = clientClient.getClientMapByIds(clientIdSet);
        DateTimeFormatter dateTimeFormatter = DatePattern.createFormatter("yyyy/MM/dd");
        //构建结算确认书数据
        List<SettlementBookData> settlementBookDataList = CglibUtil.copyList(tradeCloseMngList, SettlementBookData::new, (db, book) -> {

            //平仓部分信息
            book.setCloseVolume(BigDecimalUtil.getBigDecimalScale(db.getCloseVolume(),2));
            book.setCloseEntryPrice(BigDecimalUtil.getBigDecimalScale(db.getCloseEntryPrice(), 2));
            book.setCloseDateStr(book.getCloseDate().format(dateTimeFormatter));
            book.setClosePrice(BigDecimalUtil.getBigDecimalScale(db.getClosePrice(), 2));
            book.setCloseTotalAmount(BigDecimalUtil.getBigDecimalScale(db.getCloseTotalAmount(), 2).negate());
            book.setProfitLoss(BigDecimalUtil.getBigDecimalScale(db.getProfitLoss(), 2).negate());
            //开仓部分信息
            TradeMng tradeMng = tradeMngMap.get(book.getTradeCode());
            BussinessException.E_300100.assertTrue(tradeMng != null, book.getTradeCode() + "找不到对应的交易记录");
            assert tradeMng != null;
            book.setClientId(tradeMng.getClientId());
            book.setTradeDate(tradeMng.getTradeDate());
            book.setTradeDateStr(book.getTradeDate().format(dateTimeFormatter));
            book.setMaturityDate(tradeMng.getMaturityDate());
            book.setMaturityDateStr(book.getMaturityDate().format(dateTimeFormatter));
            book.setUnderlyingCode(tradeMng.getUnderlyingCode());
            if (tradeMng.getBuyOrSell() == BuyOrSellEnum.buy) {
                book.setBuyOrSellName("润和卖");
            } else {
                book.setBuyOrSellName("润和买");
            }
            book.setOptionType(tradeMng.getOptionType());
            if (tradeMng.getOptionType() == OptionTypeEnum.AIVanillaPricer) {
                book.setOptionTypeName(tradeMng.getExerciseType().getDesc() + tradeMng.getCallOrPut().getDesc());
            } else {
                book.setOptionTypeName(book.getOptionType().getDesc());
            }
            book.setStrike(BigDecimalUtil.getBigDecimalScale(tradeMng.getStrike(), 2));
            book.setEntryPrice(BigDecimalUtil.getBigDecimalScale(tradeMng.getEntryPrice(), 2));
            book.setOptionPremium(BigDecimalUtil.getBigDecimalScale(tradeMng.getOptionPremium(), 2).negate());
            book.setTotalAmount(BigDecimalUtil.getBigDecimalScale(book.getCloseVolume().multiply(book.getOptionPremium()), 2));
            //客户名称
            book.setClientName(clientMap.get(book.getClientId()));
        });
        List<SettlementBook> bookList = getSettementBookList(settlementBookDataList);
        List<MinioUploadVO> minioUploadVOList = new ArrayList<>();
        for (SettlementBook book : bookList) {
            TradeContractDocument document = new TradeContractDocument();
            //按照交易编号升序
            book.getData().sort(Comparator.comparing(SettlementBookData::getTradeCode));
            //交易确认书第一笔交易
            String firstTradeCode = book.getData().get(0).getTradeCode();
            Long count = tradeContractRelMapper.selectCount(new LambdaQueryWrapper<TradeContractRel>()
                    .eq(TradeContractRel::getTradeCode, firstTradeCode)
                    .eq(TradeContractRel::getIsDeleted, IsDeletedEnum.NO)
                    .eq(TradeContractRel::getFileType, FileTypeEnum.settlementConfirm)
            );
            String tradeSettlementCode = "OS-" + firstTradeCode + "_" + count;
            document.setContractCode(tradeSettlementCode);
            document.setFileType(FileTypeEnum.settlementConfirm);

            String fileName = tradeSettlementCode + "+" + book.getData().get(0).getCloseDate().format(DatePattern.PURE_DATE_FORMATTER);
            String fileFormat = ".docx";
            //生成结算确认书
            ByteArrayInputStream inputStream = buildTradeSettlementBook.generateSettlementBook(book);
            if (dto.getIsPdf() == 1) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                wordToPdfUtil.word2Pdf(inputStream, outputStream);
                inputStream.close();
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                fileFormat = ".pdf";
            }
            fileName = fileName + fileFormat;
            //上传文件到文件服务器
            MinioUploadVO minioUploadVO = fileClient.upload(new MockMultipartFile(fileName, fileName
                    , ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream), FileTypeEnum.settlementConfirm);
            minioUploadVOList.add(minioUploadVO);
            log.info("结算确认书上传结果:{}", JSONObject.toJSONString(minioUploadVO));
            document.setUrl(minioUploadVO.getUrl());
            document.setPath(minioUploadVO.getPath());
            document.setFileName(minioUploadVO.getName());
            //保存文件信息到数据库
            tradeContractDocumentMapper.insert(document);
            for (SettlementBookData data : book.getData()) {
                TradeContractRel rel = new TradeContractRel();
                rel.setFileId(document.getId());
                rel.setContractCode(tradeSettlementCode);
                rel.setTradeClearCode(tradeSettlementCode);
                rel.setFileType(FileTypeEnum.settlementConfirm);
                rel.setTradeId(data.getId());
                rel.setTradeCode(data.getTradeCode());
                tradeContractRelMapper.insert(rel);
            }
        }
        return minioUploadVOList;
    }

    private List<SettlementBook> getSettementBookList(List<SettlementBookData> settlementBookDataList) {
        List<SettlementBook> bookList = new ArrayList<>();
        //按照客户分组
        Map<Integer, List<SettlementBookData>> clientDataMap = settlementBookDataList.stream().collect(Collectors.groupingBy(SettlementBookData::getClientId));
        for (Map.Entry<Integer, List<SettlementBookData>> entry : clientDataMap.entrySet()) {
            //再按照平仓日期分组
            Map<LocalDate, List<SettlementBookData>> dateListMap = entry.getValue().stream().collect(Collectors.groupingBy(SettlementBookData::getCloseDate));
            for (Map.Entry<LocalDate, List<SettlementBookData>> dateEntry : dateListMap.entrySet()) {
                //按照结算确认书模板分组
                Map<SettlementBookTypeEnum, List<SettlementBookData>> bookTypeEnumListMap = dateEntry.getValue().stream().collect(
                        Collectors.groupingBy(data -> {
                            if (SettlementBookTypeEnum.asianPricer.getOptionTyp().contains(data.getOptionType())) {
                                return SettlementBookTypeEnum.asianPricer;
                            } else if (SettlementBookTypeEnum.snowBallPricer.getOptionTyp().contains(data.getOptionType())) {
                                return SettlementBookTypeEnum.snowBallPricer;
                            } else {
                                return SettlementBookTypeEnum.otherPricer;
                            }
                        })
                );
                //循环构造最终数据
                for (Map.Entry<SettlementBookTypeEnum, List<SettlementBookData>> bookTypeEnumListEntry : bookTypeEnumListMap.entrySet()) {
                    SettlementBook settlementBook = new SettlementBook();
                    settlementBook.setSettlementBookType(bookTypeEnumListEntry.getKey());
                    settlementBook.setClientName(bookTypeEnumListEntry.getValue().get(0).getClientName());
                    settlementBook.setCloseDateSrt(bookTypeEnumListEntry.getValue().get(0).getCloseDateStr());
                    settlementBook.setData(bookTypeEnumListEntry.getValue());
                    bookList.add(settlementBook);
                }
            }
        }

        return bookList;
    }

    @Override
    public void batchDownloadSettlementConfirmBook(DownloadSettlementConfirmBookDTO dto, HttpServletResponse response) {
        //获取对应交易的文件ID
        Set<Integer> relList = tradeContractRelMapper.selectList(new LambdaQueryWrapper<TradeContractRel>()
                        .eq(TradeContractRel::getIsDeleted, IsDeletedEnum.NO)
                        .in(TradeContractRel::getTradeId, dto.getCloseIdList())
                        .eq(TradeContractRel::getFileType, FileTypeEnum.settlementConfirm))
                .stream().map(TradeContractRel::getFileId).collect(Collectors.toSet());
        BussinessException.E_300100.assertTrue(!relList.isEmpty(), "请先生成结算确认书");
        //获取对应的文件信息
        List<TradeContractDocument> documentList = tradeContractDocumentMapper.selectBatchIds(relList);
        String zipName = "结算确认书_" + System.currentTimeMillis();
        try (ServletOutputStream outputStream = response.getOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            String fileName = URLEncoder.encode(zipName + ".zip", "UTF-8");
            response.setContentType("application/zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            for (TradeContractDocument document : documentList) {
                ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
                DownloadDTO downloadDTO = new DownloadDTO();
                downloadDTO.setPath(document.getPath());
                Response fileResponse = fileClient.getFile(downloadDTO);
                Response.Body body = fileResponse.body();
                InputStream fileInputStream;
                try {
                    fileInputStream = body.asInputStream();

                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(bytes)) != -1) {
                        dataOutputStream.write(bytes, 0, len);
                    }
                    fileInputStream.close();
                    dataOutputStream.close();
                    dataOutputStream.flush();
                } catch (Exception e) {
                   log.error("获取文件失败:{}",document.getPath(),e);
                }
                zipOutputStream.putNextEntry(new ZipEntry(document.getFileName()));
                zipOutputStream.write(dataOutputStream.toByteArray());
                zipOutputStream.flush();
                zipOutputStream.closeEntry();
            }
        } catch (Exception e) {
            log.error("文件导出失败", e);
        }
    }

    @Override
    public BigDecimal getRealizeProfitLoss(ProfitLossAppraisementDto dto) {
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.in(TradeMng::getTradeState, TradeStateEnum.getHaveCloseStateList());
        tradeMngLambdaQueryWrapper.ge(dto.getStartDate() != null, TradeMng::getMaturityDate, dto.getStartDate());
        tradeMngLambdaQueryWrapper.le(dto.getEndDate() != null, TradeMng::getMaturityDate, dto.getEndDate());
        List<TradeMng> tradeMngList = tradeMngMapper.selectList(tradeMngLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(tradeMngList)) {
            return tradeMngList.stream().map(TradeMng::getTotalProfitLoss).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Map<Integer, BigDecimal> getProfitLossByClient(CloseProfitLossDTO closeProfitLossDTO) {
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.in(TradeMng::getTradeState, TradeStateEnum.getHaveCloseStateList());
        tradeMngLambdaQueryWrapper.ge(closeProfitLossDTO.getStartDate() != null, TradeMng::getMaturityDate, closeProfitLossDTO.getStartDate());
        tradeMngLambdaQueryWrapper.le(closeProfitLossDTO.getEndDate() != null, TradeMng::getMaturityDate, closeProfitLossDTO.getEndDate());
        List<TradeProfitLossByClientVO> list = tradeMngMapper.selectProfitLossByClient(closeProfitLossDTO.getStartDate(), closeProfitLossDTO.getEndDate(), closeProfitLossDTO.getClientIdList());
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().collect(Collectors.toMap(TradeProfitLossByClientVO::getClientId, TradeProfitLossByClientVO::getProfitLoss));
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public List<TradeMng> getTradeMngListByTradeCodeSet(Set<String> tradeCodeSet) {
        LambdaQueryWrapper<TradeMng> tradeMngLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tradeMngLambdaQueryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        tradeMngLambdaQueryWrapper.in(TradeMng::getTradeCode, tradeCodeSet);
        return this.list(tradeMngLambdaQueryWrapper);
    }

    @Override
    public List<TradeMngByDailyVO> getTradeMngByDaily(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType) {
        List<Integer> clientIdList = new ArrayList<>();
        if (isNotInside) {
            //如果仅获取外部客户时，则排除掉内部客户的交易
            clientIdList  = clientClient.getInsideClientIdList();
        }
        List<String> underlyingCodeList = new ArrayList<>();
        if (assetType!=null){
            List<UnderlyingManagerVO> underlyingManagerVOList=  underlyingManagerClient.getUnderlyingListByAssetType(assetType,queryDate);
            underlyingCodeList=underlyingManagerVOList.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toList());
        }

        return tradeMngMapper.selectTradeMngByDaily(queryDate,underlyingCodeList,clientIdList);
    }

    @Override
    public Page<TradeMngByDailyVO> getTradeMngByDaily(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType, Integer pageNo, Integer pageSize) {
        Page<TradeMngByDailyVO> page = new Page<>(pageNo,pageSize);
        List<Integer> clientIdList = new ArrayList<>();
        if (isNotInside) {
            //如果仅获取外部客户时，则排除掉内部客户的交易
            clientIdList  = clientClient.getInsideClientIdList();
        }
        List<String> underlyingCodeList = new ArrayList<>();
        if (assetType!=null){
            List<UnderlyingManagerVO> underlyingManagerVOList=  underlyingManagerClient.getUnderlyingListByAssetType(assetType,queryDate);
            underlyingCodeList=underlyingManagerVOList.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toList());
        }

        return tradeMngMapper.selectTradeMngByDaily(page,queryDate,underlyingCodeList,clientIdList);
    }

    @Override
    public String updateTradeSettleType(UpdateTradeSettleTypeDTO updateTradeSettleTypeDTO) {

        return null;
    }
}
