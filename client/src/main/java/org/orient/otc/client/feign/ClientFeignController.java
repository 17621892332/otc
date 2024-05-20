package org.orient.otc.client.feign;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.*;
import org.orient.otc.client.entity.BankCardInfo;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.entity.ClientDuty;
import org.orient.otc.client.entity.ClientLevel;
import org.orient.otc.client.enums.ContactType;
import org.orient.otc.client.enums.IDCardType;
import org.orient.otc.client.service.BankCardInfoService;
import org.orient.otc.client.service.ClientDutyService;
import org.orient.otc.client.service.ClientLevelService;
import org.orient.otc.client.service.ClientService;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client")
@Slf4j
public class ClientFeignController implements ClientClient {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientLevelService clientLevelService;
    @Autowired
    private ClientDutyService clientDutyService;
    @Autowired
    private BankCardInfoService bankCardInfoService;
    @Resource
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor asyncTaskExecutor;


    @Override
    public ClientVO getClientById(Integer id) {
        return clientService.getVoById(id, ClientVO.class);
    }

    @Override
    public ClientLevelVo getClientLevel(Integer clientId) {
        return clientLevelService.getClientLevelVoByClientId(clientId);
    }

    @Override
    public Map<Integer, String> getClientMapByIds(Set<Integer> idSet) {
        List<Client> list = clientService.queryByIds(idSet);
        return list.stream().collect(Collectors.toMap(Client::getId, Client::getName));
    }

    @Override
    public Map<Integer, BigDecimal> getClientMarginRate(Set<Integer> idSet) {
        return  clientService.getClientMarginRate(idSet);
    }

    @Override
    public List<ClientVO> getClientListByIds(Set<Integer> idSet) {
        List<Client> list = clientService.queryByIds(idSet);
        List<ClientLevel> clientLevelList = clientLevelService.list();
        Map<Integer,String> levelMap = clientLevelList.stream().collect(Collectors.toMap(ClientLevel::getId,ClientLevel::getName));
        return CglibUtil.copyList(list, ClientVO::new,(db,vo)->vo.setLevelName(levelMap.get(db.getLevelId())));
    }


    /**
     * 从YL数据同步客户信息，包括客户基本信息、人员信息和银行卡信息。
     * @param clientInfoListVo YL数据客户信息列表VO
     * @return 同步操作是否成功
     */
    @Override
    public Boolean syncByYl(ClientInfoListVo clientInfoListVo) {
        // 复制客户基本信息列表
        List<Client> dbList = CglibUtil.copyList(clientInfoListVo.getDbList(), Client::new, (vo, db) -> {
            // 这里可以根据需要设置其他属性，例如：db.setName(vo.getName());
            LocalDateTime protocolSignDate = vo.getProtocolSignDate();
            if (protocolSignDate!=null){
                db.setProtocolSignDate(protocolSignDate.toLocalDate());
            }
            LocalDateTime processOptDate = vo.getProcessOptDate();
            if (processOptDate != null){
                db.setProcessOptDate(processOptDate.toLocalDate());
            }
            LocalDateTime evaluateDate = vo.getEvaluateDate();
            if (evaluateDate != null){
                db.setEvaluateDate(evaluateDate.toLocalDate());
            }
            LocalDateTime evaluateExpireDate = vo.getEvaluateExpireDate();
            if (evaluateExpireDate != null) {
                db.setEvaluateExpireDate(evaluateExpireDate.toLocalDate());
            }
            LocalDateTime licenseCodeDate = vo.getLicenseCodeDate();
            if (licenseCodeDate != null){
                db.setLicenseCodeDate(licenseCodeDate.toLocalDate());
            }
            LocalDateTime rightProtocolSignDate = vo.getRightProtocolSignDate();
            if (rightProtocolSignDate != null) {
                db.setRightProtocolSignDate(rightProtocolSignDate.toLocalDate());
            }
            LocalDateTime supProtocolDate = vo.getSupProtocolDate();
            if(supProtocolDate != null){
                db.setSupProtocolDate(supProtocolDate.toLocalDate());
            }
            LocalDateTime licenseValidTime = vo.getLicenseValidTime();
            if (licenseValidTime != null) {
                db.setLicenseValidTime(licenseValidTime.toLocalDate());
            }
            LocalDateTime actualBeneficiaryLicenseCodeDate = vo.getActualBeneficiaryLicenseCodeDate();
            if (actualBeneficiaryLicenseCodeDate != null){
                db.setActualBeneficiaryLicenseCodeDate(actualBeneficiaryLicenseCodeDate.toLocalDate());
            }

        });
        log.info("同步的客户信息="+ JSON.toJSONString(dbList));
        // 调用客户服务的保存或更新方法，并同时同步职务信息和银行卡信息
        boolean b1 = clientService.saveOrUpdateBatch(dbList);
        boolean b2 = syncDutyByYl(clientInfoListVo.getDutiesInfoList());
        boolean b3 = syncBankCardByYls(clientInfoListVo.getBankCardInfoList());
        return b1 && b2 && b3;
    }

    /**
     * 从YL系统同步职务信息到本地数据库
     * @param list YL系统人员信息列表
     * @return 同步是否成功
     */
    public Boolean syncDutyByYl(List<DutyInfoVo> list) {
        // 将YL系统职务信息列表转换为本地数据库实体类列表
        List<ClientDuty> dbList = CglibUtil.copyList(list, ClientDuty::new, (vo, db) -> {
            // 设置联系人类型id
            db.setContactTypeId(ContactType.convertRolesToKeys(vo.getContactTypes()));
            // 设置身份证类型
            db.setIdCardType(IDCardType.getByDescription(vo.getIdCardType()) != null ? IDCardType.getByDescription(vo.getIdCardType()).getCode() : null);

        });
        // 遍历本地数据库实体类列表，执行异步任务
        for (ClientDuty clientDuty : dbList) {
            if(StringUtils.isNotBlank(clientDuty.getIdCardNo())){
                // 提交任务到线程池
                asyncTaskExecutor.execute(() -> {
                    // 查询是否已存在相同身份证号的记录
                    long existingRecord = clientDutyService.getClientDutyByICardNo(clientDuty);
                    if (existingRecord >0) {
                        // 如果记录已存在，执行更新操作
                        clientDutyService.updateByIdCardNo(clientDuty);
                    } else {
                        // 如果记录不存在，执行插入操作
                        clientDutyService.add(clientDuty);
                    }
                });
            }
        }
        return true; // 同步操作成功
    }

    /**
     * 从YL系统同步银行卡信息到本地数据库
     *
     * @param list YL系统银行卡信息列表
     * @return 同步是否成功
     */
    public Boolean syncBankCardByYls(List<BankCardInfoYLVO> list) {
        // 将YL系统银行卡信息列表转换为本地数据库实体类列表
        List<BankCardInfo> dbList = CglibUtil.copyList(list, BankCardInfo::new, (vo, db) -> {
            try {
                // 设置本地数据库实体类的属性值
                db.setClientId(vo.getClientId());
                db.setAccountName(vo.getName());
                db.setOpenBank(vo.getBank());
                db.setBankAccount(vo.getCard());
                db.setLargeBankAccount(vo.getPayment());
                db.setPurpose(vo.getUsage());
                db.setRemark(vo.getComments());
                // 解析并设置有效状态
                int isEffective = "Valid".equalsIgnoreCase(vo.getValidState()) ? 0 : 1;
                db.setIsEffective(isEffective);
            } catch (NumberFormatException e) {
                // 处理NumberFormatException异常
                // 可以根据具体情况进行日志记录或其他处理
            }
        });
        // 遍历本地数据库实体类列表，执行异步任务
        for (BankCardInfo bankCardInfo : dbList) {
            if(StringUtils.isNotBlank(bankCardInfo.getBankAccount())) {
                // 提交任务到线程池
                asyncTaskExecutor.execute(() -> {
                    // 查询是否已存在相同银行卡号的记录
                    long existingRecord = bankCardInfoService.getBankCardInfoByBankAccount(bankCardInfo);
                    if (existingRecord > 0) {
                        // 如果记录已存在，执行更新操作
                        bankCardInfoService.updateByIdCardNo(bankCardInfo);
                    } else {
                        // 如果记录不存在，执行插入操作
                        bankCardInfoService.add(bankCardInfo);
                    }
                });
            }
        }
        return true; // 同步操作成功
    }

    @Override
    public Map<String, Integer> getClientMapByNameList(Set<String> nameSet) {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.in(Client::getName, nameSet);
        List<Client> list = clientService.list(lambdaQueryWrapper);
        return list.stream().collect(Collectors.toMap(Client::getName,Client::getId,(v1,v2)->v2));
    }

    @Override
    public List<Integer> getInsideClientIdList() {
        LambdaQueryWrapper<Client> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Client::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(Client::getIsInsided, 1);
        lambdaQueryWrapper.select(Client::getId);
        List<Client> list = clientService.list(lambdaQueryWrapper);
        return list.stream().map(Client::getId).collect(Collectors.toList());
    }
}
