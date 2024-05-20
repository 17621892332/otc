package org.orient.otc.api.client.vo;

import lombok.Data;

import java.util.List;

@Data
public class ClientInfoDetailAllVo {

    ClientInfoDetailVo ClientInfo;

    List<BankCardInfoYLVO> BankCards;

    List<DutyInfoVo> Duties;

}
