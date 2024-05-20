package org.orient.otc.quote.dto.daily.transdetail;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;

@Data
public class TransDetailPageDto extends BasePage {
    @ApiModelProperty("对方户名")
    private String oppunit;

    @ApiModelProperty("明细流水号")
    private String detailid;

    @ApiModelProperty("对账标识码")
    private String bankcheckflag;

    @ApiModelProperty("入账状态 [3:已入账, 0:待入账]")
    private Integer receredtype;

    @ApiModelProperty("是否退票")
    private Integer isrefund;

    @ApiModelProperty("是否接收")
    private Integer isreced;

    @ApiModelProperty("是否确认")
    private Integer isconfirm;

    @ApiModelProperty("对方账号")
    private String oppbanknumber;

    @ApiModelProperty("归属起始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate transdatestart;

    @ApiModelProperty("归属结束日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate transdateend;

    @ApiModelProperty("交易起始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate bizdatestart;

    @ApiModelProperty("交易结束日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate bizdateend;
}
