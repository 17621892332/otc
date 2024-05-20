package org.orient.otc.quote.vo.jni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.quote.vo.jni.SoResultVo;

import java.util.List;
@Data
@ApiModel
public class QuoteResultVo{
    @ApiModelProperty("返回结果list")
    List<SoResultVo> resultVoList;
}
