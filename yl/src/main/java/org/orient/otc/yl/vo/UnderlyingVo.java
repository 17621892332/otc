package org.orient.otc.yl.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dzrh
 */
@Data
public class UnderlyingVo {
    /**
     * 合约代码
     */
    private String code;
    /**
     * 合约名称
     */
    private String name;
    /**
     * 交易所代码
     */
    private String exchange;
    /**
     * 产品种类
     */
    private String productClass;
    /**
     * 场内期权的标的代码
     */
    private String underlyingCode;
    /**
     * 上市日期,格式: yyyy-MM-dd
     */
    private LocalDate createDate;
    /**
     * 到期日期,格式: yyyy-MM-dd
     */
    private LocalDate expireDate;
    /**
     * 合约乘数
     */
    private Integer multiple;
    /**
     * 行权价(场内期权适用)
     */
    private BigDecimal strike;
    /**
     * 最小变动价位
     */
    private BigDecimal priceTick;
    /**
     * 多头保证金率,标的数据上没有则取品种上的
     */
    private BigDecimal longMarginRatio;
    /**
     * 空投保证金率,标的数据上没有则取品种上的
     */
    private BigDecimal shortMarginRatio;
    /**
     * 交易产品类型
     */
    private int contractType;
    /**
     * 期权类型
     */

    private int optionType;
    /**
     * 波动率动幅度,标数据上没有则取品种上的，场内期权不返回此字段
     */

    private BigDecimal volatlitySpa;
    /**
     * 涨跌停幅度标的数据上没有则取品种上的，场内期权不返回此字段
     */
    private BigDecimal upDownLimit;
    /**
     * 涨跌停幅度是否绝对值方式，场内期权不返回此字段
     */
    private boolean isUpdownLimitFixed;

}
