package org.orient.otc.client.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.quote.feign.SettlementClient;
import org.orient.otc.api.system.dto.clientdatachangerecord.APIClientDataChangeRecordAddDto;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.orient.otc.api.system.vo.ClientDataChangeDetailVO;
import org.orient.otc.client.dto.AffiliatedOrganizationDto;
import org.orient.otc.client.dto.ClientDetailDto;
import org.orient.otc.client.dto.ClientPageDto;
import org.orient.otc.client.entity.BankCardInfo;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.entity.ClientDuty;
import org.orient.otc.client.entity.ClientLevel;
import org.orient.otc.client.mapper.BankCardInfoMapper;
import org.orient.otc.client.mapper.ClientDutyMapper;
import org.orient.otc.client.mapper.ClientLevelMapper;
import org.orient.otc.client.mapper.ClientMapper;
import org.orient.otc.client.service.ClientService;
import org.orient.otc.client.vo.*;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pjc
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {
    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ClientLevelMapper clientLevelMapper;

    @Resource
    private BankCardInfoMapper bankCardInfoMapper;

    @Resource
    private ClientDutyMapper clientDutyMapper;

    @Autowired
    private SystemDataChangeRecordClient systemDataChangeRecordClient;
    @Resource
    ObjectEqualsUtil objectEqualsUtil;

    @Autowired
    SettlementClient settlementClient;

    @Override
    public List<Client> getList() {
        List<Client> list= clientMapper.selectList(new LambdaQueryWrapper<Client>().eq(Client :: getIsDeleted,0));

        return list.stream().peek(item->{
            if (StringUtils.isBlank(item.getShortName()) ){
                item.setShortName(item.getName().substring(0, Math.min(item.getName().length(), 8)));
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<Client> queryByIds(Set<Integer> ids) {
        LambdaQueryWrapper<Client> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.in(ids!=null&& !ids.isEmpty(),Client::getId,ids);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<ClientVo> getListByPage(ClientPageDto dto) {
        LambdaQueryWrapper<Client> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper
                .like(StringUtils.isNotBlank(dto.getCode()),Client::getCode,dto.getCode())
                .eq(dto.getLevelId()!=null,Client::getLevelId,dto.getLevelId())
                .like(StringUtils.isNotBlank(dto.getName()),Client::getName,dto.getName())
                .eq(Client::getIsDeleted,IsDeletedEnum.NO)
        ;
        IPage<Client> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),queryWrapper);
        Set<Integer> levelIds = ipage.getRecords().stream().map(Client::getLevelId).collect(Collectors.toSet());
        List<ClientLevel> clientLevelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(levelIds)) {
            clientLevelList = clientLevelMapper.selectBatchIds(levelIds);
        }
        // key = id , value = ClientLevel
        Map<Integer,ClientLevel> clientLevelMap= clientLevelList.stream().collect(Collectors.toMap(ClientLevel::getId, item->item,(v1, v2)->v2));
        return ipage.convert(item->{
            ClientVo vo = new ClientVo();
            BeanUtils.copyProperties(item,vo);
            if (StringUtils.isBlank(item.getShortName()) ){
                vo.setShortName(item.getName().substring(0, Math.min(item.getName().length(), 8)));
            }
            vo.setClientLevel(clientLevelMap.get(item.getLevelId()));
            return vo;
        });
    }

    @Override
    public Map<Integer, BigDecimal> getClientMarginRate(Set<Integer> idSet) {
        List<Client> clientList = this.queryByIds(idSet);
        List<ClientLevel> clientLevelList = clientLevelMapper.selectList(new LambdaQueryWrapper<ClientLevel>()
                .eq(ClientLevel::getIsDeleted,IsDeletedEnum.NO));
        Map<Integer, BigDecimal> clientLevelMap = clientLevelList.stream().collect(Collectors.toMap(ClientLevel::getId,ClientLevel::getMarginRate,(v1,v2)->v2));
        return clientList.stream().collect(Collectors.toMap(Client::getId,a->clientLevelMap.get(a.getLevelId()),(v1,v2)->v2));
    }

    @Override
    public List<ClientVo> getClientAndBankInfoList() {
        List<ClientVo> returnList = new ArrayList<>();
        LambdaQueryWrapper<Client> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.orderByDesc(Client::getCreateTime);
        List<Client> list =  this.list(queryWrapper);
        if(!CollectionUtils.isEmpty(list)){
            Set<Integer> clientIds = list.stream().map(Client::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<BankCardInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BankCardInfo::getIsDeleted,IsDeletedEnum.NO);
            lambdaQueryWrapper.in(BankCardInfo::getClientId,clientIds);
            List<BankCardInfo> bankCardInfoList = bankCardInfoMapper.selectList(lambdaQueryWrapper);
            for(Client client : list) {
                ClientVo vo = new ClientVo();
                BeanUtils.copyProperties(client,vo);
                if(!CollectionUtils.isEmpty(bankCardInfoList)) {
                    List<BankCardInfo> tempList = bankCardInfoList.stream().filter(item->item.getClientId().equals(client.getId())).collect(Collectors.toList());
                    vo.setBankCardInfoList(tempList);
                }
                returnList.add(vo);
            }
        }
        return returnList;
    }

    @Override
    public ClientDetailVo getClientDetail(String clientCode) {
        // 根据clientCode查询客户信息
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Client::getCode, clientCode).eq(Client::getIsDeleted, IsDeletedEnum.NO);
        Client client = clientMapper.selectOne(queryWrapper);

        if (client != null) {
            // 创建ClientDetailVo对象并复制属性
            ClientDetailVo clientVo = new ClientDetailVo();
            BeanUtils.copyProperties(client,clientVo);
            clientVo.setMarginOptionType(String.valueOf(client.getMarginOptionType()));
            clientVo.setSamePeer(String.valueOf(client.getSamePeer()));
            clientVo.setFundsSource(String.valueOf(client.getFundsSource()));
            clientVo.setBadFaithRecord(String.valueOf(client.getBadFaithRecord()));
            clientVo.setRiskServiceDegree(String.valueOf(client.getRiskServiceDegree()));
            clientVo.setInvestmentTerm(String.valueOf(client.getInvestmentTerm()));
            clientVo.setTransactionTargetList(clientVo.getTransactionTarget());
            clientVo.setDerivativesInvestmentVarietiesList(clientVo.getDerivativesInvestmentVarieties());
            clientVo.setTradingInstTypeList(clientVo.getTradingInstType());
            clientVo.setIsListed(String.valueOf(client.getIsListed()));
            clientVo.setIsTradeCredit(String.valueOf(client.getIsTradeCredit()));
            clientVo.setIsIndustryConnectVariety(String.valueOf(client.getIsIndustryConnectVariety()));
            clientVo.setIsAssessmentResultChange(String.valueOf(client.getIsAssessmentResultChange()));
            clientVo.setIsRequireConversionTypes(String.valueOf(client.getIsRequireConversionTypes()));
            clientVo.setAppropriatenessDegree(String.valueOf(client.getAppropriatenessDegree()));
            clientVo.setIsInsided(String.valueOf(client.getIsInsided()));
            LambdaQueryWrapper<ClientLevel> clientLevelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            clientLevelLambdaQueryWrapper.select(ClientLevel::getName)
                    .eq(ClientLevel::getId, clientVo.getLevelId())
                    .eq(ClientLevel::getIsDeleted, IsDeletedEnum.NO);
            ClientLevel clientLevel = clientLevelMapper.selectOne(clientLevelLambdaQueryWrapper);
            if(clientLevel!=null) {
                clientVo.setLevel(clientLevel.getName());
            }
            if(client.getParentId()!=null){
                LambdaQueryWrapper<Client> queryParentWrapper = new LambdaQueryWrapper<>();
                queryParentWrapper.eq(Client::getId,client.getParentId()).eq(Client::getIsDeleted, IsDeletedEnum.NO);
                Client parentClient = clientMapper.selectOne(queryParentWrapper);
                if(parentClient!=null) {
                    clientVo.setParentName(parentClient.getName());
                }
            }
            // 查询银行卡信息
            List<BankCardInfoDetailVo> bankCardInfoVOList = getBankCardInfoList(client.getId());
            clientVo.setBankCardInfoList(bankCardInfoVOList);
            // 查询客户职责信息
            List<ClientDutyVo> clientDutyVoList = getClientDutyList(client.getId());
            clientVo.setClientDutyList(clientDutyVoList);
            return clientVo;
        }
        // 如果没有找到对应的客户，可以返回null或者抛出异常，视情况而定
        return null;
    }

    // 查询银行卡信息
    private List<BankCardInfoDetailVo> getBankCardInfoList(int clientId) {
        LambdaQueryWrapper<BankCardInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BankCardInfo::getClientId, clientId)
                .eq(BankCardInfo::getIsDeleted, IsDeletedEnum.NO);
        List<BankCardInfo> bankCardInfos = bankCardInfoMapper.selectList(queryWrapper);
        return CglibUtil.copyList(bankCardInfos, BankCardInfoDetailVo::new, (vo, db) -> {

        });
    }

    // 查询客户职责信息
    private List<ClientDutyVo> getClientDutyList(int clientId) {
        LambdaQueryWrapper<ClientDuty> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientDuty::getClientId, clientId)
                .eq(ClientDuty::getIsDeleted, IsDeletedEnum.NO);
        List<ClientDuty> clientDuties = clientDutyMapper.selectList(queryWrapper);
        return CglibUtil.copyList(clientDuties, ClientDutyVo::new, (vo, db) -> {
            db.setIdCardType(vo.getIdCardType().toString());
            db.setContactTypeIdList(Arrays.asList(vo.getContactTypeId().split(",\\s*")));
        });
    }

    @Override
    public String add(ClientDetailDto clientDetailDto) {
        Client client = new Client();
        BeanUtils.copyProperties(clientDetailDto,client);
        this.saveOrUpdate(client);
        return "新增客户信息成功";
    }

    @Override
    public String update(ClientDetailDto clientDetailDto) {
        Client dbClient = this.getById(clientDetailDto.getId());
        Client client = new Client();
        if(clientDetailDto.getTradingInstTypeList()!=null) {
            clientDetailDto.setTradingInstType(clientDetailDto.getTradingInstTypeList());
        }
        if(clientDetailDto.getTransactionTargetList()!=null) {
            clientDetailDto.setTransactionTarget(clientDetailDto.getTransactionTargetList());
        }
        if(clientDetailDto.getDerivativesInvestmentVarietiesList()!=null) {
            clientDetailDto.setDerivativesInvestmentVarieties(clientDetailDto.getDerivativesInvestmentVarietiesList());
        }
        BeanUtils.copyProperties(clientDetailDto, client);
        // 设置客户等级ID
        setClientLevelId(clientDetailDto, client);
        // 保存或更新Client对象到数据库
        this.saveOrUpdate(client);
        // 添加客户操作记录日志
        ClientDataChangeDetailVO orgVO = new ClientDataChangeDetailVO();
        BeanUtils.copyProperties(dbClient,orgVO);
        ClientDataChangeDetailVO destVO = new ClientDataChangeDetailVO();
        BeanUtils.copyProperties(dbClient,destVO); // 先取数据库中的信息
        // 复制bean忽略空值
        CopyOptions copyOption = CopyOptions.create(null, true);
        BeanUtil.copyProperties(client, destVO, copyOption); // 再取修改后的信息
        List<DiffObjectVO>  list = objectEqualsUtil.equalsObjectField(orgVO,destVO);
        APIClientDataChangeRecordAddDto apiClientDataChangeRecordAddDto = new APIClientDataChangeRecordAddDto();
        apiClientDataChangeRecordAddDto.setChangeFields(JSON.toJSONString(list));
        apiClientDataChangeRecordAddDto.setClientId(client.getId());
        apiClientDataChangeRecordAddDto.setClientCode(client.getCode());
        apiClientDataChangeRecordAddDto.setChangeType(DataChangeTypeEnum.update);
        systemDataChangeRecordClient.addClientDataChangeRecord(apiClientDataChangeRecordAddDto);
        // 返回成功消息
        return "修改客户信息成功";
    }
    private void setClientLevelId(ClientDetailDto clientDetailDto, Client client) {
        Optional.ofNullable(clientDetailDto.getLevel()).ifPresent(level -> {
            LambdaQueryWrapper<ClientLevel> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(ClientLevel::getId)
                    .eq(ClientLevel::getName, level)
                    .eq(ClientLevel::getIsDeleted, IsDeletedEnum.NO);

            Optional.ofNullable(clientLevelMapper.selectOne(queryWrapper))
                    .ifPresent(clientLevel -> client.setLevelId(clientLevel.getId()));
        });
    }
    @Override
    public List<AffiliatedOrganizationVo> getAffiliatedOrganization(AffiliatedOrganizationDto dto) {
        String name = dto.getName();
        LambdaQueryWrapper<Client> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Client::getId, Client::getName); // 指定只查询id和name字段
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Client::getName, name);
        }
        queryWrapper.last("limit 50");
        List<Client> clients = clientMapper.selectList(queryWrapper);// 执行查询并返回结果
        return CglibUtil.copyList(clients, AffiliatedOrganizationVo::new, (vo, db) -> {
            db.setId(vo.getId().toString());
        });
    }

    @Override
    public String delete(String clientCode) {
        LambdaUpdateWrapper<Client> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Client::getCode,clientCode);
        lambdaUpdateWrapper.set(Client::getIsDeleted,IsDeletedEnum.YES);
        clientMapper.update(null,lambdaUpdateWrapper);
        return "删除客户信息成功";
    }

    @Override
    public List<Client> selectByClientCodeSet(Set<String> clientCodeSet) {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.in(Client::getCode,clientCodeSet);
        return clientMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<Client> selectByClientNameSet(Set<String> clientNameSet) {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.in(Client::getName,clientNameSet);
        return clientMapper.selectList(lambdaQueryWrapper);
    }
}
