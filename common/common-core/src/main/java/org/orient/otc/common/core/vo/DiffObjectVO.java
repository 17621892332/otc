package org.orient.otc.common.core.vo;

import lombok.Data;

/**
 * 对象差异VO
 */
@Data
public class DiffObjectVO {
    /**
     * 字段名
     */
    String name;
    /**
     * 字段原值
     */
    Object orgValue;
    /**
     * 字段目标值
     */
    Object destValue;

    @Override
    public String toString() {
        return "DiffObjectVO{" +
                "name='" + name + '\'' +
                ", orgValue=" + orgValue +
                ", destValue=" + destValue +
                '}';
    }
}
