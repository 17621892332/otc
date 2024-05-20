package org.orient.otc.api.vo;

import lombok.Data;

import java.util.List;

/**
 * 情景分析返回
 */
@Data
public class PythonResult<T> {

    private List<String> error_message;
    private List<T> results;
}
