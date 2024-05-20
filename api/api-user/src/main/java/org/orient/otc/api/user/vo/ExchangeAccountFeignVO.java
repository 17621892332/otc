package org.orient.otc.api.user.vo;


import lombok.Data;

@Data
public class ExchangeAccountFeignVO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 账户名称
     */
    private String name;

    /**
     * 账户
     */
    private String account;

    private String password;
    /**
     * 簿记ID
     */
    private Integer assetunitId;
}
