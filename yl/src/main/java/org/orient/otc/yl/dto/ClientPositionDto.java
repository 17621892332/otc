package org.orient.otc.yl.dto;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class ClientPositionDto {
    /**
     * 分页请求页码(从1开始)
     */
    private  Integer pageIndex;
    /**
     * 分页请求页码(从1开始)
     */

    private Integer pageSize;
    /**
     * 客户ID过滤条件
     */
    private Integer  clientId;
    /**
     * 客户编号过滤条件,clientId小于1时生效
     */
    private String clientNumber;
    /**
     * 交易状态筛选集合,V2使用
     */
    private Integer[] tradeStatus;
    /**
     * 持仓取值日期(V1适用)，默认当前系统日期
     * 2022-04-18新增
     */
    private  String valueDate;
 }
