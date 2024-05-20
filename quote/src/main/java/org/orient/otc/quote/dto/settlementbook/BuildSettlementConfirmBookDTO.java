package org.orient.otc.quote.dto.settlementbook;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/** 生成结算确认书dto
 * @author dzrh
 */
@Data
public class BuildSettlementConfirmBookDTO {
    @ApiModelProperty("平仓ID")
    private List<Integer>  closeIdList;
    /**
     * 以pdf还是word的形式下载
     * 0 : word
     * 1 : pdf (zip中也是pdf)
     * 2 : word和pdf两种形式一起下载
     */
    private int isPdf;
}
