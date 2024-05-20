package org.orient.otc.common.core.feign;

import lombok.Data;

@Data
public class ExceptionInfo {
    /**
     * 异常时间
     */
    private String timestamp;

    /**
     * 自定义异常码
     */
    private int code;

    /**
     * 自定义异常消息
     */
    private String message;

    /**
     * 异常url
     */
    private String path;
}
