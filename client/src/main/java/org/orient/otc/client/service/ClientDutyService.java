package org.orient.otc.client.service;

import org.orient.otc.client.dto.ClientDutyDto;
import org.orient.otc.client.entity.ClientDuty;
import org.orient.otc.client.vo.ClientDutyVo;
import org.orient.otc.client.vo.ClientMailVO;
import org.orient.otc.common.database.config.IServicePlus;

import java.util.List;
import java.util.Map;

public interface ClientDutyService extends IServicePlus<ClientDuty> {
    long getClientDutyByICardNo(ClientDuty clientDuty);

    int add(ClientDuty clientDuty);

    int updateByIdCardNo(ClientDuty clientDuty);

    String add(ClientDutyDto clientDutyDto);

    String update(ClientDutyDto clientDutyDto);

    List<ClientDutyVo> list(String id);

    String delete(ClientDutyDto clientDutyDto);

    Map<String, List<ClientMailVO>> getMapByClientId(String id);
}
