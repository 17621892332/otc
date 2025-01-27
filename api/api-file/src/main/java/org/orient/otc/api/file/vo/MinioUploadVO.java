package org.orient.otc.api.file.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件上传返回结果
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MinioUploadVO {
    @ApiModelProperty("文件访问URL")
    private String url;

    @ApiModelProperty("文件路径")
    private String path;

    @ApiModelProperty("文件名称")
    private String name;
}
