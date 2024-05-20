package org.orient.otc.message.service.email.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.message.enums.MailTypeEnum;
import org.orient.otc.message.entity.MailSendRecord;
import org.orient.otc.message.entity.MailSendRecordDetail;
import org.orient.otc.message.mapper.MailSendRecordDetailMapper;
import org.orient.otc.message.mapper.MailSendRecordMapper;
import org.orient.otc.message.service.email.RejectMailServer;
import org.orient.otc.message.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.search.SearchTerm;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Slf4j
public class RejectMailServerImpl implements RejectMailServer {
    @Autowired
    MailSendRecordMapper mailSendRecordMapper;
    @Autowired
    MailSendRecordDetailMapper mailSendRecordDetailMapper;
    @Value("${spring.rejectMail.protocol}")
    String protocol;
    @Value("${spring.rejectMail.user}")
    String user;
    @Value("${spring.rejectMail.host}")
    String host; // 邮件服务器的SMTP地址
    @Value("${spring.rejectMail.portKey}")
    String portKey; // 端口
    @Value("${spring.rejectMail.portValue}")
    String portValue; // 端口
    @Value("${spring.rejectMail.password}")
    String password; // 授权码
    @Value("${spring.rejectMail.rejectFloder}")
    String rejectFloder; // 退件文件夹
    @Value("${spring.rejectMail.systemMail}")
    String systemMail; // 系统发送退件邮件地址
    @Value("${spring.rejectMail.intervalSecond}")
    Long intervalSecond; // 间隔多久读取退件
    @Value("${spring.mail.port}")
    Integer port; // 端口
    @Value("${spring.mail.from}")
    String from; // 发件人邮箱


    /**
     * 获取系统退件
     * 获取前一天之内的退件
     * @throws Exception 异常
     */
    @Override
    public void doRejectMailList(){
        Timer timer = new Timer();
        // 1小时之后处理
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    asyncDoRejectMail();
                } catch (Exception e) {
                    log.error("处理系统退件信息="+e.getMessage());
                }
            }
        }, intervalSecond);
    }

    /**
     * 登录企业邮箱, 并返回session
     * @return 返回session
     */
    public Session getSession(){
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", Integer.toString(port));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.transport.protocol", "smtp");
        // 企业邮箱
        if (from.contains("dzrhotc")) {
            // 企业邮箱必须使用SSL认证---start
            // 开启安全协议
            MailSSLSocketFactory mailSSLSocketFactory = null;
            try {
                mailSSLSocketFactory = new MailSSLSocketFactory();
                mailSSLSocketFactory.setTrustAllHosts(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            properties.put("mail.smtp.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.put("mail.smtp.socketFactory.port", port);
            // 企业邮箱必须使用SSL认证---end
        }
        Session session = Session.getInstance(properties, new Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        return session;
    }
    @Override
    public void asyncDoRejectMail() throws Exception {
        // 建立连接
        Properties props = new Properties();
        Session session;
        // 企业邮箱
        if (from.contains("dzrhotc")) {
            session = getSession();
        } else {
            // 邮件服务器端口
            props.setProperty(portKey, portValue);
            session = Session.getDefaultInstance(props);
        }
        Store store = session.getStore(protocol);
        // 连接邮件服务器，填写对应的账号和密码
        store.connect( host, user, password);
        // 登录账号
        boolean loggedIn = store.isConnected();
        if (loggedIn) {
            System.out.println("登录成功！");
        } else {
            System.out.println("登录失败！");
        }
        Folder defaultFolder = store.getDefaultFolder();
        Folder[] folders = defaultFolder.list(rejectFloder);
        for (Folder folder : folders) {
            IMAPFolder imapFolder = (IMAPFolder) folder;
            //javamail中使用id命令有校验checkOpened, 所以要去掉id方法中的checkOpened(); 否则报错Ax NO EXAMINE Unsafe Login. Please contact kefu@188.com for help
            imapFolder.doCommand(p -> {
                Map<String, String> gmap = new HashMap<>();
                gmap.put("GUID", "FUTONG");
                p.id(gmap);
                return null;
            });
            imapFolder.open(Folder.READ_ONLY);
            // 获取系统退件
            SearchTerm searchTerm = new SearchTerm() {
                @Override
                public boolean match(Message message) {
                    boolean flag;
                    try {
                        // 发送时间
                        Date sendDate = message.getSentDate();
                        // 系统发送的退件
                        boolean systemSendUseMail = message.getFrom()[0].toString().contains(systemMail);
                        // 获取前一天之内的退件
                        flag = sendDate.after(DateUtil.getPreHour()) && systemSendUseMail;
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                    return flag;
                }
            };
            // 获取收件箱中的所有退件邮件 , 根据发送日期倒序
            List<Message> rejectMessageList = Arrays.stream(folder.search(searchTerm)).sorted(((o1, o2) -> {
                try {
                    boolean flag = o1.getSentDate().after(o2.getSentDate());
                    return flag?-1:1;
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            })).collect(Collectors.toList());
            // 获取当前系统中的发送记录
            LambdaQueryWrapper<MailSendRecordDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.eq(MailSendRecordDetail::getIsDeleted,0);
            detailLambdaQueryWrapper.ge(MailSendRecordDetail::getCreateTime,DateUtil.getPreHour());
            detailLambdaQueryWrapper.orderByDesc(MailSendRecordDetail::getCreateTime);
            List<MailSendRecordDetail> detailList = mailSendRecordDetailMapper.selectList(detailLambdaQueryWrapper);
            // 退件列表
            List<MailSendRecordDetail> backMailList = new ArrayList<>();
            // 处理退件信息
            if (CollectionUtils.isNotEmpty(rejectMessageList)){
                if (!detailList.isEmpty()){
                    backMailList = doRejectMail(rejectMessageList,detailList);
                }
            }
            // 邮件记录状态处理
            doMailRecordStatus(detailList,backMailList);
            // 关闭连接
            imapFolder.close();
            store.close();
        }
        // 关闭连接对象，释放资源
        store.close();
    }

    /**
     * 处理邮件记录状态
     * @param detailList   邮件记录详情
     * @param backMailList 退件详情
     */
    private void doMailRecordStatus(List<MailSendRecordDetail> detailList, List<MailSendRecordDetail> backMailList) {
        String mailPattern ="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern mailRegex = Pattern.compile(mailPattern);
        // 邮件为空的
        List<MailSendRecordDetail> mailEmptyList = detailList.stream().filter(item->StringUtils.isBlank(item.getReceiveUserMailAddress())).collect(Collectors.toList());
        // 邮件不为空的(包含退信)
        List<MailSendRecordDetail> mailNotEmptyList = detailList.stream().filter(item->StringUtils.isNotBlank(item.getReceiveUserMailAddress())).collect(Collectors.toList());
        // 非空邮箱且, 邮件不合格
        List<MailSendRecordDetail> mailNotStantList = detailList.stream().filter(item->StringUtils.isNotBlank(item.getReceiveUserMailAddress()) && !mailRegex.matcher(item.getReceiveUserMailAddress()).matches()).collect(Collectors.toList());
        // 更新邮件记录主表条件
        LambdaUpdateWrapper<MailSendRecord> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        // 退信邮件 按父ID分组
        Map<Integer,List<MailSendRecordDetail>> backDetailMap = new HashMap<>();
        Map<Integer,List<MailSendRecordDetail>> mailEmptyMap = mailEmptyList.stream().collect(Collectors.groupingBy(MailSendRecordDetail::getParentId));
        Map<Integer,List<MailSendRecordDetail>> mailNotStantMap = mailNotStantList.stream().collect(Collectors.groupingBy(MailSendRecordDetail::getParentId));
        // 全量邮件发送记录
        Map<Integer,List<MailSendRecordDetail>> detailMap = detailList.stream().collect(Collectors.groupingBy(MailSendRecordDetail::getParentId));
        if (CollectionUtils.isNotEmpty(backMailList)) {
            // 去重
            List<MailSendRecordDetail> removeRepetList = backMailList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MailSendRecordDetail::getId))), ArrayList::new));
            // 退信邮件 按父ID分组
            backDetailMap = removeRepetList.stream().collect(Collectors.groupingBy(MailSendRecordDetail::getParentId));
        }
        for (Map.Entry<Integer,List<MailSendRecordDetail>> entry : detailMap.entrySet()) {
            lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            List<MailSendRecordDetail> list = entry.getValue();
            Integer key = entry.getKey();
            // 某个主记录的退件列表
            List<MailSendRecordDetail> backList = new ArrayList<>();
            // 某个主记录的空收件箱列表
            List<MailSendRecordDetail> emptyList = new ArrayList<>();
            // 某个主记录的不合格邮箱列表
            List<MailSendRecordDetail> notStantList = new ArrayList<>();
            if (backDetailMap.containsKey(key)) {
                backList = backDetailMap.get(key);
            }
            if (mailEmptyMap.containsKey(key)) {
                emptyList = mailEmptyMap.get(key);
            }
            if (mailNotStantMap.containsKey(key)) {
                notStantList = mailNotStantMap.get(key);
            }
            // 失败邮件数+邮箱为空数量+邮箱不合格数量
            int count = backList.size()+emptyList.size()+notStantList.size();
            // 邮件记录详情全部失败
            if (count == list.size()) {
                lambdaUpdateWrapper.set(MailSendRecord::getSendStatus,1);
            }else if (count == 0) {
                // 无退信, 无空收件箱, 主表状态为成功
                lambdaUpdateWrapper.set(MailSendRecord::getSendStatus,0);
            }  else if (count != list.size()) {
                // 部分失败, 主表状态为部分成功
                lambdaUpdateWrapper.set(MailSendRecord::getSendStatus,2);
            }
            lambdaUpdateWrapper.in(MailSendRecord::getId,list.stream().map(MailSendRecordDetail::getParentId).collect(Collectors.toSet()));
            mailSendRecordMapper.update(null,lambdaUpdateWrapper);

        }
        // 无退信list
        List<MailSendRecordDetail> noBackList = new ArrayList<>();
        // 处理子表
        if (CollectionUtils.isNotEmpty(backMailList)) {
            Set<Integer> backMailSet = backMailList.stream().map(item->item.getId()).collect(Collectors.toSet());
            noBackList = detailList.stream().filter(item->!backMailSet.contains(item.getId())).collect(Collectors.toList());
        } else {
            noBackList = detailList;
        }
        // 邮件不为空的(不包含退信)
        mailNotEmptyList = noBackList.stream().filter(item->StringUtils.isNotBlank(item.getReceiveUserMailAddress())).collect(Collectors.toList());
        LambdaUpdateWrapper<MailSendRecordDetail> detailLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (!mailNotEmptyList.isEmpty()) {
            // 收件箱不为空部分,还要判断是否符合邮箱格式, 原记录可能是不合格的, 状态不能更新
            // 取邮箱格式合格的部分更新
            List<MailSendRecordDetail> standardeMailList = mailNotEmptyList.stream().filter(item->{
               return mailRegex.matcher(item.getReceiveUserMailAddress()).matches();
            }).collect(Collectors.toList());

            // 收件箱不为空的且格式合格的, 视为发送成功
            detailLambdaUpdateWrapper.set(MailSendRecordDetail::getSendStatus,0)
                    .in(MailSendRecordDetail::getId,standardeMailList.stream().map(item->item.getId()).collect(Collectors.toList()));
            mailSendRecordDetailMapper.update(null,detailLambdaUpdateWrapper);
        }
        if (!mailEmptyList.isEmpty()) {
            // 收件箱为空的视为发送失败
            detailLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            detailLambdaUpdateWrapper.set(MailSendRecordDetail::getSendStatus,1)
                    .in(MailSendRecordDetail::getId,mailEmptyList.stream().map(item->item.getId()).collect(Collectors.toList()));
            mailSendRecordDetailMapper.update(null,detailLambdaUpdateWrapper);
        }

    }

    /**
     * 处理退件信息, 并将推荐信息更新到数据库中
     * 一个客户一天只发一次 , 所以可以用日期匹配
     * @param rejectMessageList 退件列表
     * @throws Exception 异常信息
     */
    public List<MailSendRecordDetail> doRejectMail(List<Message> rejectMessageList,List<MailSendRecordDetail> sendRecordList) throws Exception {
        String mailPattern ="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern mailRegex = Pattern.compile(mailPattern);
        // 退信中的收件地址
        Set<String> backMailSet = new HashSet<>();
        // 退信对应的发送记录
        List<MailSendRecordDetail> backMailList = new ArrayList<>();
        // 处理退回邮件
        for (Message message : rejectMessageList) {
            // 发送时间
            String backReason = doMesssage(message);
            if (StringUtils.isNotBlank(backReason)){
                // 退件
                List<MailSendRecordDetail> sendFailList = sendRecordList.stream().filter(item->{
                    // 退信原因中包含收件邮箱(邮箱不能为空且收件箱格式正确)
                    boolean flag = StringUtils.isNotBlank(item.getReceiveUserMailAddress()) && mailRegex.matcher(item.getReceiveUserMailAddress()).matches();
                    boolean sameReceiveMail = flag && backReason.contains(item.getReceiveUserMailAddress());
                    if (sameReceiveMail){
                        backMailSet.add(item.getReceiveUserMailAddress());
                    }
                    return sameReceiveMail;
                }).collect(Collectors.toList());
                if (!sendFailList.isEmpty()) {
                    backMailList.addAll(sendFailList);
                    // 更新退件信息到子表
                    for (MailSendRecordDetail entity : sendFailList) {
                        // 更新邮件状态, 退件原因
                        entity.setMailType(MailTypeEnum.back);
                        entity.setSendFailDesc(backReason);
                        entity.setSendStatus(1);
                        mailSendRecordDetailMapper.updateById(entity);
                    }
                }
            }
        }
        return backMailList;
    }

    /**
     * 获取退件原因
     * @param message 退件obj
     * @return 返回退件原因
     * @throws Exception 异常
     */
    public String doMesssage(Message message) throws Exception {
        if(message.getContent() instanceof Multipart){
            Multipart multipart = (Multipart)message.getContent();
            for (int index=0;index<multipart.getCount();index++){
                Part part = multipart.getBodyPart(index); //解包, 取出 MultiPart的各个部分, 每部分可能是邮件内容,
                String contentTemp = part.getContent()+"";
                if (contentTemp.contains("退信")){
                    return contentTemp;
                }
                if (part.getContent() instanceof Multipart) {
                    Multipart smallMultipart = (Multipart) part.getContent();// 转成小包裹
                    Part smallPart = smallMultipart.getBodyPart(0);
                    return smallPart.getContent()+"";
                }
            }
        }
        return "";
    }


}
