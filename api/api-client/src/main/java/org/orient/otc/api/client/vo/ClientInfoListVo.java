package org.orient.otc.api.client.vo;

import lombok.Data;

import java.util.List;

@Data
public class ClientInfoListVo {

    List<DutyInfoVo> dutiesInfoList;

    List<BankCardInfoYLVO> bankCardInfoList;

    List<ClientInfoDetailVo> dbList;

}
