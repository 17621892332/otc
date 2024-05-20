package org.orient.otc.quote.util;

import cn.hutool.core.date.DatePattern;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.Includes;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.quote.config.TemplateConfig;
import org.orient.otc.quote.dto.confirmbook.AccPricerConfirmBookDTO;
import org.orient.otc.quote.dto.confirmbook.AsianPricerConfirmBookDTO;
import org.orient.otc.quote.dto.confirmbook.SnowPricerConfirmBookDTO;
import org.orient.otc.quote.dto.confirmbook.VanillaPricerConfirmBookDTO;
import org.orient.otc.quote.entity.TradeObsDate;
import org.orient.otc.quote.entity.TradeSnowballOption;
import org.orient.otc.quote.exeption.BussinessException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 构建交易确认书
 * @author dzrh
 */
@Component
public class BuildTradeConfirmBook {
    @Resource
    TemplateConfig templateConfig;

    @Resource
    WordUtil wordUtil;

//    /**
//     * 雪球模板转换 适用于模板中有表格套表格,并且第二层的表格数据是动态渲染的
//     * 获取交易模板并生成确认书
//     * @param vo 用于生成确认书时  , 设置名称组成的要素
//     * @param wordTemplateVO 用于填充模板的数据
//     * @return 返回VO
//     * @throws Exception 异常
//     */
//    public TradeConfirmBookDownloadVO getTemplateAndBuildIncludeTable(TradeConfirmBookDTO vo, WordTemplateVO wordTemplateVO) throws Exception {
//        // 根据期权类型,获取模板路径及相关参数
//        BuildConfirmBookInfoVO buildConfirmBookInfoVO = tradeConfirmBookUtil.getBuildTradeConfirmBookInfoVO(vo);
//        // 生成确认书
//        String outPath = tradeConfirmBookUtil.exportConfirmBookIncludeTable(buildConfirmBookInfoVO.getPath(),wordTemplateVO);
//        // 交易确认书名称 交易确认书+客户名称+交易编号+期权类型
//        String downloadName = "OT" + "-" + vo.getTradeCode() + "+" + vo.getTradeDate().format(formatter);
//        TradeConfirmBookDownloadVO tradeConfirmBookDownloadVO = new TradeConfirmBookDownloadVO();
//        tradeConfirmBookDownloadVO.setDownloadName(downloadName);
//        tradeConfirmBookDownloadVO.setPath(outPath);
//        tradeConfirmBookDownloadVO.setZipName("交易确认书");
//        return tradeConfirmBookDownloadVO;
//    }

    /**
     * 处理亚式期权(看涨看跌) , 增强亚式(看涨看跌) 一笔交易生成一个确认书
     * @param bookDTO 交易信息
     */
    public ByteArrayInputStream generateAsianPricer(AsianPricerConfirmBookDTO bookDTO) throws Exception {
        //亚式期权的权利金单价、权利金总额都取绝对值
        bookDTO.setOptionPremium(bookDTO.getOptionPremium().abs());
        bookDTO.setTotalAmount(bookDTO.getTotalAmount().abs());
        bookDTO.setTradeDateStr(bookDTO.getTradeDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setMaturityDateStr(bookDTO.getMaturityDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setStartObsDateStr(bookDTO.getStartObsDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setEndObsDateStr(bookDTO.getEndObsDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setTradeVolumeString(bookDTO.getTradeVolume() + " " + bookDTO.getQuoteUnit());
        bookDTO.setEntryPriceString(bookDTO.getEntryPrice() + " 元/" + bookDTO.getQuoteUnit());
        bookDTO.setEnhancedStrikeString(bookDTO.getEnhancedStrike()+ " 元/" + bookDTO.getQuoteUnit());
        bookDTO.setStrikeString(bookDTO.getStrike() + " 元/" + bookDTO.getQuoteUnit());
        bookDTO.setOptionPremiumString(bookDTO.getOptionPremium() + " 元/" + bookDTO.getQuoteUnit());
        bookDTO.setTotalAmountString(BigDecimalUtil.getThousandsString(bookDTO.getTotalAmount())+ " 元");
        bookDTO.setNotionalPrincipalString(BigDecimalUtil.getThousandsString(bookDTO.getNotionalPrincipal())+ " 元");
        if (bookDTO.getCallOrPut() == CallOrPutEnum.call) {
            bookDTO.setYieldStructure("到期收益 = 交易数量×Max[(结算价格-行权价格)，0)]；");
        } else {
            bookDTO.setYieldStructure("到期收益 = 交易数量×Max[(行权价格-结算价格)，0)]；");
        }
        Configure configure = Configure.createDefault();
        String templatePath;
        switch (bookDTO.getOptionType()) {
            case AIAsianPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"asian/"+ "AIAsianPricer.docx";
                break;
            case AIEnAsianPricer:
                if (bookDTO.getCallOrPut() == CallOrPutEnum.call) {
                    templatePath = templateConfig.getTradeConfirmBookPath() + "asian/"+"AIEnAsianPricerCall.docx";
                }else {
                    templatePath = templateConfig.getTradeConfirmBookPath() + "asian/"+"AIEnAsianPricerPut.docx";
                }
                break;
            default:
                BussinessException.E_300102.assertTrue(Boolean.TRUE, bookDTO.getTradeCode() + "错误的交易数据");
                templatePath = "error.docx";
                break;
        }
        return wordUtil.exportInputStream(templatePath , configure, bookDTO);
        }

    /**
     * 处理累计期权类型
     * 累计期权生成模板文件时 , 一个交易生成一个确认书文件
     * @param bookDTO    生成的确认书文件信息
     * @throws Exception 系统异常
     */
    public ByteArrayInputStream generateAccPricer(AccPricerConfirmBookDTO bookDTO) throws Exception {
        Configure configure = Configure.createDefault();
        String templatePath;
        //累计期权数据处理
        bookDTO.setTradeVolumeString(bookDTO.getTradeVolume() + " " + bookDTO.getQuoteUnit());
        bookDTO.setBasicQuantityString(bookDTO.getBasicQuantity() + " " + bookDTO.getQuoteUnit());
        bookDTO.setTotalAmountString(bookDTO.getTotalAmount()+ " 元");
        bookDTO.setTradeDateStr(bookDTO.getTradeDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setStartObsDateStr(bookDTO.getStartObsDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setEndObsDateStr(bookDTO.getEndObsDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setBarrierString(bookDTO.getBarrier().compareTo(BigDecimal.ZERO) > 0
                ? bookDTO.getBarrier() +" 元/"+bookDTO.getQuoteUnit() : "/");
        bookDTO.setStrikeString(bookDTO.getStrike() +" 元/"+bookDTO.getQuoteUnit());
        bookDTO.setEntryPriceString(bookDTO.getEntryPrice() +" 元/"+bookDTO.getQuoteUnit());
        bookDTO.setFixedPaymentString(bookDTO.getFixedPayment().compareTo(BigDecimal.ZERO) > 0
                ? bookDTO.getFixedPayment() +" 元/"+bookDTO.getQuoteUnit() : "/");
        bookDTO.setExpireMultipleString(bookDTO.getExpireMultiple()!=null?bookDTO.getExpireMultiple().toPlainString():"");
        bookDTO.setKnockoutRebateString(bookDTO.getKnockoutRebate().compareTo(BigDecimal.ZERO) > 0
                ? bookDTO.getKnockoutRebate() +" 元/"+bookDTO.getQuoteUnit() : "/");
        switch (bookDTO.getOptionType()) {
            case AICallAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AICallAccPricer.docx";
                break;
            case AIPutAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() + "acc/"+"AIPutAccPricer.docx";
                break;
            case AICallFixAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AICallFixAccPricer.docx";
                break;
            case AIPutFixAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AIPutFixAccPricer.docx";
                break;
            case AICallKOAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AICallKOAccPricer.docx";
                break;
            case AIPutKOAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AIPutKOAccPricer.docx";
                break;
            case AICallFixKOAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AICallFixKOAccPricer.docx";
                break;
            case AIPutFixKOAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AIPutFixKOAccPricer.docx";
                break;
            case AIEnCallKOAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AIEnCallKOAccPricer.docx";
                break;
            case AIEnPutKOAccPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() +"acc/"+ "AIEnPutKOAccPricer.docx";
                break;
            default:
                BussinessException.E_300102.assertTrue(Boolean.TRUE, bookDTO.getTradeCode() + "错误的交易数据");
                templatePath = "error.docx";
                break;
        }
        return wordUtil.exportInputStream(templatePath, configure, bookDTO);
    }


    /**
     * 处理香草期权类型
     * 香草期权生成模板文件时 , 同一个客户同一交易日生成一个交易确认书文件
     * @param list          交易信息
     */
    public ByteArrayInputStream generateVanillaAndForwardPricer(List<VanillaPricerConfirmBookDTO> list) throws Exception {
        DateTimeFormatter dateTimeFormatter = DatePattern.createFormatter("yyyy/MM/dd");
        for (VanillaPricerConfirmBookDTO bookDTO : list) {
            bookDTO.setTradeDateStr(bookDTO.getTradeDate().format(dateTimeFormatter));
            bookDTO.setMaturityDateStr(bookDTO.getMaturityDate().format(dateTimeFormatter));
            bookDTO.setDzBuyOrSellName(bookDTO.getBuyOrSell() == BuyOrSellEnum.buy ? "卖出" : "买入");
            bookDTO.setTradeVolumeString(bookDTO.getTradeVolume().toPlainString());
            bookDTO.setEntryPriceString(bookDTO.getEntryPrice().toPlainString());
            bookDTO.setStrikeString(bookDTO.getStrike().toPlainString());
            bookDTO.setOptionPremiumString(bookDTO.getOptionPremium().toPlainString());
            bookDTO.setTotalAmountString(BigDecimalUtil.getThousandsString(bookDTO.getTotalAmount()));
            bookDTO.setNotionalPrincipalString(BigDecimalUtil.getThousandsString(bookDTO.getNotionalPrincipal()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("experiences", list);
        map.put("clientName", list.get(0).getClientName());
        map.put("tradeDateEndStr", list.get(0).getTradeDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        Configure configure = Configure.createDefault();
        String templatePath;
        if (list.get(0).getOptionType() == OptionTypeEnum.AIVanillaPricer) {
            templatePath = templateConfig.getTradeConfirmBookPath() + "AIVanillaPricer.docx";
        } else {
            templatePath = templateConfig.getTradeConfirmBookPath() + "AIForwardPricer.docx";
        }
        return wordUtil.exportInputStream(templatePath, configure, map);
    }

    /**
     * 处理雪球期权类型 雪球期权生成模板文件时 , 一个交易生成一个确认书文件
     * @param bookDTO        交易信息
     * @param snowballOption 雪球特有属性
     * @throws Exception 系统异常
     */
    public ByteArrayInputStream generateSnowBallPricer(SnowPricerConfirmBookDTO bookDTO, TradeSnowballOption snowballOption) throws Exception {

        // 模板中待填充的数据集合
        bookDTO.setTradeDateStr(bookDTO.getTradeDate().format(DatePattern.NORM_DATE_FORMATTER));
        bookDTO.setTradeDateEndStr(bookDTO.getTradeDate().format(DatePattern.CHINESE_DATE_FORMATTER));
        bookDTO.setProductStartDateStr(bookDTO.getProductStartDate().format(DatePattern.NORM_DATE_FORMATTER));
        bookDTO.setMaturityDateStr(bookDTO.getMaturityDate().format(DatePattern.NORM_DATE_FORMATTER));
        bookDTO.setNotionalPrincipalString(BigDecimalUtil.getThousandsString(bookDTO.getNotionalPrincipal()));
        bookDTO.setEntryPriceString(bookDTO.getEntryPrice().toPlainString());
        bookDTO.setParticipationRateString("100%");
        //敲入要素处理
        BigDecimal entryPrice = bookDTO.getEntryPrice();
        // 设置敲入价格
        if (snowballOption.getKnockinBarrierValue() != null && snowballOption.getKnockinBarrierRelative() != null) {
            // 相对
            if (snowballOption.getKnockinBarrierRelative()) {
                // 敲入价格
                bookDTO.setKnockInBarrierValueString(BigDecimalUtil.getBigDecimalString(entryPrice.multiply(BigDecimalUtil.percentageToBigDecimal(snowballOption.getKnockinBarrierValue())), 2));
                // 敲入价格百分比
                bookDTO.setKnockInBarrierValueRate(BigDecimalUtil.getBigDecimalString(snowballOption.getKnockinBarrierValue(), 2) + "%");
            } else {
                // 先保留6位小数(不影响小数位的第五位),以防除不尽, 再乘以100, 取百分比, 最后再保留2位小数
                BigDecimal knockInBarrierValueRate = snowballOption.getKnockinBarrierValue().divide(entryPrice, 6, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                bookDTO.setKnockInBarrierValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getKnockinBarrierValue(), 2));
                bookDTO.setKnockInBarrierValueRate(knockInBarrierValueRate + "%");
            }
        }
        // 设置执行价格一
        if (snowballOption.getStrikeOnceKnockedinRelative() != null && snowballOption.getStrikeOnceKnockedinValue() != null) {
            // 相对 , 取执行价格(百分数)*入场价格
            if (snowballOption.getStrikeOnceKnockedinRelative()) {
                // 行权价格一
                bookDTO.setStrikeOnceKnockedInValueString(BigDecimalUtil.getBigDecimalString(entryPrice.multiply(BigDecimalUtil.percentageToBigDecimal(snowballOption.getStrikeOnceKnockedinValue())), 2));
                // 行权价格一百分比
                bookDTO.setStrikeOnceKnockedInValueRate(BigDecimalUtil.getBigDecimalString(snowballOption.getStrikeOnceKnockedinValue(), 2) + "%");
            } else { // 非相对 ,比率=敲入价格/入场价格 四舍五入保留两位小数
                // 先保留6位小数(不影响小数位的第五位),以防除不尽, 再乘以100, 取百分比, 最后再保留2位小数
                BigDecimal strikeOnceKnockedinValueRate = snowballOption.getStrikeOnceKnockedinValue().divide(entryPrice, 6, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                bookDTO.setStrikeOnceKnockedInValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getStrikeOnceKnockedinValue(), 2));
                bookDTO.setStrikeOnceKnockedInValueRate(strikeOnceKnockedinValueRate + "%");
            }
        }
        // 设置执行价格二
        if (snowballOption.getStrike2OnceKnockedinRelative() != null && snowballOption.getStrike2OnceKnockedinValue() != null) {
            // 相对 , 取执行价格(百分数)*入场价格
            if (snowballOption.getStrike2OnceKnockedinRelative()) {
                // 行权价格一
                bookDTO.setStrike2OnceKnockedInValueString(BigDecimalUtil.getBigDecimalString(entryPrice.multiply(BigDecimalUtil.percentageToBigDecimal(snowballOption.getStrike2OnceKnockedinValue())), 2));
                // 行权价格一百分比
                bookDTO.setStrike2OnceKnockedInValueRate(BigDecimalUtil.getBigDecimalString(snowballOption.getStrike2OnceKnockedinValue(), 2) + "%");
            } else { // 非相对 ,比率=敲入价格/入场价格 四舍五入保留两位小数
                // 先保留6位小数(不影响小数位的第五位),以防除不尽, 再乘以100, 取百分比, 最后再保留2位小数
                BigDecimal strike2OnceKnockedinValueRate = snowballOption.getStrike2OnceKnockedinValue().divide(entryPrice, 6, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                bookDTO.setStrike2OnceKnockedInValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getStrike2OnceKnockedinValue(), 2));
                bookDTO.setStrike2OnceKnockedInValueRate(strike2OnceKnockedinValueRate + "%");
            }
        }
        // 未敲入未敲出有收益率
        bookDTO.setBonusRateStructValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getBonusRateStructValue(), 2) + "%");
        //保证金占用
        bookDTO.setUseMarginString(BigDecimalUtil.getThousandsString(snowballOption.getUseMargin()));
                    /*
                    红利票息是否年化    是   到期年化收益率=红利票息, 到期绝对收益率=0.00%
                    红利票息是否年化    否   到期年化收益率=0.00%  , 到期绝对收益率=红利票息
                     */
        if (snowballOption.getBonusRateAnnulized() != null && snowballOption.getBonusRateAnnulized()) {
            // 到期年化收益率
            bookDTO.setAbsBonusRateStructValueString("0.00%");
            // 到期绝对收益率
            bookDTO.setYearBonusRateStructValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getBonusRateStructValue(), 2) + "%");
        } else {
            // 到期年化收益率
            bookDTO.setAbsBonusRateStructValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getBonusRateStructValue(), 2) + "%");
            // 到期绝对收益率
            bookDTO.setYearBonusRateStructValueString("0.00%");
        }

        /*
        返息率是否年化    是    年化期权费率=返息率, 绝对期权费率=0.00%
        返息率是否年化    否    年化期权费率=0.00%, 绝对期权费率=返息率
         */
        if (snowballOption.getReturnRateAnnulized() != null && snowballOption.getReturnRateAnnulized()) {
            // 年化期权费率
            bookDTO.setYearReturnRateStructValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getReturnRateStructValue(), 2) + "%");
            // 绝对期权费率
            bookDTO.setAbsReturnRateStructValueString("0.00%");
        } else {
            // 年化期权费率
            bookDTO.setYearReturnRateStructValueString("0.00%");
            // 绝对期权费率
            bookDTO.setAbsReturnRateStructValueString(BigDecimalUtil.getBigDecimalString(snowballOption.getReturnRateStructValue(), 2) + "%");
        }

        // 敲出要素列表
        List<SnowPricerConfirmBookDTO.KnockOutInfo> knockOutInfoList = new ArrayList<>();
        // 设置敲出价格
        for (int index = 0; index < bookDTO.getObsDateList().size(); index++) {
            TradeObsDate item = bookDTO.getObsDateList().get(index);
            // 敲出要素
            SnowPricerConfirmBookDTO.KnockOutInfo knockOutInfo = new SnowPricerConfirmBookDTO.KnockOutInfo();
            knockOutInfo.setIndex(index + 1);
            if (item.getBarrierRelative()) {
                // 相对
                BigDecimal barrier = entryPrice.multiply(BigDecimalUtil.percentageToBigDecimal(item.getBarrier()));
                // 敲出价格
                knockOutInfo.setKnockOutValueString(BigDecimalUtil.getBigDecimalString(barrier, 2));
                // 敲出价格百分比
                knockOutInfo.setKnockOutRateString(BigDecimalUtil.getBigDecimalString(item.getBarrier(), 2) + "%");
            } else {
                BigDecimal barrierRate = item.getBarrier().divide(entryPrice, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
                // 敲出价格
                knockOutInfo.setKnockOutValueString(BigDecimalUtil.getBigDecimalString(item.getBarrier(), 2));
                // 敲出价格百分比
                knockOutInfo.setKnockOutRateString(BigDecimalUtil.getBigDecimalString(barrierRate, 2) + "%");
            }
            // 敲出收益率
            knockOutInfo.setRebateRateString(BigDecimalUtil.getBigDecimalString(item.getRebateRate(), 2) + "%");
            //观察日
            knockOutInfo.setKnockOutDateStr(item.getObsDate().format(DatePattern.NORM_DATE_FORMATTER));
            knockOutInfoList.add(knockOutInfo);
        }
        String templatePath;
        if (Objects.equals(bookDTO.getAssetTyp(), AssetTypeEnum.EQ.name())) {
            templatePath = "equity";
        } else if (Objects.equals(bookDTO.getAssetTyp(), AssetTypeEnum.CO.name())) {
            templatePath = "commodity";
        } else {
            BussinessException.E_300100.assertTrue(Boolean.FALSE, bookDTO.getAssetTyp() + "暂无对应模板");
            return new ByteArrayInputStream(new byte[0]);
        }
        switch (bookDTO.getOptionType()) {
            case AIBreakEvenSnowBallCallPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() + "snow/" + templatePath + "AIBreakEvenSnowBallCallPricer.docx";
                break;
            case AIBreakEvenSnowBallPutPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() + "snow/" + templatePath + "AIBreakEvenSnowBallPutPricer.docx";
                break;
            case AISnowBallCallPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() + "snow/" + templatePath + "AISnowBallCallPricer.docx";
                break;
            case AISnowBallPutPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() + "snow/" + templatePath + "AISnowBallPutPricer.docx";
                break;
            case AILimitLossesSnowBallCallPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() + "snow/" + templatePath + "AILimitLossesSnowBallCallPricer.docx";
                break;
            case AILimitLossesSnowBallPutPricer:
                templatePath = templateConfig.getTradeConfirmBookPath() + "snow/" + templatePath + "AILimitLossesSnowBallPutPricer.docx";
                break;
            default:
                BussinessException.E_300100.assertTrue(Boolean.FALSE, bookDTO.getOptionType() + "暂无对应模板");
        }
        Configure configure = Configure.createDefault();
        //构建观察日详情
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        Configure obsConfig = Configure.builder().bind("obs", policy).build();
        bookDTO.setExperience(Includes.ofStream(wordUtil.exportInputStream(templateConfig.getTradeConfirmBookPath() + "snow/knockOut.docx"
                , obsConfig, new HashMap<String, Object>() {{
                    put("obs", knockOutInfoList);
                }})).create());
        return wordUtil.exportInputStream(templatePath, configure, bookDTO);

    }
}
