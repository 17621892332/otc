package org.orient.otc.quote.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.*;
import org.orient.otc.common.core.util.FieldAlias;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class TradeMng extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 交易编号
     */
    @FieldAlias(value = "交易编号")
    private String tradeCode;

    /**
     * 组合编号
     */
    @FieldAlias(value = "组合编号")
    private String combCode;

    /**
     * 关联交易编号
     */
    @FieldAlias(value = "关联交易编号")
    private String relevanceTradeCode;
    /**
     * 顺序
     */
    @ApiModelProperty(value = "顺序")
    private Integer sort;

    /**
     * 交易员id
     */
    @FieldAlias(value = "交易员id")
    private Integer traderId;

    /**
     * 簿记ID
     */
    @FieldAlias(value = "簿记ID")
    private Integer assetId;

    /**
     * 客户ID
     */
    @FieldAlias(value = "客户ID")
    private Integer clientId;

    /**
     * 交易日期
     */
    @FieldAlias(value = "交易日期")
    private LocalDate tradeDate;

    /**
     * 产品开始日期
     */
    @FieldAlias(value = "产品开始日期")
    private LocalDate productStartDate;

    /**
     * 到期日
     */
    @FieldAlias(value = "到期日")
    private LocalDate maturityDate;

    /**
     * 标的合约
     */
    @FieldAlias(value = "标的合约")
    private String underlyingCode;

    /**
     * 入场价格
     */
    @FieldAlias(value = "入场价格")
    private BigDecimal entryPrice;

    /**
     * 期权组合类型
     */
    @FieldAlias(value = "期权组合类型")
    private OptionCombTypeEnum optionCombType;

    /**
     * 期权类型
     */
    @FieldAlias(value = "期权类型")
    private OptionTypeEnum optionType;


    /**
     * 行权方式
     */
    @FieldAlias(value = "行权方式")
    private ExerciseTypeEnum exerciseType;
    /**
     * 保底封顶
     */
    @FieldAlias(value = "保底封顶")
    private CeilFloorEnum ceilFloor;
    /**
     * 行权价格
     */
    @FieldAlias(value = "行权价格")
    private BigDecimal strike;

    /**
     * 行权价格2
     */
    @FieldAlias(value = "行权价格2")
    private BigDecimal strike2;
    /**
     * 增强价格
     */
    @FieldAlias(value = "增强价格")
    private BigDecimal enhancedStrike;

    /**
     * 折扣率
     */
    @FieldAlias(value = "折扣率")
    private BigDecimal discountRate;
    /**
     * 客户方向
     */
    @FieldAlias(value = "客户方向")
    private BuyOrSellEnum buyOrSell;


    /**
     * 看涨看跌
     */
    @FieldAlias(value = "看涨看跌")
    private CallOrPutEnum callOrPut;

    /**
     * 期权价格
     */
    @FieldAlias(value = "期权价格")
    private BigDecimal optionPremium;

    /**
     * 期权%单价
     */
    @FieldAlias(value = "期权%单价")
    private BigDecimal optionPremiumPercent;

    /**
     * 期权费率是否年化
     */
    @FieldAlias(value = "期权费率是否年化")
    private  Boolean optionPremiumPercentAnnulized;

    /**
     * 保证金￥单价
     */
    @FieldAlias(value = "保证金￥单价")
    private BigDecimal margin;


    /**
     * 交易波动率
     */
    @FieldAlias(value = "交易波动率")
    private BigDecimal tradeVol;

    /**
     * 计算波动率
     */
    @FieldAlias(value = "计算波动率")
    private BigDecimal riskVol;

    /**
     * mid波动率
     */
    @FieldAlias(value = "mid波动率")
    private BigDecimal midVol;

    /**
     * mid分红率
     */
    @FieldAlias(value = "mid分红率")
    private BigDecimal midDividendYield;

    /**
     * 成交分红率
     */
    @FieldAlias(value = "成交分红率")
    private BigDecimal tradeDividendYield;


    /**
     * 无风险利率
     */
    @FieldAlias(value = "无风险利率")
    private BigDecimal riskFreeInterestRate;
    /**
     * 成交数量
     */
    @FieldAlias(value = "成交数量")
    private BigDecimal tradeVolume;

    /**
     * 成交金额
     */
    @FieldAlias(value = "成交金额")
    private BigDecimal totalAmount;

    /**
     * 名义本金
     */
    @FieldAlias(value = "名义本金")
    private BigDecimal notionalPrincipal;
    /**
     * 存续名义本金
     */
    @FieldAlias(value = "存续名义本金")
    private BigDecimal availableNotionalPrincipal;

    /**
     * 存续数量
     */
    @FieldAlias(value = "存续数量")
    private BigDecimal availableVolume;

    /**
     * 累计盈亏
     */
    @FieldAlias(value = "累计盈亏")
    private BigDecimal totalProfitLoss;

    /**
     * 平仓日期
     */
    @FieldAlias(value = "平仓日期")
    private LocalDate closeDate;

    @FieldAlias(value = "day1PnL")
    private BigDecimal day1PnL;

    /**
     * 结算方式
     */
    @FieldAlias(value = "结算方式")
    private SettleTypeEnum settleType;

    /**
     * 起始观察日期
     */
    @FieldAlias(value = "起始观察日期")
    private LocalDate startObsDate;

    /**
     * 采价次数
     */
    @FieldAlias(value = "采价次数")
    private Integer obsNumber;
    /**
     * 每日数量
     */
    @FieldAlias(value = "每日数量")
    private BigDecimal basicQuantity;
    /**
     * 杠杆系数
     */
    @FieldAlias(value = "杠杆系数")
    private BigDecimal leverage;

    /**
     * 单位固定赔付
     */
    @FieldAlias(value = "单位固定赔付")
    private BigDecimal fixedPayment;
    /**
     * 敲出价格
     */
    @FieldAlias(value = "敲出价格")
    private BigDecimal barrier;

    /**
     * 执行价斜坡
     */
    @FieldAlias(value = "执行价斜坡")
    private BigDecimal strikeRamp;
    /**
     * 障碍价斜坡
     */
    @FieldAlias(value = "障碍价斜坡")
    private BigDecimal barrierRamp;

    /**
     * TTM
     */
    @FieldAlias(value = "TTM")
    private BigDecimal ttm;

    /**
     * 工作日
     */
    @FieldAlias(value = "工作日")
    private Integer workday;

    /**
     * 交易日
     */
    @FieldAlias(value = "交易日")
    private Integer tradingDay;

    /**
     * 公共假日
     */
    @FieldAlias(value = "公共假日")
    private Integer bankHoliday;

    /**
     * 交易状态
     */
    @FieldAlias(value = "交易状态")
    private TradeStateEnum tradeState;

    @FieldAlias(value = "pv")
    private BigDecimal pv;

    @FieldAlias(value = "delta")
    private BigDecimal delta;

    @FieldAlias(value = "gamma")
    private BigDecimal gamma;

    @FieldAlias(value = "vega")
    private BigDecimal vega;

    @FieldAlias(value = "theta")
    private BigDecimal theta;

    @FieldAlias(value = "rho")
    private BigDecimal rho;

    @FieldAlias(value = "dividendRho")
    private BigDecimal dividendRho;
    /**
     * 是否同步至镒链 0-未同步 1已同步 2同步失败
     */
    @FieldAlias(value = "是否同步")
    private Integer isSync;

    /**
     * 同步消息
     */
    @FieldAlias(value = "同步消息")
    private String syncMsg;

    /**
     * 风险预警信息
     */
    @FieldAlias(value = "风险预警信息")
    private String warningMsg;


    /**
     * 观望日期列表
     */
    @TableField(exist = false)
    List<TradeObsDate> tradeObsDateList;

    /**
     * 用于前端生成简讯
     */
    @TableField(exist = false)
    private String tradeKey;

    /***
     * 敲出赔付
     */
    @FieldAlias(value = "敲出赔付")
    private BigDecimal knockoutRebate;

    /**
     * 到期倍数
     */
    @FieldAlias(value = "到期倍数")
    private BigDecimal expireMultiple;

    /**
     * 自定义结构类型
     */
    @FieldAlias(value = "自定义结构类型")
    private String structureType;

    /**
     * 自定义期权信息
     */
    @FieldAlias(value = "自定义结构信息")
    private String extendInfo;
}
