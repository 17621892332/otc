package org.orient.otc.quote.dto.settlementbook;

import lombok.Data;
import org.orient.otc.api.quote.enums.OptionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 结算确认书构造对象
 * @author dzrh
 */
@Data
public class SettlementBookData {

    private Integer id;

    private Integer clientId;

    private String clientName;

    private String tradeCode;

    private LocalDate tradeDate;

    private String tradeDateStr;

    private LocalDate maturityDate;

    private String maturityDateStr;

    private String underlyingCode;

    private String buyOrSellName;

    private OptionTypeEnum optionType;

    private String optionTypeName;

    private BigDecimal closeVolume;

    private BigDecimal strike;

    private BigDecimal entryPrice;

    private BigDecimal optionPremium;

    private BigDecimal totalAmount;

    private BigDecimal closeEntryPrice;

    private LocalDate closeDate;

    private String closeDateStr;

    private BigDecimal closePrice;

    private BigDecimal closeTotalAmount;

    private BigDecimal profitLoss;
}
