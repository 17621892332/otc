package org.orient.otc.api.enums;

/**
 * 情景分析类型
 */
public enum ReportTypeEnum {
    /**
     * 价格
     */
    spotLadder("价格"),
    /**
     * 波动率
     */
    spotVol("波动率"),
    /**
     * 日期
     */
    spotDate("日期"),
    /**
     * BucketedVega
     */
    bucketedVega("BucketedVega"),
    ;
    private String desc;

    ReportTypeEnum(String desc) {
        this.desc = desc;
    }
}
