package org.orient.otc.quote.dto.quote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 模板参数
 */
@Data
@ApiModel
public class QuoteTemplateDto {

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    @NotEmpty(message = "模板名称不能为空")
    private String templateName;

    /**
     * 定价内容列表
     */
    @ApiModelProperty(value = "定价内容列表")
    @NotNull(message = "模板内容不能为空")
    private List<QuoteContentDTO> quoteList;

    /**
     * 是否公共
     */
    @ApiModelProperty(value = "是否公共")
    @NotNull(message = "是否公共不能为空")
    private String isPublic;



}
