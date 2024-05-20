package org.orient.otc.common.security.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dzrh
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLogInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 服务名
     */
    private String serverName;

    /**
     * 请求方法
     */
    @ApiModelProperty( value = "请求方法", required = true)
    private String path;
    /**
     * 请求参数
     */
    @ApiModelProperty( value = "请求参数", required = true)
    private String requestInfo;

    /**
     * 响应内容
     */
    @ApiModelProperty( value = "响应内容", required = true)
    private String responseData;
    /**
     * 请求ip
     */
    @ApiModelProperty( value = "请求ip", required = true)
    private String ip;

    /**
     * 请求用户ID
     */
    @ApiModelProperty( value = "请求用户ID", required = true)
    private Integer requestUserId;
    /**
     * 请求用户名称
     */
    @ApiModelProperty( value = "请求用户名称", required = true)
    private String requestUserName;

    /**
     * 请求耗时
     */
    private Long spendTimes;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;
}
