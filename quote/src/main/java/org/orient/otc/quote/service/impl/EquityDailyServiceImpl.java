package org.orient.otc.quote.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.client.dto.GrantCreditDTO;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.feign.GrantCreditClient;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.api.quote.vo.TradeObsDateVO;
import org.orient.otc.api.system.feign.DictionaryClient;
import org.orient.otc.api.system.feign.StructureClient;
import org.orient.otc.api.system.vo.StructureInfoVO;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.exception.BaseException;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.quote.dto.daily.DailyPageDTO;
import org.orient.otc.quote.entity.TradeRiskInfo;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.service.EquityDailyService;
import org.orient.otc.quote.service.TradeMngService;
import org.orient.otc.quote.service.TradeRiskInfoService;
import org.orient.otc.quote.vo.daily.EquityPositionDailyVO;
import org.orient.otc.quote.vo.daily.EquityTradeDailyVO;
import org.orient.otc.quote.vo.daily.PositionDailyVO;
import org.orient.otc.quote.vo.daily.TradeMngByDailyVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权益报送服务实现
 */
@Service
public class EquityDailyServiceImpl implements EquityDailyService {

    @Resource
    private TradeMngService tradeMngService;
    @Resource
    private TradeRiskInfoService tradeRiskInfoService;
    @Resource
    private ClientClient clientClient;
    @Resource
    private GrantCreditClient grantCreditClient;

    @Resource
    private StructureClient structureClient;

    @Resource
    private DictionaryClient dictionaryClient;

    @Resource
    private UnderlyingManagerClient underlyingManagerClient;
    @Resource
    private SystemConfigUtil systemConfigUtil;

    @Value("${template.daily}")
    private String dailyTemplatePath;
    @Override
    public IPage<EquityTradeDailyVO> getEquityTradeDailyVOByPage(DailyPageDTO dto) {
        //获取权益对应的交易信息
        Page<TradeMngByDailyVO> tradeMngByDailyVOPage = tradeMngService.getTradeMngByDaily(dto.getQueryDate(), Boolean.TRUE, AssetTypeEnum.EQ, dto.getPageNo(), dto.getPageSize());
        Page<EquityTradeDailyVO> page = new Page<>();
        page.setRecords(convertTradeDaily(dto.getQueryDate(), tradeMngByDailyVOPage.getRecords()));
        page.setTotal(tradeMngByDailyVOPage.getTotal());
        page.setSize(tradeMngByDailyVOPage.getSize());
        page.setCurrent(tradeMngByDailyVOPage.getCurrent());
        return page;
    }

    @Override
    public void exportEquityTradeDaily(LocalDate queryDate, HttpServletResponse response) throws IOException {
        //获取系统配置信息
        String mainLicenseCode = systemConfigUtil.getMainLicenseCode();
        StringBuilder fileNameBuilder = new StringBuilder("CN_");
        fileNameBuilder.append(mainLicenseCode).append("_");
        fileNameBuilder.append("CF0601_");
        fileNameBuilder.append(queryDate.format(DatePattern.NORM_DATE_FORMATTER));
        OutputStream outputStream = response.getOutputStream();
        response.setContentType("application/ms-excel");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode(fileNameBuilder.append(".xlsx").toString(), "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        String templateFileName = dailyTemplatePath + "equityTradeDaily.xlsx";
        List<TradeMngByDailyVO> tradeMngByDailyVOList = tradeMngService.getTradeMngByDaily(queryDate, Boolean.TRUE, AssetTypeEnum.EQ);
        List<EquityTradeDailyVO> equityTradeDailyVOList = convertTradeDaily(queryDate, tradeMngByDailyVOList);
        try (ExcelWriter excelWriter = EasyExcel
                .write(outputStream)
                .withTemplate(templateFileName)
                .inMemory(true)
                .build()) {
            //填充数据
            WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
            excelWriter.fill(equityTradeDailyVOList, writeSheet);

        }
    }

    @Override
    public IPage<EquityPositionDailyVO> getEquityPositionDailyByPage(DailyPageDTO dto) {
        Page<PositionDailyVO> positionDailyVOPage = tradeRiskInfoService.getPositionDailyByPage(dto.getQueryDate(), true, AssetTypeEnum.EQ, dto.getPageNo(), dto.getPageSize());
        Page<EquityPositionDailyVO> page = new Page<>();
        page.setRecords(convertPositionDaily(dto.getQueryDate(), positionDailyVOPage.getRecords()));
        page.setTotal(positionDailyVOPage.getTotal());
        page.setSize(positionDailyVOPage.getSize());
        page.setCurrent(positionDailyVOPage.getCurrent());
        return page;
    }

    @Override
    public void exportEquityPositionDaily(LocalDate queryDate, HttpServletResponse response) throws IOException {
        //获取系统配置信息
        String mainLicenseCode = systemConfigUtil.getMainLicenseCode();
        StringBuilder fileNameBuilder = new StringBuilder("CN_");
        fileNameBuilder.append(mainLicenseCode).append("_");
        fileNameBuilder.append("CF0601_");
        fileNameBuilder.append(queryDate.format(DatePattern.NORM_DATE_FORMATTER));
        OutputStream outputStream = response.getOutputStream();
        response.setContentType("application/ms-excel");
        response.setCharacterEncoding("UTF-8");
        String fileName = URLEncoder.encode(fileNameBuilder.append(".xlsx").toString(), "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        String templateFileName = dailyTemplatePath + "equityPositionDaily.xlsx";
        List<PositionDailyVO> positionDailyVOList = tradeRiskInfoService.getPositionDailyList(queryDate, Boolean.TRUE, AssetTypeEnum.EQ);
        List<EquityPositionDailyVO> equityTradeDailyVOList = convertPositionDaily(queryDate, positionDailyVOList);
        try (ExcelWriter excelWriter = EasyExcel
                .write(outputStream)
                .withTemplate(templateFileName)
                .inMemory(true)
                .build()) {
            //填充数据
            WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
            excelWriter.fill(equityTradeDailyVOList, writeSheet);

        }
    }

    private List<EquityPositionDailyVO> convertPositionDaily(LocalDate queryDate, List<PositionDailyVO> positionDailyVOList) {

        //交易确认书编号分组
        Map<String, List<String>> tradeConfirmCodeMap = positionDailyVOList.stream()
                .collect(Collectors.groupingBy(pos->pos.getTradeConfirmCode()==null?"":pos.getTradeConfirmCode()
                        , Collectors.mapping(PositionDailyVO::getTradeCode, Collectors.toList())));
        //客户ID集合
        Set<Integer> clientIdSet = positionDailyVOList.stream().map(PositionDailyVO::getClientId).collect(Collectors.toSet());
        //客户信息
        Map<Integer, ClientVO> clientVOMap = clientClient.getClientListByIds(clientIdSet).stream().collect(Collectors.toMap(ClientVO::getId, item -> item, (v1, v2) -> v2));
        //合约代码集合
        Set<String> underlyingCodeSet = positionDailyVOList.stream().map(PositionDailyVO::getUnderlyingCode).collect(Collectors.toSet());
        Map<String, UnderlyingManagerVO> underlyingManagerVOMap = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet)
                .stream().collect(Collectors.toMap(UnderlyingManagerVO::getUnderlyingCode, item -> item, (v1, v2) -> v2));
        //标的资产类型
        Map<String, String> assetTypeMap = dictionaryClient.getDictionaryMapByIds("AssetType");

        //获取系统自定义结构报送信息
        List<StructureInfoVO> structureInfoVOList = structureClient.getStructureInfoList();
        Map<String, String> structureMap = structureInfoVOList.stream().collect(Collectors.toMap(StructureInfoVO::getStructureName, StructureInfoVO::getSubmittedStructureName));
        String mainName = systemConfigUtil.getMainName();
        String mainLicenseCode = systemConfigUtil.getMainLicenseCode();
        List<EquityPositionDailyVO> equityPositionDailyVOList = new ArrayList<>();
        positionDailyVOList.forEach(item -> {
            EquityPositionDailyVO positionDailyVO = new EquityPositionDailyVO();
            positionDailyVO.setMainName(mainName);
            positionDailyVO.setMainLicenseCode(mainLicenseCode);
            ClientVO clientVO = clientVOMap.get(item.getClientId());
            BussinessException.E_300100.assertNotNull(clientVO, "客户ID:" + item.getClientId() + "客户信息不存在");
            //交易对手方信息
            positionDailyVO.setClientName(clientVO.getName());
            positionDailyVO.setClientLicenseCode(clientVO.getLicenseCode());
            //交易确认时间
            positionDailyVO.setConfirmDate(item.getTradeDate());
            //持仓日期
            positionDailyVO.setPositionDate(item.getRiskDate());
            //填报方向
            positionDailyVO.setBuyOrSell(item.getBuyOrSell() == BuyOrSellEnum.buy ? "S" : "B");
            //合约信息
            UnderlyingManagerVO underlyingManagerVO = underlyingManagerVOMap.get(item.getUnderlyingCode());
            //资产类型
            positionDailyVO.setAssetType(assetTypeMap.get(underlyingManagerVO.getUnderlyingAssetType()));
            //工具类型
            positionDailyVO.setToolType(OptionTypeEnum.AIForwardPricer == item.getOptionType() ? "FW" : "OP");
            //权益的报送行权方式目前暂时都用欧式
            positionDailyVO.setExerciseType("EU");
            //期权权利类型
            if (item.getCallOrPut() != null) {
                positionDailyVO.setOptionRightsType(item.getCallOrPut() == CallOrPutEnum.call ? "2" : "1");
            }
            if (OptionTypeEnum.getDailyPut().contains(item.getOptionType())) {
                positionDailyVO.setOptionRightsType("1");
            }
            if (OptionTypeEnum.getDailyCall().contains(item.getOptionType())) {
                positionDailyVO.setOptionRightsType("2");
            }
            if (OptionTypeEnum.getChooser().contains(item.getOptionType())) {
                positionDailyVO.setOptionRightsType("9");
            }
            //产品结构
            if (item.getOptionCombType() != null) {
                positionDailyVO.setStructureType(item.getOptionCombType().getKey());
            } else {
                if (item.getOptionType() != OptionTypeEnum.AIForwardPricer) {
                    if (item.getOptionType() != OptionTypeEnum.AICustomPricer) {
                        positionDailyVO.setStructureType(item.getOptionType().getKey());
                    } else {
                        positionDailyVO.setStructureType(structureMap.get(item.getStructureType()));
                    }
                }
            }
            //交易确认书编码
            positionDailyVO.setTradeConfirmCode(item.getTradeConfirmCode());
            List<String> tradeCodeList = tradeConfirmCodeMap.get(item.getTradeConfirmCode());
            if (tradeCodeList != null && tradeCodeList.size() > 1) {
                positionDailyVO.setConfirmId(tradeCodeList.indexOf(item.getTradeCode()));
            }
            //参与率
            positionDailyVO.setParticipationRate("1");
            //保本雪球为Y其它均为N
            positionDailyVO.setIsAnnualizedOption(OptionTypeEnum.getChooser().contains(item.getOptionType()) ? "Y" : "N");
            //标的资产类型
            positionDailyVO.setUnderlyingAssetType(underlyingManagerVO.getUnderlyingAssetType());
            //标的资产品种
            positionDailyVO.setVarietyCode(underlyingManagerVO.getVarietyCode());
            //标的资产对应合约
            positionDailyVO.setExchangeUnderlyingCode(underlyingManagerVO.getExchangeUnderlyingCode());
            if (underlyingManagerVO.getExchangeUnderlyingCode().contains(".SH")) {
                positionDailyVO.setVarietyCode(underlyingManagerVO.getExchangeUnderlyingCode());
            }
            //执行价格
            positionDailyVO.setStrike(item.getStrike());
            //合约估值时标的价格
            positionDailyVO.setLastPrice(item.getLastPrice());
            //合约估值
            positionDailyVO.setAvailableAmount(item.getAvailableAmount());
            //估值方法
            positionDailyVO.setValuationType("O");
            //总名义金额
            positionDailyVO.setNotionalPrincipal(item.getNotionalPrincipal());
            //已平仓名义金额
            positionDailyVO.setCloseNotionalPrincipal(positionDailyVO.getNotionalPrincipal().subtract(item.getAvailableNotionalPrincipal()));
            //价格符号
            positionDailyVO.setPriceSymbol("1");
            //计价货币
            positionDailyVO.setInvoicingCurrency("CNY");
            //总名义数量
            positionDailyVO.setTotalVolume(item.getTradeVolume());
            //交易名义数量
            positionDailyVO.setCloseVolume(item.getTradeVolume().subtract(item.getAvailableVolume()));
            //数量单位
            positionDailyVO.setPoint(underlyingManagerVO.getUnit());
            //估值波动率
            positionDailyVO.setValuationVol(BigDecimalUtil.percentageToBigDecimal(item.getNowVol()));
            positionDailyVO.setTradeDayByYear(245);
            positionDailyVO.setDelta(item.getDelta());
            positionDailyVO.setGamma(item.getGamma());
            positionDailyVO.setVega(item.getVega());
            positionDailyVO.setTheta(item.getTheta());
            positionDailyVO.setRho(item.getRho());
            positionDailyVO.setDeltaCash(item.getDeltaCash());
            positionDailyVO.setRiskFreeInterestRate(new BigDecimal("0.03"));
            positionDailyVO.setDividendYield(new BigDecimal("0.03"));
            positionDailyVO.setGammaCash(item.getGammaCash());
            //到期日期
            positionDailyVO.setMaturityDate(item.getMaturityDate());

            if ("SA".equals(positionDailyVO.getStructureType())) {
                //获取敲出价格
                Set<BigDecimal> barrierSet = item.getObsDateList().stream().map(TradeObsDateVO::getBarrier).collect(Collectors.toSet());
                Boolean barrierNotChange = Boolean.TRUE;
                if (barrierSet.size() != 1) {
                    barrierNotChange = Boolean.FALSE;
                }
                //雪球分类
                //雪球结构要素
                switch (item.getOptionType()) {
                    case AISnowBallPutPricer:
                        if (barrierNotChange) {
                            positionDailyVO.setSnowStructureType("1");
                        } else {
                            positionDailyVO.setSnowStructureType("3");
                        }
                        positionDailyVO.setSnowType("1");
                        positionDailyVO.setKnockInOptionType("2");
                        break;
                    case AISnowBallCallPricer:
                        if (barrierNotChange) {
                            positionDailyVO.setSnowStructureType("1");
                        } else {
                            positionDailyVO.setSnowStructureType("3");
                        }
                        positionDailyVO.setSnowType("1");
                        positionDailyVO.setKnockInOptionType("1");
                        break;
                    case AILimitLossesSnowBallCallPricer:
                        if (barrierNotChange) {
                            positionDailyVO.setSnowStructureType("5");
                        } else {
                            positionDailyVO.setSnowStructureType("9");
                        }
                        positionDailyVO.setSnowType("2");
                        positionDailyVO.setKnockInOptionType("3");
                        break;
                    case AILimitLossesSnowBallPutPricer:
                        if (barrierNotChange) {
                            positionDailyVO.setSnowStructureType("4");
                        } else {
                            positionDailyVO.setSnowStructureType("9");
                        }
                        positionDailyVO.setSnowType("2");
                        positionDailyVO.setKnockInOptionType("4");
                        break;
                    case AIBreakEvenSnowBallCallPricer:
                    case AIBreakEvenSnowBallPutPricer:
                        positionDailyVO.setSnowStructureType("9");
                        positionDailyVO.setSnowType("4");
                        positionDailyVO.setKnockInOptionType("9");
                        break;
                }
                //获取后续最近一次的观察日期
                TradeObsDateVO tradeObsDateVO = item.getObsDateList().stream()
                        .filter(obs -> !queryDate.isAfter(obs.getObsDate()))
                        .findFirst().orElseThrow(() -> new BaseException(BussinessException.E_300100, item.getTradeCode() + "找不到后续的观察日期"));
                BigDecimal barrier;
                if (tradeObsDateVO.getBarrierRelative() != null && tradeObsDateVO.getBarrierRelative()) {
                    barrier = item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(tradeObsDateVO.getBarrier()));
                } else {
                    barrier = tradeObsDateVO.getBarrier();
                }
                //雪球期权状态
                if (!item.getAlreadyKnockedIn()) {
                    positionDailyVO.setSnowStatus("1");
                } else {
                    //设置敲出标识
                    if (OptionTypeEnum.getCallKnockOut().contains(item.getOptionType())) {
                        //看涨期权最新价格大于或者等于敲出价格则为敲出
                        if (item.getLastPrice().compareTo(barrier) >= 0) {
                            positionDailyVO.setSnowStatus("2");
                        } else {
                            positionDailyVO.setSnowStatus("3");
                        }

                    }
                    if (OptionTypeEnum.getPutKnockOut().contains(item.getOptionType())) {
                        //看跌期权最新价格小于或者等于敲出价格则为敲出
                        if (item.getLastPrice().compareTo(barrier) <= 0) {
                            positionDailyVO.setSnowStatus("2");
                        } else {
                            positionDailyVO.setSnowStatus("3");
                        }
                    }
                }
                //交易对手类型
                if (clientVO.getClientSuperviseType() != null) {
                    switch (clientVO.getClientSuperviseType()) {
                        case 1:
                            positionDailyVO.setClientType("1");
                            break;
                        case 2:
                            positionDailyVO.setClientType("2");
                            break;
                        case 3:
                            positionDailyVO.setClientType("3");
                            break;
                        case 4:
                            positionDailyVO.setClientType("4");
                            break;
                        default:
                            positionDailyVO.setClientType("9");
                            break;
                    }

                }

                //敲出观察日和敲出观察价格
                List<String> obsDateFormatList = new ArrayList<>();
                List<String>  barrierFormatList = new ArrayList<>();
                for (TradeObsDateVO vo : item.getObsDateList()) {
                    obsDateFormatList.add(vo.getObsDate().format(DatePattern.NORM_DATE_FORMATTER));
                    if (vo.getBarrierRelative()) {
                        barrierFormatList.add(item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(vo.getBarrier())).setScale(2, RoundingMode.HALF_UP).toString());
                    } else {
                        barrierFormatList.add(vo.getBarrier().toString());
                    }
                }
                positionDailyVO.setObsKnockOutDate(String.join(";", obsDateFormatList));
                positionDailyVO.setObsKnockOutPrice(String.join(";", barrierFormatList));
                //敲入信息
                //格式化敲入价格
                if (item.getKnockinBarrierValue() != null) {
                    positionDailyVO.setObsKnockInDate("逐日");
                    if (item.getKnockinBarrierRelative()) {
                        positionDailyVO.setObsKnockInPrice(item.getEntryPrice().multiply(BigDecimalUtil.percentageToBigDecimal(item.getKnockinBarrierValue())).setScale(2, RoundingMode.HALF_UP).toString());
                    } else {
                        positionDailyVO.setObsKnockInPrice(item.getKnockinBarrierValue().toString());
                    }
                }
                //标的初始价格
                positionDailyVO.setEntryPrice(item.getEntryPrice());
                //期权费率
                if (item.getOptionPremiumPercent()!=null){
                    positionDailyVO.setOptionPremiumPercent(item.getOptionPremiumPercent()+"%");
                }
                //保底比例
                if(item.getStrike2OnceKnockedinValue()!=null){
                    BigDecimal strike2;
                    if (!item.getStrike2OnceKnockedinRelative()){
                        strike2=  BigDecimalUtil.bigDecimalToPercentage(item.getStrike2OnceKnockedinValue().divide(item.getEntryPrice(),2,RoundingMode.HALF_UP));
                    }else {
                        strike2=item.getStrike2OnceKnockedinValue();
                    }
                    BigDecimal strike;
                    if (!item.getStrikeOnceKnockedinRelative()){
                        strike=  BigDecimalUtil.bigDecimalToPercentage(item.getStrikeOnceKnockedinValue().divide(item.getEntryPrice(),2,RoundingMode.HALF_UP));
                    }else {
                        strike=item.getStrikeOnceKnockedinValue();
                    }
                    positionDailyVO.setMinimumGuaranteeRatio(new BigDecimal("100").subtract(strike.subtract(strike2).abs().setScale(0,RoundingMode.HALF_UP)) +"%");
                }
                //保证金信息
                if (OptionTypeEnum.getSnowBall().contains(item.getOptionType())) {
                    //雪球的直接使用保证金占用
                    positionDailyVO.setInitMargin(item.getUseMargin());
                } else {
                    //非雪球的使用初始保证金单价乘成交数量
                    positionDailyVO.setInitMargin(item.getInitMargin().multiply(item.getTradeVolume()));
                }
                //维持保证金
                positionDailyVO.setMargin(item.getMargin());
                //风险中性收益率
                positionDailyVO.setRiskNeutralReturnRate(positionDailyVO.getRiskFreeInterestRate().subtract(positionDailyVO.getDividendYield()));
                //红利票息率
                positionDailyVO.setBonusRateStructValue(item.getBonusRateStructValue());
                //敲出票息率
                positionDailyVO.setKnockOutRebateRate(tradeObsDateVO.getRebateRate());
            }
            equityPositionDailyVOList.add(positionDailyVO);
        });
        return equityPositionDailyVOList;
    }

    private List<EquityTradeDailyVO> convertTradeDaily(LocalDate queryDate, List<TradeMngByDailyVO> tradeMngByDailyVOList) {
        //交易确认书编号分组
        Map<String, List<String>> tradeConfirmCodeMap = tradeMngByDailyVOList.stream()
                .filter(tradeMngByDailyVO -> tradeMngByDailyVO.getSettlementConfirmCode() == null)
                .collect(Collectors.groupingBy(pos->pos.getTradeConfirmCode()==null?"":pos.getTradeConfirmCode()
                        , Collectors.mapping(TradeMngByDailyVO::getTradeCode, Collectors.toList())));
        //结算确认书编号分组
        Map<String, List<Integer>> settlementConfirmMap = tradeMngByDailyVOList.stream()
                .filter(tradeMngByDailyVO -> tradeMngByDailyVO.getSettlementConfirmCode() != null)
                .collect(Collectors.groupingBy(TradeMngByDailyVO::getSettlementConfirmCode
                        , Collectors.mapping(TradeMngByDailyVO::getCloseId, Collectors.toList())));
        //客户ID集合
        Set<Integer> clientIdSet = tradeMngByDailyVOList.stream().map(TradeMngByDailyVO::getClientId).collect(Collectors.toSet());
        //客户信息
        Map<Integer, ClientVO> clientVOMap = clientClient.getClientListByIds(clientIdSet).stream().collect(Collectors.toMap(ClientVO::getId, item -> item, (v1, v2) -> v2));
        //授信信息
        GrantCreditDTO grantCreditDTO = new GrantCreditDTO();
        grantCreditDTO.setClientIdList(clientIdSet);
        grantCreditDTO.setEndDate(queryDate);
        Map<Integer, BigDecimal> grantCreditMap = grantCreditClient.getClientGrantCredit(grantCreditDTO);
        //合约代码集合
        Set<String> underlyingCodeSet = tradeMngByDailyVOList.stream().map(TradeMngByDailyVO::getUnderlyingCode).collect(Collectors.toSet());
        Map<String, UnderlyingManagerVO> underlyingManagerVOMap = underlyingManagerClient.getUnderlyingByCodes(underlyingCodeSet).stream().collect(Collectors.toMap(UnderlyingManagerVO::getUnderlyingCode, item -> item, (v1, v2) -> v2));
        //标的资产类型
        Map<String, String> assetTypeMap = dictionaryClient.getDictionaryMapByIds("AssetType");
        //获取交易所
        Map<String, String> exchangeMap = dictionaryClient.getDictionaryMapByIds("Exchange");
        //获取日终风险信息
        List<TradeRiskInfo> tradeRiskInfoList = tradeRiskInfoService.selectTradeRiskInfoListByRiskDate(null, queryDate);
        Map<String, TradeRiskInfo> marginMap = tradeRiskInfoList.stream().collect(Collectors.toMap(TradeRiskInfo::getId, Function.identity()));
        //获取系统自定义结构报送信息
        List<StructureInfoVO> structureInfoVOList = structureClient.getStructureInfoList();
        Map<String, String> structureMap = structureInfoVOList.stream().collect(Collectors.toMap(StructureInfoVO::getStructureName, StructureInfoVO::getSubmittedStructureName));
        String mainName = systemConfigUtil.getMainName();
        String mainLicenseCode = systemConfigUtil.getMainLicenseCode();
        List<EquityTradeDailyVO> equityTradeDailyVOList = new ArrayList<>();
        tradeMngByDailyVOList.forEach(item -> {
            EquityTradeDailyVO equityTradeDailyVO = new EquityTradeDailyVO();
            equityTradeDailyVO.setMainName(mainName);
            equityTradeDailyVO.setMainLicenseCode(mainLicenseCode);
            ClientVO clientVO=  clientVOMap.get(item.getClientId());
            BussinessException.E_300100.assertNotNull(clientVO,"客户ID:"+item.getClientId()+"客户信息不存在");
            //交易对手方信息
            equityTradeDailyVO.setClientName(clientVO.getName());
            equityTradeDailyVO.setClientLicenseCode(clientVO.getLicenseCode());
            //协议信息
            equityTradeDailyVO.setProtocolType(clientVO.getProtocolSignVersion());
            equityTradeDailyVO.setProtocolDate(clientVO.getProtocolSignDate());
            //授信信息
            equityTradeDailyVO.setIsHaveCredit(grantCreditMap.getOrDefault(item.getClientId(), BigDecimal.ZERO).compareTo(BigDecimal.ZERO) != 0 ? "Y" : "N");
            equityTradeDailyVO.setCredit(grantCreditMap.getOrDefault(item.getClientId(), BigDecimal.ZERO));
            //保证金信息
            if (OptionTypeEnum.getSnowBall().contains(item.getOptionType())) {
                //雪球的直接使用保证金占用
                equityTradeDailyVO.setInitMargin(item.getUseMargin());
            } else {
                //非雪球的使用初始保证金单价乘成交数量
                equityTradeDailyVO.setInitMargin(item.getInitMargin().multiply(item.getTradeVolume()));
            }
            //维持保证金
            equityTradeDailyVO.setMargin(marginMap.getOrDefault(item.getTradeCode(),new TradeRiskInfo()).getMargin());
            //操作类型
            if (item.getCloseDate() == null) {
                equityTradeDailyVO.setOperationType("NT");
            } else {
                switch (item.getTradeState()) {
                    case partClosed:
                        equityTradeDailyVO.setOperationType("PU");
                        break;
                    case closed:
                    case expired:
                        equityTradeDailyVO.setOperationType("FU");
                        break;
                    case knockoutTerminate:
                        equityTradeDailyVO.setOperationType("ED");
                        break;
                    default:
                        equityTradeDailyVO.setOperationType("OTC_BUG");
                        break;
                }
            }
            //交易确认书编码
            if (item.getSettlementConfirmCode() != null) {
                equityTradeDailyVO.setTradeConfirmCode(item.getSettlementConfirmCode());
                List<Integer> closeIdList = settlementConfirmMap.get(item.getSettlementConfirmCode());
                if (closeIdList != null && closeIdList.size() > 1) {
                    equityTradeDailyVO.setConfirmId(closeIdList.indexOf(item.getCloseId()));
                }
            } else {
                equityTradeDailyVO.setTradeConfirmCode(item.getTradeConfirmCode());
                List<String> tradeCodeList = tradeConfirmCodeMap.get(item.getTradeConfirmCode());
                if (tradeCodeList != null && tradeCodeList.size() > 1) {
                    equityTradeDailyVO.setConfirmId(tradeCodeList.indexOf(item.getTradeCode()));
                }
            }
            //时间信息
            equityTradeDailyVO.setConfirmDate(item.getCloseDate() != null ? item.getCloseDate() : item.getTradeDate());
            equityTradeDailyVO.setTradeDate(item.getTradeDate());
            equityTradeDailyVO.setMaturityDate(item.getMaturityDate());
            if (TradeStateEnum.getCloseStateList().contains(item.getTradeState())) {
                equityTradeDailyVO.setCloseDate(item.getCloseDate());
            }
            //产品信息及标的物信息
            equityTradeDailyVO.setBuyOrSell(item.getBuyOrSell() == BuyOrSellEnum.buy ? "S" : "B");
            //工具类型
            equityTradeDailyVO.setToolType(OptionTypeEnum.AIForwardPricer == item.getOptionType() ? "FW" : "OP");
            //权益的报送行权方式目前暂时都用欧式
            equityTradeDailyVO.setExerciseType("EU");
            //期权权利类型
            if (item.getCallOrPut() != null) {
                equityTradeDailyVO.setOptionRightsType(item.getCallOrPut() == CallOrPutEnum.call ? "2" : "1");
            }
            if (OptionTypeEnum.getDailyPut().contains(item.getOptionType())) {
                equityTradeDailyVO.setOptionRightsType("1");
            }
            if (OptionTypeEnum.getDailyCall().contains(item.getOptionType())) {
                equityTradeDailyVO.setOptionRightsType("2");
            }
            if (OptionTypeEnum.getChooser().contains(item.getOptionType())) {
                equityTradeDailyVO.setOptionRightsType("9");
            }
            //产品结构
            if (item.getOptionCombType() != null) {
                equityTradeDailyVO.setStructureType(item.getOptionCombType().getKey());
            } else {
                if (item.getOptionType() != OptionTypeEnum.AIForwardPricer) {
                    if (item.getOptionType() != OptionTypeEnum.AICustomPricer) {
                        equityTradeDailyVO.setStructureType(item.getOptionType().getKey());
                    } else {
                        equityTradeDailyVO.setStructureType(structureMap.get(item.getStructureType()));
                    }
                }
            }

            //合约信息
            UnderlyingManagerVO underlyingManagerVO = underlyingManagerVOMap.get(item.getUnderlyingCode());
            //资产类型
            equityTradeDailyVO.setAssetType(assetTypeMap.get(underlyingManagerVO.getUnderlyingAssetType()));
            //标的资产类型
            equityTradeDailyVO.setUnderlyingAssetType(underlyingManagerVO.getUnderlyingAssetType());
            //标的资产品种
            equityTradeDailyVO.setVarietyCode(underlyingManagerVO.getVarietyCode());
            //标的资产对应合约
            equityTradeDailyVO.setExchangeUnderlyingCode(underlyingManagerVO.getExchangeUnderlyingCode());
            if (underlyingManagerVO.getExchangeUnderlyingCode().contains(".SH")) {
                equityTradeDailyVO.setVarietyCode(underlyingManagerVO.getExchangeUnderlyingCode());
            }
            //标的资产交易场所
            equityTradeDailyVO.setExchange(exchangeMap.getOrDefault(underlyingManagerVO.getExchange(), underlyingManagerVO.getExchange()));
            //总名义数量
            equityTradeDailyVO.setTotalVolume(item.getTradeVolume());
            //交易名义数量
            equityTradeDailyVO.setTradeVolume(item.getCloseVolume() != null ? item.getCloseVolume() : item.getTradeVolume());
            //数量单位
            equityTradeDailyVO.setPoint(underlyingManagerVO.getUnit());
            //参与率
            equityTradeDailyVO.setParticipationRate("1");
            //保本雪球为Y其它均为N
            equityTradeDailyVO.setIsAnnualizedOption(OptionTypeEnum.getChooser().contains(item.getOptionType()) ? "Y" : "N");
            //标的资产进场价格
            equityTradeDailyVO.setEntryPrice(item.getEntryPrice());
            //执行价格
            equityTradeDailyVO.setStrike(item.getStrike());
            //价格符号
            equityTradeDailyVO.setPriceSymbol("1");
            //计价货币
            equityTradeDailyVO.setInvoicingCurrency("CNY");
            //总名义金额
            equityTradeDailyVO.setNotionalPrincipal(item.getNotionalPrincipal());
            //交易名义金额
            equityTradeDailyVO.setTradeNotionalPrincipal(item.getCloseNotionalPrincipal() != null ? item.getCloseNotionalPrincipal() : item.getNotionalPrincipal());
            //期权费金额
            equityTradeDailyVO.setTradeAmount(item.getCloseTotalAmount() != null ? item.getCloseTotalAmount() : item.getTotalAmount());
            //结算信息
            if (item.getCloseDate() != null) {
                //结算方式
                equityTradeDailyVO.setSettlementMethod("C");
                //最后结算日
                if (TradeStateEnum.getCloseStateList().contains(item.getTradeState())) {
                    equityTradeDailyVO.setLastSettlementDate(item.getCloseDate());
                }
                //结算价确认方式
                equityTradeDailyVO.setSettlementConfirmType("9");
                //商品参考价格
                equityTradeDailyVO.setUnderlyingPrice(item.getCloseEntryPrice());
            }
            //风险对冲
            equityTradeDailyVO.setDeltaCash(item.getCloseDelta() != null ?
                    item.getCloseDelta().multiply(item.getCloseEntryPrice()).multiply(BigDecimal.valueOf(underlyingManagerVO.getContractSize())) :
                    item.getDelta().multiply(item.getEntryPrice()).multiply(BigDecimal.valueOf(underlyingManagerVO.getContractSize())));
            equityTradeDailyVOList.add(equityTradeDailyVO);
        });
        return equityTradeDailyVOList;
    }

}
