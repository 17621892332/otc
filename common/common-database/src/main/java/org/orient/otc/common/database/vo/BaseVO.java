package org.orient.otc.common.database.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 公用的基础字段VO类
 */
@Data
public class BaseVO {
    /**
     * 是否删除
     */
    @ApiModelProperty(hidden = true)
    private int isDeleted;
    /**
     * 创建用户ID
     */
    private Integer creatorId;

    /**
     * 最后更新用户ID
     */
    private Integer updatorId;

    /**
     * 记录创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
