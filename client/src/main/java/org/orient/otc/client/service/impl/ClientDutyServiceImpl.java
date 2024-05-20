package org.orient.otc.client.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.system.dto.clientdatachangerecord.APIClientDataChangeRecordAddDto;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.api.system.feign.DictionaryClient;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.orient.otc.client.dto.ClientDutyDto;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.entity.ClientDuty;
import org.orient.otc.client.mapper.ClientDutyMapper;
import org.orient.otc.client.mapper.ClientMapper;
import org.orient.otc.client.service.ClientDutyService;
import org.orient.otc.client.vo.ClientDutyVo;
import org.orient.otc.client.vo.ClientMailVO;
import org.orient.otc.client.vo.client.ClientDutyVO;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientDutyServiceImpl extends ServiceImpl<BaseMapper<ClientDuty>, ClientDuty> implements ClientDutyService  {
    @Resource
    private ClientDutyMapper clientDutyMapper;
    @Resource
    DictionaryClient dictionaryClient;

    @Resource
    ClientMapper clientMapper;

    @Autowired
    private SystemDataChangeRecordClient systemDataChangeRecordClient;
    @Resource
    private ObjectEqualsUtil objectEqualsUtil;

    @Override
    public long getClientDutyByICardNo(ClientDuty clientDuty) {
        return clientDutyMapper.selectCount(new LambdaQueryWrapper<ClientDuty>().eq(ClientDuty :: getIdCardNo,clientDuty.getIdCardNo()).eq(ClientDuty :: getClientId,clientDuty.getClientId()).eq(ClientDuty :: getIsDeleted,0));
    }

    @Override
    public int add(ClientDuty clientDuty) {
        int count = clientDutyMapper.insert(clientDuty);
        // 添加客户人员变更记录
        ClientDutyVO destVo = new ClientDutyVO();
        BeanUtils.copyProperties(clientDuty,destVo);
        List<DiffObjectVO> list = objectEqualsUtil.getObjectFields(destVo);
        Client client = clientMapper.selectById(clientDuty.getClientId());
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(clientDuty.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientDutyYLAdd);
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return count;
    }

    @Override
    public int updateByIdCardNo(ClientDuty clientDuty) {
        ClientDutyVO orgVo = this.getVoById(clientDuty.getId(), ClientDutyVO.class);
        UpdateWrapper<ClientDuty> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("idCardNo", clientDuty.getIdCardNo())
                .eq("clientId", clientDuty.getClientId())
                .eq("isDeleted",0);
        int updateCount = clientDutyMapper.update(clientDuty, updateWrapper);
        ClientDutyVO destVo = new ClientDutyVO();
        BeanUtils.copyProperties(clientDuty,destVo);
        List<DiffObjectVO> list = objectEqualsUtil.equalsObjectField(orgVo,destVo);
        Client client = clientMapper.selectById(clientDuty.getClientId());
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(clientDuty.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientDutyUpdate);
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return updateCount;
    }

    @Override
    public String add(ClientDutyDto clientDutyDto) {
        ClientDuty clientDuty = new ClientDuty();
        BeanUtils.copyProperties(clientDutyDto,clientDuty);
        if(clientDutyDto.getContactTypeIdList()!=null){
            clientDuty.setContactTypeId(String.join(",", clientDutyDto.getContactTypeIdList()));
        }
        this.saveOrUpdate(clientDuty);
        // 添加客户人员变更记录
        ClientDutyVO destVo = new ClientDutyVO();
        BeanUtils.copyProperties(clientDutyDto,destVo);
        List<DiffObjectVO> list = objectEqualsUtil.getObjectFields(destVo);
        Client client = clientMapper.selectById(clientDuty.getClientId());
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(clientDuty.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientDutyAdd);
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return "新增人员信息成功";
    }

    @Override
    public String update(ClientDutyDto clientDutyDto) {
        ClientDutyVO orgVO = this.getVoById(clientDutyDto.getId(),ClientDutyVO.class);
        ClientDuty clientDuty = new ClientDuty();
        BeanUtils.copyProperties(clientDutyDto,clientDuty);
        if(clientDutyDto.getContactTypeIdList()!=null){
            clientDuty.setContactTypeId(String.join(",", clientDutyDto.getContactTypeIdList()));
        }
        this.saveOrUpdate(clientDuty);
        // 添加客户人员变更记录
        ClientDutyVO destVo = new ClientDutyVO();
        BeanUtils.copyProperties(clientDutyDto,destVo);
        List<DiffObjectVO> list = objectEqualsUtil.equalsObjectField(orgVO,destVo);
        Client client = clientMapper.selectById(clientDuty.getClientId());
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(clientDuty.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientDutyUpdate);
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return "修改人员信息成功";
    }

    @Override
    public List<ClientDutyVo> list(String id) {
        LambdaQueryWrapper<ClientDuty> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClientDuty::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(ClientDuty::getClientId, id);
        List<ClientDuty> clientDuties = clientDutyMapper.selectList(lambdaQueryWrapper);
        return CglibUtil.copyList(clientDuties, ClientDutyVo::new, (vo, db) -> {
            db.setIdCardType(vo.getIdCardType().toString());
            db.setContactTypeIdList(Arrays.asList(vo.getContactTypeId().split(",\\s*")));
        });
    }

    @Override
    public String delete(ClientDutyDto clientDutyDto) {
        LambdaUpdateWrapper<ClientDuty> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(ClientDuty::getId,clientDutyDto.getId());
        lambdaUpdateWrapper.set(ClientDuty::getIsDeleted,IsDeletedEnum.YES);
        clientDutyMapper.update(null,lambdaUpdateWrapper);
        // 添加变更记录
        ClientDuty entity = clientDutyMapper.selectById(clientDutyDto.getId());
        List<DiffObjectVO> list = new ArrayList<>();
        list.add(objectEqualsUtil.getDeleteDiffObjectVO());
        Client client = clientMapper.selectById(entity.getClientId());
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(entity.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientDutyDelete);
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return "删除人员信息成功";
    }

    /**
     * 获取客户联系人邮箱列表
     * @param id 客户ID
     * @return 返回邮箱列表
     */
    @Override
    public Map<String, List<ClientMailVO>> getMapByClientId(String id) {
        Map<String, List<ClientMailVO>> returnMap = new HashMap<>();
        List<ClientDutyVo> list = list(id);
        // 获取联系人类型 key = 类型ID,value = 类型名称
        Map<String,String>  dictionaryMap = dictionaryClient.getDictionaryMapByIds("ContactType");
        for (Map.Entry<String,String> entry : dictionaryMap.entrySet()) {
            String contactTypeId = entry.getKey();
            String contactTypeName = entry.getValue();
            // 查找联系人中, 联系人类型相同且接收邮件的联系人邮箱并返回
            //Set<String> receiveEmailSetTemp =  null;
            List<ClientDutyVo> clientList = list.stream().filter(item->item.getContactTypeIdList().contains(contactTypeId) && item.getIsReceiveEmail()==1).collect(Collectors.toList());
            if (!clientList.isEmpty()) {
                String charSequence = "，"; // 多个邮箱拼接符号
                List<ClientMailVO> sameTypeReceiveUser = new ArrayList<>(); // 同类型的收件列表
                for (ClientDutyVo clientItem : clientList) {
                    ClientMailVO clientMailVO = new ClientMailVO();
                    clientMailVO.setId(clientItem.getId());
                    clientMailVO.setEmail(clientItem.getEmail());
                    clientMailVO.setContactName(clientItem.getContactName());
                    Set<String> emailSet = new HashSet<>();
                    String email = clientItem.getEmail();
                    if (StringUtils.isNotBlank(email) && email.contains(charSequence)){ // 邮箱字符串中包含多个邮箱, 多个邮箱之间用中文逗号拼接的 , 需要分割开
                        List<String> emails = Arrays.asList(email.split(charSequence));
                        if (!emails.isEmpty()){
                            emails.forEach(item->{
                                if (StringUtils.isNotBlank(item)) { // 邮件非空
                                    emailSet.add(item);
                                }
                            });
                        }
                    } else {
                        emailSet.add(email);
                    }
                    clientMailVO.setEmailSet(emailSet);
                    sameTypeReceiveUser.add(clientMailVO);
                }
                returnMap.put(contactTypeName,sameTypeReceiveUser);
            }
        }
        return returnMap;
    }
}
