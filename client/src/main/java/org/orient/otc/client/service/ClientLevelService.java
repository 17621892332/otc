package org.orient.otc.client.service;

import org.orient.otc.api.client.vo.ClientLevelVo;
import org.orient.otc.client.dto.clientlevel.ClientLevelAddDto;
import org.orient.otc.client.dto.clientlevel.ClientLevelListDto;
import org.orient.otc.client.dto.clientlevel.ClientLevelUpdateDto;
import org.orient.otc.client.entity.ClientLevel;
import org.orient.otc.common.database.config.IServicePlus;

import java.util.List;

public interface ClientLevelService extends IServicePlus<ClientLevel> {
    ClientLevel getClientLevelById(Integer id);

    ClientLevelVo getClientLevelVoByClientId(Integer clientId);

    List<ClientLevel> getList(ClientLevelListDto dto);

    String updateClientLevel(ClientLevelUpdateDto dto);

    String addClientLevel(ClientLevelAddDto dto);

    String deleteClientLevel(Integer id);
}
