package org.orient.otc.quote.vo.settlementreport;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资金监控
 * @author dzrh
 */
@Data
public class CapitalMonitorExcelVO {

    /**
     * 客户编号
     */
    @ExcelProperty({"客户信息", "客户编号"})
    private String clientCode;
    @ExcelProperty({"客户信息", "客户名称"})
    private String clientName;

    @ExcelProperty({"客户信息", "资信等级"})
    private String clientLevelName;

    /**
     * 是否内部客户
     */
    @ExcelProperty({"客户信息", "内部客户"})
    private String isInternal;

    /**
     * 监管客户类型
     */
    @ExcelProperty({"客户信息", "监管客户类型"})
    private String clientSuperviseType;
    /**
     * 期初结存
     */
    @ExcelProperty(value = {"收支与结存", "期初结存"})
    private BigDecimal startBalance;


    /**
     * 出金入金
     */
    @ExcelProperty(value = {"收支与结存", "出金入金"})
    private BigDecimal inOutPrice;

    /**
     * 成交收支
     */
    @ExcelProperty(value = {"收支与结存", "成交收支"})
    private BigDecimal tradePrice;

    /**
     * 了结收支
     */
    @ExcelProperty(value = {"收支与结存", "了结收支"})
    private BigDecimal closePrice;

    /**
     * 其他收支
     */
    @ExcelProperty(value = {"收支与结存", "其他收支"})

    private BigDecimal otherPrice;

    /**
     * 期末结存
     */
    @ExcelProperty(value = {"收支与结存", "期末结存"})
    private BigDecimal endBalance;

    /**
     * 质押市值
     */
    @ExcelProperty(value = {"收支与结存", "质押市值"})
    private BigDecimal pledgePrice;

    /**
     * 保证金占用
     */
    @ExcelProperty(value = {"占用与可取", "保证金占用"})
    private BigDecimal marginOccupyPrice;

    /**
     * 可用资金
     */
    @ExcelProperty(value = {"占用与可取", "可用资金"})
    private BigDecimal availablePrice;

    /**
     * 授信额度
     */
    @ExcelProperty(value = {"占用与可取", "授信额度"})
    private BigDecimal creditPrice;

    /**
     * 授信占用
     */
    @ExcelProperty(value = {"占用与可取", "授信占用"})
    BigDecimal creditOccupyPrice;

    /**
     * 追保金额
     */
    @ExcelProperty(value = {"占用与可取", "追保金额"})
    private BigDecimal additionalPrice;

    /**
     * 可取资金
     */
    @ExcelProperty(value = {"占用与可取", "可取资金"})
    private BigDecimal desirablePrice;

    /**
     * 实现盈亏
     */
    @ExcelProperty(value = {"盈亏和估值", "实现盈亏"})
    private BigDecimal realizeProfitLoss;

    /**
     * 持仓盈亏
     */
    @ExcelProperty(value = {"盈亏和估值", "持仓盈亏"})
    private BigDecimal positionProfitLoss;
    /**
     * 持仓市值
     */
    @ExcelProperty(value = {"盈亏和估值", "持仓市值"})
    private BigDecimal positionValue;
    /**
     * 总资产
     */
    @ExcelProperty(value = {"盈亏和估值", "总资产"})
    private BigDecimal totalAssets;
}
