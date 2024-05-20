package org.orient.otc.quote.dto.confirmbook;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.BuyOrSellEnum;
import org.orient.otc.api.quote.enums.CallOrPutEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.SettleTypeEnum;
import org.orient.otc.quote.entity.TradeObsDate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易确认书格式汇总对象
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class TradeConfirmBookGroup implements Serializable {

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 日期
     */
    private LocalDate  tradeDate;

    /**
     * 交易数据
     */
    private List<TradeConfirmBookDTO> dataList;

}
