package org.orient.otc.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.system.dto.clientdatachangerecord.APIClientDataChangeRecordAddDto;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.apache.commons.lang3.ObjectUtils;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.client.dto.BankCardInfoAddDto;
import org.orient.otc.client.dto.BankCardInfoDeleteDto;
import org.orient.otc.client.dto.BankCardInfoQueryByClientIdDto;
import org.orient.otc.client.dto.BankCardInfoUpdateDto;
import org.orient.otc.client.entity.BankCardInfo;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.mapper.BankCardInfoMapper;
import org.orient.otc.client.mapper.ClientMapper;
import org.orient.otc.client.service.BankCardInfoService;
import org.orient.otc.client.vo.BankCardInfoVO;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BankCardInfoServiceImpl extends ServiceImpl<BaseMapper<BankCardInfo>, BankCardInfo> implements BankCardInfoService {
    @Autowired
    BankCardInfoMapper bankCardInfoMapper;

    @Resource
    ClientMapper clientMapper;

    @Resource
    UserClient userClient;

    @Autowired
    private SystemDataChangeRecordClient systemDataChangeRecordClient;
    @Resource
    private ObjectEqualsUtil objectEqualsUtil;

    @Override
    public List<BankCardInfoVO> getByClientId(BankCardInfoQueryByClientIdDto dto) {
        LambdaQueryWrapper<BankCardInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BankCardInfo::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(BankCardInfo::getClientId,dto.getClientId());
        if (ObjectUtils.isNotEmpty(dto.getIsEffective())) {
            lambdaQueryWrapper.eq(BankCardInfo::getIsEffective, dto.getIsEffective());
        }
        List<BankCardInfo> list = bankCardInfoMapper.selectList(lambdaQueryWrapper);
        List<BankCardInfoVO> returnList = list.stream().map(item->{
            BankCardInfoVO vo = new BankCardInfoVO();
            BeanUtils.copyProperties(item,vo);
            return vo;
        }).collect(Collectors.toList());
        Set<Integer> updatorIdSet = list.stream().map(item->item.getUpdatorId()).collect(Collectors.toSet());
        // key=userID , value=name
        Map<Integer,String> userMap =  userClient.getUserMapByIds(updatorIdSet);
        Client client = clientMapper.selectById(dto.getClientId());
        if(CollectionUtils.isNotEmpty(returnList)) {
            for (BankCardInfoVO vo : returnList) {
                vo.setClient(client);
                vo.setUpdatorName(userMap.get(vo.getUpdatorId()));
            }
        }

        return returnList;
    }

    @Override
    public String add(BankCardInfoAddDto dto) {
        BankCardInfo bankCardInfo = new BankCardInfo();
        BeanUtils.copyProperties(dto,bankCardInfo);
        this.saveOrUpdate(bankCardInfo);
        // 添加变更记录
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(dto.getClientId());
        Client client = clientMapper.selectById(dto.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientBankInfoAdd);
        List<DiffObjectVO> list = new ArrayList<>();
        if (StringUtils.isNotBlank(dto.getOpenBank())){
            list.add(objectEqualsUtil.buildDiffObjectVO("开户行","",dto.getOpenBank()));
        }
        if (StringUtils.isNotBlank(dto.getBankAccount())){
            list.add(objectEqualsUtil.buildDiffObjectVO("银行卡号","",dto.getBankAccount()));
        }
        if (StringUtils.isNotBlank(dto.getLargeBankAccount())){
            list.add(objectEqualsUtil.buildDiffObjectVO("大额行号","",dto.getLargeBankAccount()));
        }
        if (StringUtils.isNotBlank(dto.getRemark())){
            list.add(objectEqualsUtil.buildDiffObjectVO("备注","",dto.getRemark()));
        }
        if (StringUtils.isNotBlank(dto.getPurpose())){
            list.add(objectEqualsUtil.buildDiffObjectVO("用途","",dto.getPurpose()));
        }
        if (dto.getIsEffective()==1){
            list.add(objectEqualsUtil.buildDiffObjectVO("是否有效","","无效"));
        } else if (dto.getIsEffective()==0){
            list.add(objectEqualsUtil.buildDiffObjectVO("是否有效","","有效"));
        }
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return "新增客户银行账户信息成功";
    }

    @Override
    public String update(BankCardInfoUpdateDto dto) {
        BankCardInfo entity = bankCardInfoMapper.selectById(dto.getId());
        BankCardInfo bankCardInfo = new BankCardInfo();
        BeanUtils.copyProperties(dto,bankCardInfo);
        this.saveOrUpdate(bankCardInfo);
        // 添加银行卡变更记录
        BankCardInfoVO orgObj = new BankCardInfoVO();
        BeanUtils.copyProperties(entity,orgObj);
        BankCardInfoVO destObj = new BankCardInfoVO();
        BeanUtils.copyProperties(bankCardInfo,destObj);
        List<DiffObjectVO> list = objectEqualsUtil.equalsObjectField(orgObj,destObj);
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(dto.getClientId());
        Client client = clientMapper.selectById(dto.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientBankInfoUpdate);
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return "修改客户银行账户信息成功";
    }

    @Override
    public String delete(BankCardInfoDeleteDto dto) {
        LambdaUpdateWrapper<BankCardInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(BankCardInfo::getId,dto.getId());
        lambdaUpdateWrapper.set(BankCardInfo::getIsDeleted,IsDeletedEnum.YES);
        bankCardInfoMapper.update(null,lambdaUpdateWrapper);
        // 添加银行卡变更记录
        BankCardInfo entity = bankCardInfoMapper.selectById(dto.getId());
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(entity.getClientId());
        Client client = clientMapper.selectById(entity.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientBankInfoDelete);
        List<DiffObjectVO> list = new ArrayList<>();
        list.add(objectEqualsUtil.getDeleteDiffObjectVO());
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return "删除客户银行账户信息成功";
    }

    @Override
    public String enable(BankCardInfoUpdateDto dto) {
        BankCardInfo entity = bankCardInfoMapper.selectById(dto.getId());
        LambdaUpdateWrapper<BankCardInfo> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(BankCardInfo::getId,dto.getId());
        lambdaUpdateWrapper.set(BankCardInfo::getIsEffective,dto.getIsEffective());
        bankCardInfoMapper.update(null,lambdaUpdateWrapper);
        // 添加银行卡变更记录
        APIClientDataChangeRecordAddDto apiDto = new APIClientDataChangeRecordAddDto();
        apiDto.setClientId(entity.getClientId());
        Client client = clientMapper.selectById(entity.getClientId());
        apiDto.setClientCode(client.getCode());
        apiDto.setChangeType(DataChangeTypeEnum.clientBankInfoUpdate);
        List<DiffObjectVO> list = new ArrayList<>();
        String orgValue = entity.getIsEffective()==1?"无效":"有效";
        String odestValue = dto.getIsEffective()==1?"无效":"有效";
        list.add(objectEqualsUtil.buildDiffObjectVO("是否有效",orgValue,odestValue));
        apiDto.setChangeFields(JSON.toJSONString(list));
        systemDataChangeRecordClient.addClientDataChangeRecord(apiDto);
        return "操作成功";
    }


    @Override
    public int updateByIdCardNo(BankCardInfo bankCardInfo) {
        UpdateWrapper<BankCardInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("bankAccount", bankCardInfo.getBankAccount())
                .eq("clientId", bankCardInfo.getClientId())
                .eq("isDeleted",0);
        return bankCardInfoMapper.update(bankCardInfo, updateWrapper);
    }

    @Override
    public long getBankCardInfoByBankAccount(BankCardInfo bankCardInfo) {
        return bankCardInfoMapper.selectCount(new LambdaQueryWrapper<BankCardInfo>().eq(BankCardInfo :: getBankAccount,bankCardInfo.getBankAccount()).eq(BankCardInfo :: getClientId,bankCardInfo.getClientId()).eq(BankCardInfo :: getIsDeleted,0));
    }

    @Override
    public int add(BankCardInfo bankCardInfo) {
        return bankCardInfoMapper.insert(bankCardInfo);
    }
}
