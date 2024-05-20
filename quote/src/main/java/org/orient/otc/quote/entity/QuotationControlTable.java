package org.orient.otc.quote.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * (QuotationControlTable)实体类
 * @author makejava
 * @since 2023-07-10 13:50:38
 */
@Data
public class QuotationControlTable implements Serializable {
    private static final long serialVersionUID = -67902807633277255L;
    /**
     * 鎶ヤ环绠＄悊ID锛屽敮涓€鏍囪瘑
     */
    private String itemid;

    private String itemname;

    private String message;

    private String optiontype;

    private String controltype;
    /**
     * 瀛楁浠ｇ爜
     */
    private String fieldcode;

    private String  fieldCodeEx;

    private String fieldalias;

    private String isinput;
    /**
     * 鍏宠仈浜嬩欢
     */
    private String relatedevent;

    private String controlleg1;

    private String controlleg2;

    private String controlleg3;

    private String controlleg4;

    private String controlleg5;

    private String dataformat;

    private String defaultvalue;

    private String datasource;

    private String displaymember;

    private String valuemember;

    private String bgcolor;

    private String readonly;

    private String valueFieldCode;

    private String enabled;

    private Integer itemsort;

    private String visible;
    /**
     * 0为开仓控制，1为平仓控制
     */
    private String flag;

    private String modifyenable;

    private String unit;

}

