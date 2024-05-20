package org.orient.otc.user.vo;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class ExchangeAccountLoginVO {
    /**
     * 经纪公司代码
     */
    private String brokerId;
    /**
     * 用户代码
     */
    private String userId;
    /**
     * 前置编号
     */
    private String frontId;
    /**
     * 会话编号
     */
    private String sessionId;
    /**
     * 交易日
     */
    private String tradingDay;
    /**
     * 登录成功时间
     */
    private String loginTime;

    /**
     * 错误ID
     */
    private Integer errorId;

    /**
     * 错误信息
     */
    private String errorMsg;

}
