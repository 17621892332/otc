package org.orient.otc.openapi.vo;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class BaseResult<T> {
    /**
     * 为0表示无错
     */
    String errorCode;

    /**
     * 出错时的错误信息
     */
    String message;

    boolean status;
    /**
     * 结果数据
     */
    DataResult<T> data;
}
