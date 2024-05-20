package org.orient.otc.yl.vo;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class BaseResult<T> {
    /**
     * 为0表示无错
     */
    Integer errcode;

    /**
     * 出错时的错误信息
     */
    String errmsg;
    /**
     * 结果数据
     */
    T data;
}
