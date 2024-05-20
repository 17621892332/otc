package org.orient.otc.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xp
 */
@Data
public class TradeDataDTO implements Serializable {
    /**
     * 结构类型
     */
    private String optType;

    /**
     * 标的代码
     */
    private String code;
    /**
     * 存续数量
     */
    private BigDecimal quantity;

    /**
     * 存续名义本金
     */
    private BigDecimal nominal;
    /**
     * 买卖方向
     */
    private Integer sign;
    /**
     * 合约乘数
     */
    private Integer multiplier;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime todayDate;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    private String flag;

    private BigDecimal r;

    /**
     * 分红率
     */
    private BigDecimal q;

    private String tradingCode;
    /**
     * 分红率
     */
    private BigDecimal dividend;

    @JsonProperty(value = "cVS")
    @JSONField(name = "cVS")
    private List<VolatityDataDTO> cVS;

    private BigDecimal constSgm;

    private BigDecimal startFut;

    /**
     * 最新标的价格
     */
    private BigDecimal currentFut;

    /**
     * 执行价格
     */
    private BigDecimal strike;



    /**
     * 执行价斜坡
     */
    private BigDecimal strikeRamp;

    /**
     * 敲出价格
     */
    private BigDecimal barrier;

    /**
     * 障碍价斜坡
     */
    private BigDecimal barrierRamp;

    /**
     * 敲出赔付
     */
    private BigDecimal rebate;
    /**
     * 观察日列表
     */
    private List<TradeObsDateDTO> obsDateList;

    /**
     * 单位固定赔付
     */
    private BigDecimal fixedPayment;

    /**
     * 每日数量
     */
    private BigDecimal basicQuantity;
    /**
     * 结算方式：1（现金）/ 0（头寸）
     */
    private Integer isCashsettle;
    /**
     * 多倍系数
     */
    private BigDecimal leverage;
    /**
     * 每日杠杆倍数
     */
    private BigDecimal dailyLeverage;
    /**
     * 到期杠杆倍数
     */
    private BigDecimal expiryLeverage;

    /**
     * 上执行价格
     */
    private BigDecimal ku;

    /**
     * 下执行价格
     */
    private BigDecimal kd;

    private Boolean isKnockedin;

    private BigDecimal ustrikeRamp;

    private BigDecimal dstrikeRamp;

    private BigDecimal bonus;

    private Boolean isBonusAn;

    private BigDecimal returnRate;

    private Boolean isReturnAn;

}
