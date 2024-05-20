package org.orient.otc.netty.vo;

import lombok.Data;

/**
 * 合约列表
 */
@Data
public class UnderlyingVarietyVO {

    /**
     * 合约代码
     */
    private String underlyingCode;


    /**
     * 品种代码
     */
    private String varietyCode;
}
