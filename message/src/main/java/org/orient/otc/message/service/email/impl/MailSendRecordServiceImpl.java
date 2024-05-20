package org.orient.otc.message.service.email.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.feign.ClientDutyClient;
import org.orient.otc.api.message.dto.ReSendDto;
import org.orient.otc.api.message.dto.SendMailDto;
import org.orient.otc.api.message.enums.MailTypeEnum;
import org.orient.otc.api.quote.dto.BuildSettlementReportDTO;
import org.orient.otc.api.quote.dto.GetCapitalMonitorMailKeywordsConfigDto;
import org.orient.otc.api.quote.dto.GetSettlementReportMailKeywordsConfigDto;
import org.orient.otc.api.quote.feign.SettlementClient;
import org.orient.otc.api.quote.feign.SettlementReportClient;
import org.orient.otc.api.quote.vo.SettlementReportFileVO;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.message.dto.*;
import org.orient.otc.message.entity.MailSendRecord;
import org.orient.otc.message.entity.MailSendRecordDetail;
import org.orient.otc.message.entity.MailTemplate;
import org.orient.otc.message.mapper.MailSendRecordDetailMapper;
import org.orient.otc.message.mapper.MailSendRecordMapper;
import org.orient.otc.message.mapper.MailTemplateMapper;
import org.orient.otc.message.service.email.EmailServer;
import org.orient.otc.message.service.email.MailSendRecordService;
import org.orient.otc.message.service.email.MailTemplateService;
import org.orient.otc.message.service.email.RejectMailServer;
import org.orient.otc.message.vo.MailSendRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MailSendRecordServiceImpl extends ServiceImpl<MailSendRecordMapper, MailSendRecord> implements MailSendRecordService {
    @Autowired
    MailSendRecordMapper mailSendRecordMapper;
    @Autowired
    MailSendRecordDetailMapper mailSendRecordDetailMapper;
    @Resource
    ClientDutyClient clientDutyClient;
    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;
    @Autowired
    EmailServer emailServer;
    @Autowired
    MailTemplateService mailTemplateService;
    @Autowired
    MailTemplateMapper mailTemplateMapper;
    @Autowired
    SettlementClient settlementClient;
    @Autowired
    SettlementReportClient settlementReportClient;
    @Autowired
    ClientClient clientClient;
    @Autowired
    MailSendRecordService mailSendRecordService;
    @Autowired
    RejectMailServer rejectMailServer;
    @Autowired
    UserClient userClient;

    /**
     * 批量发送模板
     * 默认取客户所有联系人中, 愿意接收邮件的联系人邮箱
     * 追保金额>0 , 发送追保正文 , 邮件主题以【追保通知】开头
     * 追保金额<=0, 只发送普通正文
     * 结算报告附件生成的报告内容: 默认全部(账户状况, 持仓明细, 历史交易, 资金明细, 质押记录)
     * @param dto 入参
     * @return 返回操作信息
     */
    @Override
    public String multiSendMail(MultiSendMailDto dto) {
        AuthorizeInfo authorizeInfo = ThreadContext.getAuthorizeInfo();
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(attributes, true);
        ThreadContext.setAuthorizeInfo(authorizeInfo);
        for (Integer clientId : dto.getClientIdList()) {
            asyncTaskExecutor.execute(
                    () -> {
                        // 1. 获取模板
                        MailTemplate mailTemplate = mailTemplateService.getDefaultTemplate();
                        DoSendMailDto doSendMailDto = new DoSendMailDto();
                        doSendMailDto.setMailTemplate(mailTemplate);
                        doSendMailDto.setClientId(clientId);
                        doSendMailDto.setQueryDate(dto.getQueryDate());
                        doSendMailDto.setSendType(1);
                        doSendMailDto.setMailReSendRecord(null);
                        doSendMailDto.setStartDate(null);
                        doSendMailDto.setIsAppendMail(null);
                        doSendMailDto.setReportTypeSet(null);
                        doSendMailDto.setReceiveUserList(null);
                        doSendMailDto.setAuthorizeInfo(JSON.toJSONString(authorizeInfo));
                        mailSendRecordService.doSendMail(doSendMailDto);
                        //doSendMail(mailTemplate,clientId,dto.getQueryDate(),1,null,null,null,null,null);
                    }
            );
        }
        rejectMailServer.doRejectMailList();
        return "操作成功";
    }

    /**
     * 处理每个客户的邮件发送(批量发送 或 重发)
     * 重发不再记录发送失败记录 , 更新发送次数
     * 批量发送每次都记录
     */
    @Override
    //public void doSendMail(MailTemplate mailTemplate, Integer clientId,LocalDate queryDate, int sendType, MailSendRecord mailReSendRecord,LocalDate startDate,Integer isAppendMail,Set<String> reportTypeSet,List<String> receiveUserList){
    public void doSendMail(DoSendMailDto dto){
        MailTemplate mailTemplate = dto.getMailTemplate();
        Integer clientId = dto.getClientId();
        LocalDate queryDate = dto.getQueryDate();
        // -1:重发 , 0: 结算报告  ,  1:批量发送
        int sendType = dto.getSendType();
        // 重发使用
        MailSendRecordDetailDto mailReSendRecord = dto.getMailReSendRecord();
        LocalDate startDate = dto.getStartDate();
        Set<String> reportTypeSet = dto.getReportTypeSet();
        // 结算报告发送使用
        List<String> receiveUserList = dto.getReceiveUserList();
        // 2. 客户联系人邮箱列表
        Set<String> receiveMailSet = new HashSet<>();
        SendMailDto sendMailDto = new SendMailDto();
        sendMailDto.setMailTemplateId(mailTemplate.getId());
        sendMailDto.setAuthorizeInfo(dto.getAuthorizeInfo());
        sendMailDto.setMailType(MailTypeEnum.settleReport.getKey());
        sendMailDto.setClientId(clientId);
        // 邮件通配符结果
        Map<String,String> mailKeywordsMap = new HashMap<>();
        // 3. 生成附件并取回通配符结果
        if (sendType==-1) {
            // 重发 , 不再添加记录邮件发送记录
            if (StringUtils.isBlank(mailReSendRecord.getReceiveUserMailAddress())){
                //重发失败 , 不再记录发送的失败信息 ,更新重发次数
                mailSendRecordService.updateReSendCount(mailReSendRecord.getId(),mailReSendRecord.getReSendCount()+1,mailReSendRecord.getMailType().getKey());
                return;
            }
            // 重发的时候,直接取重发地址
            receiveMailSet.add(mailReSendRecord.getReceiveUserMailAddress());
            mailKeywordsMap = doReSend(sendMailDto,clientId,mailReSendRecord);
            log.info("重发通配符结果=" + JSON.toJSONString(mailKeywordsMap));
            sendMailDto.setReSend(true);
            if (mailReSendRecord.getMailType()==MailTypeEnum.settleReport) {
                sendMailDto.setMailType(MailTypeEnum.settleReportResend.getKey());
            } else if (mailReSendRecord.getMailType()==MailTypeEnum.capitalMonitor) {
                sendMailDto.setMailType(MailTypeEnum.capitalMonitorResend.getKey());
            } else {
                sendMailDto.setMailType(mailReSendRecord.getMailType().getKey());
            }
        } else if (sendType == 1){
            // 资金监控-批量发送
            sendMailDto.setMailType(MailTypeEnum.capitalMonitor.getKey());
            Map<String, Set<String>> clientDutyMap = clientDutyClient.getMapByClientId(clientId.toString());
            for (Set<String> itemValue : clientDutyMap.values()){
                receiveMailSet.addAll(itemValue);
            }
            if (CollectionUtils.isEmpty(receiveMailSet)){
                mailSendRecordService.add(sendMailDto,null);
                return;
            }
            // 批量发送时, 生成的结算报告不要质押记录sheet
            reportTypeSet = new HashSet<>();
            reportTypeSet.add("accountOverview");
            reportTypeSet.add("capital");
            reportTypeSet.add("accSummary");
            reportTypeSet.add("tradeRisk");
            reportTypeSet.add("historyTrade");
            mailKeywordsMap = multiSend(sendMailDto,clientId,queryDate,reportTypeSet);
            log.info("资金监控发送邮件通配符结果=" + JSON.toJSONString(mailKeywordsMap));
            sendMailDto.setReSend(false);
            buildReSendParams(startDate,queryDate,clientId,sendType,reportTypeSet,sendMailDto);
        } else if (sendType == 0){
            // 结算报告发送
            sendMailDto.setMailType(MailTypeEnum.settleReport.getKey());
            // 结算报告发送邮件使用页面选择的收件人列表
            receiveMailSet.addAll(receiveUserList);
            if (CollectionUtils.isEmpty(receiveMailSet)){
                mailSendRecordService.add(sendMailDto,null);
                return;
            }
            mailKeywordsMap = settlementReportSendMail(sendMailDto,clientId,queryDate,startDate,reportTypeSet);
            log.info("结算报告发送邮件通配符结果=" + JSON.toJSONString(mailKeywordsMap));
            sendMailDto.setReSend(false);
            buildReSendParams(startDate,queryDate,clientId,sendType,reportTypeSet,sendMailDto);
        }
        // 处理邮件主题和邮件正文
        doTitleAndContent(dto,mailKeywordsMap,sendMailDto);
        // 非重发邮件, 都需要替换邮件主题和正文中的通配符
        if (sendType != -1){
            // 4. 替换模板正文和追加正文中的通配符关键字
            replaceMailKeywords(dto,mailKeywordsMap,queryDate);
        }
        sendMailDto.setTitle(mailTemplate.getMailTitle());
        sendMailDto.setContent(mailTemplate.getMailContent());
        sendMailDto.setAppendContent(mailTemplate.getAppendContent());
        sendMailDto.setReceiveUserList(new ArrayList<>(receiveMailSet));
        // 校验邮箱格式, 格式错误的直接添加错误记录, 格式正确的继续发送
        checkMail(sendMailDto);
        boolean flag = emailServer.sendEMail(sendMailDto);
        if (sendType==-1) {
            if (!flag) {
                // 重发失败 , 更新发送次数, 更新发送类型
                mailSendRecordService.updateReSendCount(mailReSendRecord.getId(),mailReSendRecord.getReSendCount()+1,sendMailDto.getMailType());
            } else {
                // 重发成功 , 更新状态, 发送次数, 更新发送类型
                mailSendRecordService.updateReSendStatusAndCount(mailReSendRecord.getId(),-1,mailReSendRecord.getReSendCount()+1,sendMailDto.getMailType());
            }
        }
    }

    /**
     * 处理邮件标题和正文
     * 根据追保金额是否大于0, 或结算报告中手动选择的邮件类型, 判断是否需要发送追保邮件, 处理邮件主题和邮件正文
     * @param dto               邮件发送页面入参
     * @param mailKeywordsMap   通配符结果map
     * @param sendMailDto       发送参数dto
     */
    public void doTitleAndContent(DoSendMailDto dto,Map<String,String> mailKeywordsMap,SendMailDto sendMailDto){
        MailTemplate mailTemplate = dto.getMailTemplate();
        MailSendRecordDetailDto mailReSendRecord = dto.getMailReSendRecord();
        int sendType = dto.getSendType();
        // 结算报告发送中使用(由于是页面是可选的)
        Integer isAppendMail = dto.getIsAppendMail();
        // 邮件主题
        String title = mailTemplate.getMailTitle();
        // 正文
        String content = mailTemplate.getMailContent();
        // 追保正文
        String appendContent = mailTemplate.getAppendContent();

        if (sendType == 0){
            // 结算报告中,客户自己选择是否发送追保邮件
            if (isAppendMail==1) {
                title = "【追保通知】"+title;
                content = ""; // 清空普通正文
            } else {
                appendContent = ""; // 清空追保正文
            }
        } else if (sendType == 1) { // 资金监控中批量发送时, 根据当前客户的追保金额来区分是否发送追保邮件
            if (mailKeywordsMap.containsKey("追保金额")) {
                BigDecimal additionalPriceTemp = new BigDecimal(mailKeywordsMap.get("追保金额"));
                // 追保金额大于0
                if (additionalPriceTemp.compareTo(BigDecimal.ZERO) > 0) {
                    title = "【追保通知】"+title;
                    // 清空普通正文
                    content = "";
                } else {
                    // 清空追保正文
                    appendContent = "";
                }
            }
        } else  if (sendType == -1) {
            sendMailDto.setReSendMailId(mailReSendRecord.getId());
            // 重发时 , 邮件内容不变, 主题不变
            title = mailReSendRecord.getTitle();
            content = mailReSendRecord.getEmailContent();
            appendContent = "";
        }
        mailTemplate.setMailTitle(title);
        mailTemplate.setMailContent(content);
        mailTemplate.setAppendContent(appendContent);
    }
    /**
     * 替换通配符
     * @param dto               邮件发送页面入参
     * @param mailKeywordsMap   通配符结果map
     * @param queryDate         查询日期
     */
    public void replaceMailKeywords(DoSendMailDto dto,Map<String,String> mailKeywordsMap,LocalDate queryDate){
        MailTemplate mailTemplate = dto.getMailTemplate();
        // 邮件主题
        String title = mailTemplate.getMailTitle();
        // 正文
        String content = mailTemplate.getMailContent();
        // 追保正文
        String appendContent = mailTemplate.getAppendContent();

        for (Map.Entry<String,String> entry : mailKeywordsMap.entrySet()) {
            String mailKeywords = entry.getKey();
            String value = entry.getValue();
            // 结束日期不做替换,因为通配符中的不是真实的日期, 要以入参中的结束日期/查询日期为准
            if ("结束日期".equals(mailKeywords)){
                continue;
            }
            String regex = "\\{\\{"+mailKeywords+"\\}\\}";
            if (StringUtils.isNotBlank(title)) {
                title = title.replaceAll(regex,value);
            }
            if (StringUtils.isNotBlank(content)) {
                content = content.replaceAll(regex,value);
            }
            if (StringUtils.isNotBlank(appendContent)) {
                appendContent = appendContent.replaceAll(regex,value);
            }

        }
        // 结束日期的真实数据
        if (StringUtils.isNotBlank(title)) {
            String regex = "\\{\\{"+"结束日期"+"\\}\\}";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date = queryDate.format(formatter);
            title = title.replaceAll(regex,date);
        }
        mailTemplate.setMailTitle(title);
        mailTemplate.setMailContent(content);
        mailTemplate.setAppendContent(appendContent);
    }

    /**
     * 校验邮箱格式
     * 格式错误的邮件格式记录入库,
     * 格式正确进行邮件发送
     * @param sendMailDto 发送邮件入参
     */
    public void checkMail(SendMailDto sendMailDto){
        if (!CollectionUtils.isEmpty(sendMailDto.getReceiveUserList())){
            String mailPattern ="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern mailRegex = Pattern.compile(mailPattern);
            // 格式错误邮箱
            List<String> errorMailList = new ArrayList<>();
            List<String> receiveUserList = sendMailDto.getReceiveUserList();
            for (String mail: receiveUserList){
                boolean flag = mailRegex.matcher(mail).matches();
                // 邮箱地址不合法 , 直接记录错误信息
                if (!flag){
                    errorMailList.add(mail);
                }
            }
            if (!errorMailList.isEmpty()) {
                // 收件人邮箱格式全部不正确
                if (errorMailList.size()==receiveUserList.size()){
                    mailSendRecordService.addSendFailRecord(sendMailDto,errorMailList,"邮件格式不正确",true);
                    throw new RuntimeException("收件人邮箱格式全部不正确");
                } else {
                    // 部分格式不正确
                    receiveUserList.removeAll(errorMailList);
                    mailSendRecordService.addSendFailRecord(sendMailDto,errorMailList,"邮件格式不正确",false);
                }
            }
            sendMailDto.setReceiveUserList(receiveUserList);
        }
    }

    /**
     * 构建邮件发送参数 用于重发
     * @param startDate 开始日期
     * @param queryDate 结束日期
     * @param clientId 客户ID
     * @param sendType 发送类型
     * @param reportTypeSet 报告内容
     */
    private void buildReSendParams(LocalDate startDate, LocalDate queryDate, Integer clientId, int sendType, Set<String> reportTypeSet,SendMailDto sendMailDto) {
        ReSendDto reSendDto = new ReSendDto();
        reSendDto.setStartDate(startDate);
        reSendDto.setEndDate(queryDate);
        reSendDto.setClientId(clientId);
        // 资金监控批量发送,选择的结算报告内容默认全部(不带质押)
        if (sendType==1) {
            reportTypeSet.remove("collateral");
            reSendDto.setReportTypeSet(reportTypeSet);
        } else if (sendType==0){
            // 结算报告
            reSendDto.setReportTypeSet(reportTypeSet);
            reSendDto.setMailTemplateId(sendMailDto.getMailTemplateId());
        }
        sendMailDto.setReSendParams(JSON.toJSONString(reSendDto));
    }


    /**
     * 处理重发
     * 生成重发的结算报告附件和获取通配符结果
     * @param sendMailDto 发送参数
     * @param clientId 客户ID
     * @param mailSendRecord 邮件记录obj
     * @return  通配符结果
     */
    public Map<String,String> doReSend(SendMailDto sendMailDto, Integer clientId,  MailSendRecordDetailDto mailSendRecord){
        Map<String,String> mailKeywordsMap;
        // 生成结算报告所需参数
        BuildSettlementReportDTO buildSettlementReportDTO = new BuildSettlementReportDTO();
        // 邮件发送参数json串
        String reSendParam = mailSendRecord.getReSendParam();
        ReSendDto reSendDto = JSON.parseObject(reSendParam, ReSendDto.class);
        // 获取当前客户的结算报告附件
        BeanUtils.copyProperties(reSendDto,buildSettlementReportDTO);
        MailTypeEnum mailType = mailSendRecord.getMailType();
        if (MailTypeEnum.settleReport == mailType) {
            // 结算报告重发
            sendMailDto.setMailType(MailTypeEnum.settleReportResend.getKey());
            GetSettlementReportMailKeywordsConfigDto dto = new GetSettlementReportMailKeywordsConfigDto();
            dto.setClientId(clientId);
            dto.setStartDate(reSendDto.getStartDate());
            dto.setEndDate(reSendDto.getEndDate());
            buildSettlementReportDTO.setStartDate(reSendDto.getStartDate());
            buildSettlementReportDTO.setEndDate(reSendDto.getEndDate());
            // 获取结算报告中邮件通配符结果
            mailKeywordsMap = settlementClient.getSettlementReportMailKeywordsConfig(dto);
        } else if (MailTypeEnum.capitalMonitor == mailType) {
            // 资金监控重发
            sendMailDto.setMailType(MailTypeEnum.capitalMonitorResend.getKey());
            GetCapitalMonitorMailKeywordsConfigDto dto = new GetCapitalMonitorMailKeywordsConfigDto();
            dto.setClientId(clientId);
            dto.setQueryDate(reSendDto.getEndDate());
            // 获取资金报告中邮件通配符结果
            mailKeywordsMap = settlementClient.getCapitalMonitorMailKeywordsConfig(dto);
            // 资金监控中发送邮件,生成结算报告时, 结束日期=开始日期(查询日期)
            buildSettlementReportDTO.setStartDate(reSendDto.getEndDate());
        } else {
            // 发送失败, 读取退信之后, 发送类型改为回退, 或者重发时候, 发送类型变更
            GetSettlementReportMailKeywordsConfigDto dto = new GetSettlementReportMailKeywordsConfigDto();
            if (reSendDto.getStartDate()!=null){
                buildSettlementReportDTO.setStartDate(reSendDto.getStartDate());
                dto.setStartDate(reSendDto.getStartDate());
            } else {
                buildSettlementReportDTO.setStartDate(reSendDto.getEndDate());
                dto.setStartDate(reSendDto.getEndDate());
            }
            dto.setClientId(clientId);
            dto.setEndDate(reSendDto.getEndDate());
            buildSettlementReportDTO.setEndDate(reSendDto.getEndDate());
            // 获取结算报告中邮件通配符结果
            mailKeywordsMap = settlementClient.getSettlementReportMailKeywordsConfig(dto);
        }
        buildSettlementReportDTO.setEndDate(reSendDto.getEndDate());
        buildSettlementReportDTO.setAuthorizeInfo(sendMailDto.getAuthorizeInfo());
        log.info("重发-生成附件入参"+JSON.toJSONString(buildSettlementReportDTO));
        SettlementReportFileVO settlementReportFileVO = settlementReportClient.getSettlementReportTempFileByClient(buildSettlementReportDTO);
        //MultipartFile multipartFile = FileUtil.file2MultipartFile(settlementReportFileVO.getSettlementReportFile());
        sendMailDto.setSettlementReportTempFileByte(settlementReportFileVO.getSettlementReportTempFileByte());
        sendMailDto.setTempFileName(settlementReportFileVO.getTempFileName());
        return mailKeywordsMap;
    }

    /**
     * 处理资金记录的批量发送
     * 生成资金记录的结算报告附件和获取通配符结果
     * @param sendMailDto 发送参数
     * @param clientId 客户ID
     * @param queryDate 查询日期
     */
    public Map<String,String>  multiSend(SendMailDto sendMailDto, Integer clientId, LocalDate queryDate,Set<String> reportTypeSet){
        Map<String,String> mailKeywordsMap;
        BuildSettlementReportDTO buildSettlementReportDTO = new BuildSettlementReportDTO();
        buildSettlementReportDTO.setClientId(clientId);
        buildSettlementReportDTO.setEndDate(queryDate);
        // 资金监控中发送邮件,生成结算报告时, 结束日期=开始日期(查询日期)
        buildSettlementReportDTO.setStartDate(queryDate);
        buildSettlementReportDTO.setReportTypeSet(reportTypeSet);
        buildSettlementReportDTO.setAuthorizeInfo(sendMailDto.getAuthorizeInfo());
        SettlementReportFileVO settlementReportFileVO = settlementReportClient.getSettlementReportTempFileByClient(buildSettlementReportDTO);
        //MultipartFile multipartFile = FileUtil.file2MultipartFile(settlementReportFileVO.getSettlementReportFile());
        sendMailDto.setSettlementReportTempFileByte(settlementReportFileVO.getSettlementReportTempFileByte());
        sendMailDto.setTempFileName(settlementReportFileVO.getTempFileName());
        // 组装通配符
        GetCapitalMonitorMailKeywordsConfigDto dto = new GetCapitalMonitorMailKeywordsConfigDto();
        dto.setClientId(clientId);
        dto.setQueryDate(queryDate);
        //  取每个客户信息对应的通配符结果
        mailKeywordsMap = settlementClient.getCapitalMonitorMailKeywordsConfig(dto);
        return mailKeywordsMap;
    }

    /**
     *  结算报告发送邮件
     * 生成资金记录的结算报告附件和获取通配符结果
     * @param sendMailDto 邮件发送参数
     * @param clientId 客户ID
     * @param queryDate 查询日期
     * @param startDate 开始日期
     * @param reportTypeSet 结算报告类型
     * @return 返回通配符结果
     */
    public Map<String,String> settlementReportSendMail(SendMailDto sendMailDto, Integer clientId, LocalDate queryDate, LocalDate startDate,Set<String> reportTypeSet){
        Map<String,String> mailKeywordsMap;
        GetSettlementReportMailKeywordsConfigDto dto = new GetSettlementReportMailKeywordsConfigDto();
        dto.setClientId(clientId);
        dto.setStartDate(startDate);
        dto.setEndDate(queryDate);
        //  取每个客户信息对应的通配符结果
        mailKeywordsMap = settlementClient.getSettlementReportMailKeywordsConfig(dto);
        // 生成结算报告所需参数
        BuildSettlementReportDTO buildSettlementReportDTO = new BuildSettlementReportDTO();
        buildSettlementReportDTO.setStartDate(startDate);
        buildSettlementReportDTO.setEndDate(queryDate);
        buildSettlementReportDTO.setClientId(clientId);
        buildSettlementReportDTO.setReportTypeSet(reportTypeSet);
        buildSettlementReportDTO.setAuthorizeInfo(sendMailDto.getAuthorizeInfo());
        SettlementReportFileVO settlementReportFileVO = settlementReportClient.getSettlementReportTempFileByClient(buildSettlementReportDTO);
        //MultipartFile multipartFile = FileUtil.file2MultipartFile(settlementReportFileVO.getSettlementReportFile());
        sendMailDto.setSettlementReportTempFileByte(settlementReportFileVO.getSettlementReportTempFileByte());
        sendMailDto.setTempFileName(settlementReportFileVO.getTempFileName());
        return mailKeywordsMap;
    }

    /**
     * 新增邮件发送记录
     * @param dto 邮件发送入参
     * @param receiveUserList 收件人地址
     */
    @Override
    public void add(SendMailDto dto,List<String> receiveUserList) {
        try {
            Integer parentId = null;
            if (dto.getErrorMailRecordId() == null) {
                // 新增邮件记录主表
                MailSendRecord mailSendRecordEntity = new MailSendRecord();
                AuthorizeInfo authorizeInfo = ThreadContext.getAuthorizeInfo();
                if (authorizeInfo==null) {
                    authorizeInfo  = JSON.parseObject(dto.getAuthorizeInfo(),AuthorizeInfo.class);
                    ThreadContext.setAuthorizeInfo(authorizeInfo);
                }
                mailSendRecordEntity.setSendUserId(authorizeInfo.getId());
                mailSendRecordEntity.setEmailContent(dto.getContent()+dto.getAppendContent());
                mailSendRecordEntity.setClientId(dto.getClientId());
                // 发送中
                mailSendRecordEntity.setSendStatus(-1);
                mailSendRecordEntity.setMailType(MailTypeEnum.getByKey(dto.getMailType()));
                mailSendRecordEntity.setTitle(dto.getTitle());
                mailSendRecordEntity.setReSendParam(dto.getReSendParams());
                mailSendRecordMapper.insert(mailSendRecordEntity);
                parentId = mailSendRecordEntity.getId();
            } else {
                parentId = dto.getErrorMailRecordId();
            }

            // 新增邮件记录子表
            MailSendRecordDetail detail = new MailSendRecordDetail();
            detail.setParentId(parentId);
            if(!CollectionUtils.isEmpty(receiveUserList)){
                for (String mail : receiveUserList){
                    detail.setId(null);
                    detail.setReceiveUserMailAddress(mail);
                    detail.setMailType(MailTypeEnum.getByKey(dto.getMailType()));
                    // 发送中
                    detail.setSendStatus(-1);
                    mailSendRecordDetailMapper.insert(detail);
                }
            } else {
                detail.setId(null);
                detail.setSendFailDesc("收件人为空");
                detail.setSendStatus(1);
                detail.setMailType(MailTypeEnum.getByKey(dto.getMailType()));
                mailSendRecordDetailMapper.insert(detail);
            }
        } catch (Exception e) {
            log.error("添加邮件发送记录失败; ="+e.getMessage());
        }
    }

    /**
     * 添加邮件发送记录(失败)
     * @param dto 发送参数
     * @param errorMailList 格式错误邮箱
     * @param failDesc 失败原因
     * @param allFail 收件人格式是否全部错误
     */
    @Override
    public void addSendFailRecord(SendMailDto dto, List<String> errorMailList, String failDesc, boolean allFail) {
        // 新增邮件记录主表
        MailSendRecord mailSendRecordEntity = new MailSendRecord();
        AuthorizeInfo authorizeInfo = ThreadContext.getAuthorizeInfo();
        if (authorizeInfo!=null) {
            mailSendRecordEntity.setSendUserId(authorizeInfo.getId());
        } else {
            mailSendRecordEntity.setSendUserId(1);
        }
        mailSendRecordEntity.setEmailContent(dto.getContent()+dto.getAppendContent());
        mailSendRecordEntity.setClientId(dto.getClientId());
        if (allFail) {
            // 收件人格式全错
            mailSendRecordEntity.setSendStatus(1);
        } else {
            // 部分错误
            mailSendRecordEntity.setSendStatus(-1);
        }
        mailSendRecordEntity.setMailType(MailTypeEnum.getByKey(dto.getMailType()));
        mailSendRecordEntity.setTitle(dto.getTitle());
        mailSendRecordEntity.setReSendParam(dto.getReSendParams());
        mailSendRecordMapper.insert(mailSendRecordEntity);
        // 回填ID
        dto.setErrorMailRecordId(mailSendRecordEntity.getId());
        // 新增邮件记录子表
        MailSendRecordDetail detail = new MailSendRecordDetail();
        detail.setParentId(mailSendRecordEntity.getId());
        if(!CollectionUtils.isEmpty(errorMailList)){
            for (String mail : errorMailList){
                detail.setId(null);
                detail.setReceiveUserMailAddress(mail);
                detail.setMailType(MailTypeEnum.getByKey(dto.getMailType()));
                // 发送中
                detail.setSendStatus(1);
                detail.setSendFailDesc("邮件格式不正确");
            }
            mailSendRecordDetailMapper.insert(detail);
        }

    }

    @Override
    public IPage<MailSendRecordVO> selectListByPage(MailSendRecordPageDto dto) {
        LambdaQueryWrapper<MailSendRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 收件箱
        if (StringUtils.isNotBlank(dto.getReceiveUserMailAddress())) {
            LambdaQueryWrapper<MailSendRecordDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.eq(MailSendRecordDetail::getIsDeleted,IsDeletedEnum.NO);
            detailLambdaQueryWrapper.like(MailSendRecordDetail::getReceiveUserMailAddress,dto.getReceiveUserMailAddress());
            Set<Integer> parentIds = mailSendRecordDetailMapper.selectList(detailLambdaQueryWrapper).stream().map(item->item.getParentId()).collect(Collectors.toSet());
            lambdaQueryWrapper.in(MailSendRecord::getId,parentIds);
        }
        if (dto.getCreateTime() != null){
            LocalDateTime start = dto.getCreateTime().atTime(0,0,0);
            LocalDateTime end = dto.getCreateTime().atTime(23,59,59);
            lambdaQueryWrapper.le(MailSendRecord::getCreateTime,end);
            lambdaQueryWrapper.ge(MailSendRecord::getCreateTime,start);
        }
        lambdaQueryWrapper.eq(MailSendRecord::getIsDeleted, IsDeletedEnum.NO)
                .eq(dto.getClientId()!=null,MailSendRecord::getClientId,dto.getClientId())
                .eq(dto.getMailType()!=null,MailSendRecord::getMailType,dto.getMailType())
                .eq(dto.getSendStatus()!=null,MailSendRecord::getSendStatus,dto.getSendStatus())
                .in(!CollectionUtils.isEmpty(dto.getSendUserIdList()),MailSendRecord::getSendUserId,dto.getSendUserIdList())
                .orderByDesc(MailSendRecord::getCreateTime);
        IPage<MailSendRecord> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        // 客户ID集合
        Set<Integer> clientSet = ipage.getRecords().stream().map(MailSendRecord::getClientId).collect(Collectors.toSet());
        Map<Integer,String> clientMap =  clientClient.getClientMapByIds(clientSet);
        // 发件人ID集合
        Set<Integer> sendUserIdSet = ipage.getRecords().stream().map(MailSendRecord::getSendUserId).collect(Collectors.toSet());
        Map<Integer, String> userMap = userClient.getUserMapByIds(sendUserIdSet);
        List<MailSendRecordDetail> detailList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(ipage.getRecords())) {
            // 查询邮件发送记录子表
            Set<Integer> idSet = ipage.getRecords().stream().map(MailSendRecord::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<MailSendRecordDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.in(MailSendRecordDetail::getParentId,idSet);
            detailLambdaQueryWrapper.eq(MailSendRecordDetail::getIsDeleted,IsDeletedEnum.NO);
            if (StringUtils.isNotBlank(dto.getReceiveUserMailAddress())) {
                detailLambdaQueryWrapper.like(MailSendRecordDetail::getReceiveUserMailAddress,dto.getReceiveUserMailAddress());
            }
            detailList = mailSendRecordDetailMapper.selectList(detailLambdaQueryWrapper);
        }
        // key=发送记录主表ID, value=发送记录子表细信息
        Map<Integer,List<MailSendRecordDetail>> detailMap = detailList.stream().collect(Collectors.groupingBy(MailSendRecordDetail::getParentId));
        return ipage.convert(entity->{
            MailSendRecordVO vo = new MailSendRecordVO();
            BeanUtils.copyProperties(entity,vo);
            vo.setClientName(clientMap.get(entity.getClientId()));
            vo.setSendUserName(userMap.get(entity.getSendUserId()));
            vo.setDetailList(detailMap.get(entity.getId()));
            return vo;
        });
    }

    @Override
    public String reSend(ReSendMailDto dto) {
        AuthorizeInfo authorizeInfo = ThreadContext.getAuthorizeInfo();

        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(attributes, true);
        ThreadContext.setAuthorizeInfo(authorizeInfo);

        LambdaQueryWrapper<MailSendRecordDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MailSendRecordDetail::getIsDeleted,IsDeletedEnum.NO)
                .in(MailSendRecordDetail::getId,dto.getId())
                // 重发次数<3次
                .lt(MailSendRecordDetail::getReSendCount,3);
        // 获取重发邮件列表
        List<MailSendRecordDetail> list = mailSendRecordDetailMapper.selectList(lambdaQueryWrapper);
        // 邮件记录主表信息 key=ID , value=obj
        Map<Integer,MailSendRecord> parentMap;
        if (!list.isEmpty()){
            Set<Integer> parentIDSet = list.stream().map(MailSendRecordDetail::getParentId).collect(Collectors.toSet());
            List<MailSendRecord> parentList = mailSendRecordMapper.selectBatchIds(parentIDSet);
            parentMap = parentList.stream().collect(Collectors.toMap(MailSendRecord::getId, item->item,(v1, v2)->v2));
        } else {
            parentMap = null;
        }
        for (MailSendRecordDetail entity : list) {
            asyncTaskExecutor.execute(
                    () -> {
                        // 1. 获取模板
                        MailTemplate mailTemplate = mailTemplateService.getDefaultTemplate();
                        String reSendParam = null;
                        MailSendRecord mailSendRecord = null;
                        if (parentMap.containsKey(entity.getParentId())) {
                            mailSendRecord = parentMap.get(entity.getParentId());
                            reSendParam = mailSendRecord.getReSendParam();
                        }
                        if (StringUtils.isNotBlank(reSendParam) && !"null".equalsIgnoreCase(reSendParam)) {
                            ReSendDto reSendDto = JSON.parseObject(reSendParam, ReSendDto.class);
                            // 重发操作中, 重发参数中的模板ID不为空, 要使用重发参数中的模板
                            if (reSendDto.getMailTemplateId() != null) {
                                mailTemplate = mailTemplateMapper.selectById(reSendDto.getMailTemplateId());
                            }
                            // 邮件记录详情
                            MailSendRecordDetailDto mailSendRecordDetailDto = new MailSendRecordDetailDto();
                            BeanUtils.copyProperties(entity, mailSendRecordDetailDto);
                            mailSendRecordDetailDto.setTitle(mailSendRecord.getTitle());
                            mailSendRecordDetailDto.setEmailContent(mailSendRecord.getEmailContent());
                            mailSendRecordDetailDto.setReSendParam(mailSendRecord.getReSendParam());
                            // 重发参数
                            DoSendMailDto doSendMailDto = new DoSendMailDto();
                            doSendMailDto.setMailTemplate(mailTemplate);
                            doSendMailDto.setClientId(mailSendRecord.getClientId());
                            doSendMailDto.setQueryDate(null);
                            doSendMailDto.setSendType(-1);
                            doSendMailDto.setMailReSendRecord(mailSendRecordDetailDto);
                            doSendMailDto.setStartDate(null);
                            doSendMailDto.setIsAppendMail(null);
                            doSendMailDto.setReportTypeSet(null);
                            doSendMailDto.setReceiveUserList(null);
                            doSendMailDto.setAuthorizeInfo(JSON.toJSONString(authorizeInfo));
                            mailSendRecordService.doSendMail(doSendMailDto);
                            //doSendMail(mailTemplate,entity.getClientId(),null,-1 , entity,null,null,null,null);

                        }
                    }
            );
        }
        rejectMailServer.doRejectMailList();
        return "操作成功";
    }

    @Override
    public void updateReSendCount(Integer id, int reSendCount,String mailType) {
        MailSendRecordDetail entity = new MailSendRecordDetail();
        entity.setId(id);
        entity.setReSendCount(reSendCount);
        entity.setMailType(MailTypeEnum.getByKey(mailType));
        mailSendRecordDetailMapper.updateById(entity);
    }

    @Override
    public void updateReSendStatusAndCount(Integer id, int sendStatus, int reSendCount,String mailType) {
        MailSendRecordDetail entity = new MailSendRecordDetail();
        entity.setId(id);
        entity.setSendStatus(sendStatus);
        entity.setReSendCount(reSendCount);
        entity.setMailType(MailTypeEnum.getByKey(mailType));
        mailSendRecordDetailMapper.updateById(entity);
    }

    @Override
    public void updateReSendMessageId(Integer id, String messageId) {
        MailSendRecord dbEntity = mailSendRecordMapper.selectById(id);
        if (dbEntity != null){
            MailSendRecord entity = new MailSendRecord();
            entity.setId(id);
            entity.setSendStatus(-1);
           // entity.setReSendCount(dbEntity.getReSendCount()+1);
            entity.setMailType(MailTypeEnum.back);
            //entity.setMessageId(messageId);
            entity.setCreateTime(LocalDateTime.now());
            mailSendRecordMapper.updateById(entity);
        }
    }
}
