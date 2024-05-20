package org.orient.otc.api.client.vo;

import lombok.Data;

import java.util.List;
@Data
public class DutyInfoVo {
    private String contactName;
    private List<String> contactTypes;
    private String idCardNo;
    private String idCardType;
    private String phoneNumber;
    private String fax;
    private String email;
    private String address;
    private String deadLine;
    private Integer isReceiveEmail;
    private Integer clientId;
}
