package org.orient.otc.api.client.vo;

import lombok.Data;

@Data
public class BankCardInfoYLVO{

    private Integer clientId;
    private String name;
    private String bank;
    private String card;
    private String payment;
    private String comments;
    private String usage;
    private String validState;
}
