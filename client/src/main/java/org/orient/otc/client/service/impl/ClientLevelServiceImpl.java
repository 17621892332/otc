package org.orient.otc.client.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.client.vo.ClientLevelVo;
import org.orient.otc.client.dto.clientlevel.ClientLevelAddDto;
import org.orient.otc.client.dto.clientlevel.ClientLevelListDto;
import org.orient.otc.client.dto.clientlevel.ClientLevelUpdateDto;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.entity.ClientLevel;
import org.orient.otc.client.exception.BussinessException;
import org.orient.otc.client.mapper.ClientLevelMapper;
import org.orient.otc.client.mapper.ClientMapper;
import org.orient.otc.client.service.ClientLevelService;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class ClientLevelServiceImpl extends ServiceImpl<BaseMapper<ClientLevel>, ClientLevel> implements ClientLevelService {
    @Resource
    private ClientLevelMapper clientLevelMapper;
    @Resource
    private ClientMapper clientMapper;
    @Override
    public ClientLevel getClientLevelById(Integer id) {

        return clientLevelMapper.selectOne(new LambdaQueryWrapper<ClientLevel>().eq(ClientLevel :: getId,id).eq(ClientLevel :: getIsDeleted,0));
    }

    @Override
    public ClientLevelVo getClientLevelVoByClientId(Integer clientId) {
        Client client = clientMapper.selectById(clientId);
        BussinessException.E_700105.assertTrue(Objects.nonNull(client),"客户ID:"+clientId);
        return this.getVoById(client.getLevelId(), ClientLevelVo.class);
    }

    @Override
    public List<ClientLevel> getList(ClientLevelListDto dto) {
        LambdaQueryWrapper<ClientLevel> queryWrapper = new LambdaQueryWrapper<ClientLevel>();
        queryWrapper.eq(ClientLevel :: getIsDeleted,0);
        queryWrapper.like(StringUtils.isNotBlank(dto.getName()),ClientLevel :: getName,dto.getName());
        return clientLevelMapper.selectList(queryWrapper);
    }

    @Override
    public String updateClientLevel(ClientLevelUpdateDto dto) {
        LambdaUpdateWrapper<ClientLevel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ClientLevel::getId,dto.getId());
        updateWrapper.set(StringUtils.isNotBlank(dto.getName()),ClientLevel::getName,dto.getName());
        updateWrapper.set(null != dto.getMarginRate(),ClientLevel::getMarginRate,dto.getMarginRate());
        clientLevelMapper.update(null,updateWrapper);
        return "客户等级修改成功";
    }

    @Override
    public String addClientLevel(ClientLevelAddDto dto) {
        ClientLevel entity = new ClientLevel();
        BeanUtils.copyProperties(dto,entity);
        clientLevelMapper.insert(entity);
        return "新增客户等级成功";
    }

    @Override
    public String deleteClientLevel(Integer id) {
        LambdaQueryWrapper<Client> clientLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clientLambdaQueryWrapper.eq(Client::getIsDeleted, IsDeletedEnum.NO);
        clientLambdaQueryWrapper.eq(Client::getLevelId, id);
        // 查看当前客户等级是否有绑定客户
        Long count = clientMapper.selectCount(clientLambdaQueryWrapper);
        if (count>0){
            BussinessException.E_700106.assertTrue(false,"当前客户等级有客户绑定");
        }
        LambdaUpdateWrapper<ClientLevel> clientLevelLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        clientLevelLambdaUpdateWrapper.set(ClientLevel::getIsDeleted,IsDeletedEnum.YES);
        clientLevelLambdaUpdateWrapper.eq(ClientLevel::getId,id);
        clientLevelMapper.update(null,clientLevelLambdaUpdateWrapper);
        return "客户等级删除成功";
    }
}
