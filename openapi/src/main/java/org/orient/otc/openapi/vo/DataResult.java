package org.orient.otc.openapi.vo;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class DataResult<T> {
    /**
     * 为0表示无错
     */
    String filter;

    /**
     * 出错时的错误信息
     */
    String pageNo;

    String pageSize;

    boolean lastPage;
    /**
     * 结果数据
     */
    T rows;
}
