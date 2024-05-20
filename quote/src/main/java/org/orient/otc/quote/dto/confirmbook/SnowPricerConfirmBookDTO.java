package org.orient.otc.quote.dto.confirmbook;

import com.deepoove.poi.data.DocxRenderData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.quote.entity.TradeObsDate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 香草交易确认书对象
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SnowPricerConfirmBookDTO implements Serializable {


    /**
     * 客户ID
     */
    private Integer clientId;

    /**
     * 客户名称
     */
    private String clientName;


    private OptionTypeEnum optionType;

    /**
     * 交易编号
     */
    private String tradeCode;

    /**
     * 客户方向
     */
    private BuyOrSellEnum buyOrSell;
    /**
     * 产品买方
     */
    private String productBuyName;

    /**
     * 交易日期
     */
    private LocalDate tradeDate;

    /**
     * 交易日期
     */
    private String tradeDateStr;

    /**
     * 产品开始日期
     */
    private LocalDate productStartDate;

    /**
     * 产品开始日期
     */
    private String productStartDateStr;

    /**
     * 交易到期日
     */
    private LocalDate maturityDate;
    /**
     * 交易到期日
     */
    private String maturityDateStr;
    /**
     * 标的合约
     */
    private String underlyingCode;

    private String underlyingCodeByExchange;

    /**
     * 资产类型
     */
    private String assetTyp;

    /**
     * 名义本金
     */
    private BigDecimal notionalPrincipal;
    /**
     * 名义本金String
     */
    private String notionalPrincipalString;

    /**
     * 入场价格
     */
    private BigDecimal entryPrice;

    /**
     * 入场价格str
     */
    private String entryPriceString;

    /**
     * 参与率
     */
    private BigDecimal participationRate;

    /**
     * 参与率
     */
    private String participationRateString;

    /**
     * 敲入价格
     */
    private String knockInBarrierValueString;

    /**
     * 敲入价格百分比
     */
    private String knockInBarrierValueRate;

    /**
     * 执行价格一
     */
    private String strikeOnceKnockedInValueString;

    /**
     * 执行价格一百分比
     */
    private String strikeOnceKnockedInValueRate;

    /**
     * 执行价格二
     */
    private String strike2OnceKnockedInValueString;

    /**
     * 执行价格二百分比
     */
    private String strike2OnceKnockedInValueRate;

    /**
     * 红利票息
     */
    private String bonusRateStructValueString;

    /**
     * 到期年化收益率
     */
    private String yearBonusRateStructValueString;

    /**
     * 到期绝对收益率
     */
    private String absBonusRateStructValueString;

    /**
     * 年化期权费率
     */
    private String yearReturnRateStructValueString;

    /**
     * 绝对期权费率
     */
    private String absReturnRateStructValueString;

    /**
     * 保证金占用
     */
    private String useMarginString;

    /**
     * 户名
     */
    private String bankAccountName;

    /**
     * 开户行
     */
    private String bankOpenBank;

    /**
     * 账户
     */
    private String bankAccount;

    /**
     * 大额行号
     */
    private String largeBankAccount;

    /**
     * 联系人
     */
    private String clientContactName;


    /**
     * 联系地址
     */
    private String clientContactAddress;


    /**
     * 电子邮箱
     */
    private String clientContactEmail;


    /**
     * 联系电话
     */
    private String clientContactPhone;


    /**
     * 传真
     */
    private String clientContactFax;

    /**
     * 日期
     */
    private String tradeDateEndStr;
    /**
     * 敲出信息
     */
    private List<TradeObsDate> obsDateList;

    private DocxRenderData experience;

    @Data
    public static class KnockOutInfo {
        /**
         * 序号
         */
        private Integer index;
        /**
         * 敲出观察日
         */
        private String knockOutDateStr;
        /**
         * 敲出价格
         */
        private String knockOutValueString;
        /**
         * 敲出价格百分比
         */
        private String knockOutRateString;

        /**
         * 敲出收益率
         */
        private String rebateRateString;
    }

}
