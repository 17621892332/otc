package org.orient.otc.yl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 执行场外期权交易了结
 * @author dzrh
 */
@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeCloseDto {
  /**
   * 交易编号
   */
  private String tradeNumber;
  /**
   * 了结方式：平仓，行权(到期日行权用)，到期，提前行权(美式期权用)
   */
  private String closeType;
  /**
   * 了结日期，格式: yyyy-MM-dd
   */
  private LocalDate closeDate;
  /**
   * 了结数量，数量成交方式时使用
   */
  private BigDecimal closeTradeAmount;
  /**
   * 了结数量比例，名义本金成交方式时使用
   */
  private BigDecimal closeTradeAmountRate;
  /**
   * 平仓单价，了结方式为平仓并且数量成交方式时使用
   */
  private BigDecimal unwindPrice;
  /**
   * 平仓单价比例，了结方式为平仓并且名义本金成交方式时使用
   */
  private BigDecimal unwindPriceRate;
  /**
   * 平仓总额，了结方式为平仓时使用，两种成交方式都适用
   */
  private BigDecimal unwindTotalFee;
  /**
   * 了结标的价格
   */
  private BigDecimal underlyingPrice;
  /**
   * 平仓波动率
   */
  private BigDecimal unwindVolatility;
  /**
   * 是否跳过审批流程,默认true，为false并且工作流程已配置时操作执行成功后交易状态为‘待复核’状态，否则交易状态为完全/部分了结状态 20200806新增
   */
  private Boolean skipWorkflow;

}
