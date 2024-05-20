package org.orient.otc.quote.dto.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.quote.enums.TradeStateEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.util.List;

/** 生成交易确认书dto
 * @author dzrh
 */
@Data
public class BuildTradeConfirmBookDto{
    @ApiModelProperty("交易编号集合")
    private List<String>  tradeCodeList;
    /**
     * 以pdf还是word的形式下载
     * 0 : word
     * 1 : pdf (zip中也是pdf)
     * 2 : word和pdf两种形式一起下载
     */
    private int isPdf;
}
