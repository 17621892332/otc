package org.orient.otc.quote.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.quote.dto.CapitalSyncDTO;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;
import org.orient.otc.api.system.dto.APICapitalDataChangeRecordAddDto;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.orient.otc.api.system.vo.CapitalDataChangeDetailVO;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.util.ThreadContext;
import org.orient.otc.quote.dto.capitalrecords.*;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.entity.CapitalRecords;
import org.orient.otc.quote.exeption.BussinessException;
import org.orient.otc.quote.mapper.CapitalRecordsMapper;
import org.orient.otc.quote.service.CapitalRecordsService;
import org.orient.otc.quote.util.HutoolUtil;
import org.orient.otc.quote.vo.CapitalRecordsExportVO;
import org.orient.otc.quote.vo.CapitalRecordsImportVO;
import org.orient.otc.quote.vo.CapitalRecordsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CapitalRecordsServiceImpl extends ServiceImpl<CapitalRecordsMapper, CapitalRecords> implements CapitalRecordsService {
    @Resource
    CapitalRecordsMapper capitalRecordsMapper;
    @Resource
    UserClient userClient;
    @Resource
    ClientClient client;
    @Resource
    SystemDataChangeRecordClient systemDataChangeRecordClient;
    @Resource
    ObjectEqualsUtil objectEqualsUtil;

    @Value("${isNeedSyncToYl}")
    private Boolean isNeedSyncToYl;

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Override
    public List<CapitalRecords> getListByVestingDate(Set<Integer> clientIdList,LocalDate endDate) {
        LambdaQueryWrapper<CapitalRecords> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.le(endDate!=null,CapitalRecords::getVestingDate,endDate);
        lambdaQueryWrapper.in(CapitalRecords::getCapitalStatus,Arrays.asList(CapitalStatusEnum.settlement,CapitalStatusEnum.confirmed));
        lambdaQueryWrapper.in(clientIdList!=null&&!clientIdList.isEmpty(),CapitalRecords::getClientId,clientIdList);
        lambdaQueryWrapper.eq(CapitalRecords::getIsDeleted, IsDeletedEnum.NO);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public Map<Integer, BigDecimal> getMapByVestingDate(List<Integer> clientIdList, LocalDate endDate) {
        QueryWrapper<CapitalRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("clientId","ifnull(sum(money),0) as money");
        queryWrapper.lt(endDate!=null,"vestingDate",endDate);
        queryWrapper.in(clientIdList!=null&& !clientIdList.isEmpty(),"clientId",clientIdList);
        queryWrapper.eq("isDeleted", IsDeletedEnum.NO);
        queryWrapper.groupBy("clientId");
        List<CapitalRecords> capitalRecordsList = this.list(queryWrapper);

        return capitalRecordsList.stream().collect(Collectors.toMap(CapitalRecords::getClientId,CapitalRecords::getMoney));
    }



    /**
     * 获取构造器
     * @param dto 入参对象
     * @return 返回查询构造器对象
     */
    public LambdaQueryWrapper<CapitalRecords> getLambdaQueryWrapper(CapitalRecordsPageDto dto) {
        LambdaQueryWrapper<CapitalRecords> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        /*// 发生时间-开始
        lambdaQueryWrapper.ge(dto.getStartHappenTime()!=null,CapitalRecords::getHappenTime,dto.getStartHappenTime());
        // 发生时间-结束
        lambdaQueryWrapper.le(dto.getEndHappenTime()!=null,CapitalRecords::getHappenTime,dto.getEndHappenTime());*/
        // 归属时间-开始
        lambdaQueryWrapper.ge(dto.getStartVestingDate()!=null,CapitalRecords::getVestingDate,dto.getStartVestingDate());
        // 归属时间-结束
        lambdaQueryWrapper.le(dto.getEndVestingDate()!=null,CapitalRecords::getVestingDate,dto.getEndVestingDate());
        // 客户名称列表
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getClientIdList()),CapitalRecords::getClientId,dto.getClientIdList());
        // 交易编号
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getTradeCode()),CapitalRecords::getTradeCode,dto.getTradeCode());
        // 资金编号
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getCapitalCode()),CapitalRecords::getCapitalCode,dto.getCapitalCode());
        // 方向类型
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getDirectionList()),CapitalRecords::getDirection,dto.getDirectionList());
        // 资金状态
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getCapitalStatusList()),CapitalRecords::getCapitalStatus,dto.getCapitalStatusList());
        // 操作人
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getUpdatorIdList()),CapitalRecords::getUpdatorId,dto.getUpdatorIdList());
        lambdaQueryWrapper.eq(CapitalRecords::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.orderByDesc(CapitalRecords::getHappenTime); // 按照创建时间倒序
        return lambdaQueryWrapper;
    }

    /**
     * 补充引用字段值
     * @param list 待补充字段的对象集合
     */
    public void setQuoteFiledName(List<CapitalRecordsVO> list) {
        Set<Integer> clientIdSet = list.stream().map(CapitalRecordsVO::getClientId).collect(Collectors.toSet());
        // 客户信息 key = 客户ID , value = 客户obj
        Map<Integer, ClientVO> clientMap;
        if(!clientIdSet.isEmpty()) {
            // 获取客户信息
            clientMap = client.getClientListByIds(clientIdSet).stream().collect(Collectors.toMap(ClientVO::getId, item->item,(v1, v2)->v2));
        } else {
            clientMap = new HashMap<>();
        }
        // 处理创建人和更新人
        Set<Integer> creatorIdSet = list.stream().map(CapitalRecordsVO::getCreatorId).collect(Collectors.toSet());
        Set<Integer> updatorIdSet = list.stream().map(CapitalRecordsVO::getUpdatorId).collect(Collectors.toSet());
        Set<Integer> userIsSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(creatorIdSet)){
            userIsSet.addAll(creatorIdSet);
        }
        if (CollectionUtils.isNotEmpty(updatorIdSet)){
            userIsSet.addAll(updatorIdSet);
        }
        // key = id , value = name
        Map<Integer,String> userMap = userClient.getUserMapByIds(userIsSet);
        list.forEach(item->{
            if (clientMap.containsKey(item.getClientId())) {
                ClientVO client = clientMap.get(item.getClientId());
                item.setClient(client);
                item.setClientName(client.getName());
            }
            item.setCreatorName(userMap.get(item.getCreatorId()));
            item.setUpdatorName(userMap.get(item.getUpdatorId()));
            if(item.getDirection() != null) {
                item.setDirectionName(item.getDirection().getDesc());
            }
            if (item.getCapitalStatus() != null) {
                item.setCapitalStatusName(item.getCapitalStatus().getDesc());
            }
        });
    }

    @Override
    public IPage<CapitalRecordsVO> getListByPage(CapitalRecordsPageDto dto) {
        LambdaQueryWrapper<CapitalRecords> lambdaQueryWrapper =getLambdaQueryWrapper(dto);
        IPage<CapitalRecords> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);

        IPage<CapitalRecordsVO> returnIpage = ipage.convert(item->{
            CapitalRecordsVO vo = new CapitalRecordsVO();
            BeanUtils.copyProperties(item,vo);
            return vo;
        });
        setQuoteFiledName(returnIpage.getRecords());
        return returnIpage;
    }

    @Override
    public IPage<CapitalRecordsVO> getListByClientPage(SettlementReportDTO dto) {
        IPage<CapitalRecords> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),getCapitalRecordsQueryWrapper(dto));

        IPage<CapitalRecordsVO> returnIpage = ipage.convert(item->{
            CapitalRecordsVO vo = new CapitalRecordsVO();
            BeanUtils.copyProperties(item,vo);
            return vo;
        });
        setQuoteFiledName(returnIpage.getRecords());
        return returnIpage;
    }

    @Override
    public List<CapitalRecordsVO> getListByClient(SettlementReportDTO dto) {

        List<CapitalRecords> dbList = this.list(getCapitalRecordsQueryWrapper(dto));

        return CglibUtil.copyList(dbList,CapitalRecordsVO::new);
    }
    private LambdaQueryWrapper<CapitalRecords> getCapitalRecordsQueryWrapper(SettlementReportDTO dto){
        LambdaQueryWrapper<CapitalRecords> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 归属时间-开始
        lambdaQueryWrapper.ge(dto.getStartDate()!=null,CapitalRecords::getVestingDate,dto.getStartDate());
        // 归属时间-结束
        lambdaQueryWrapper.le(dto.getEndDate()!=null,CapitalRecords::getVestingDate,dto.getEndDate());
        // 客户名称列表
        lambdaQueryWrapper.eq(CapitalRecords::getClientId,dto.getClientId());
        lambdaQueryWrapper.in(CapitalRecords::getCapitalStatus,Arrays.asList(CapitalStatusEnum.settlement,CapitalStatusEnum.confirmed));
        //资金记录为0的过滤掉
        lambdaQueryWrapper.ne(CapitalRecords::getMoney,BigDecimal.ZERO);
        lambdaQueryWrapper.eq(CapitalRecords::getIsDeleted, IsDeletedEnum.NO);
        // 按照创建时间倒序
        lambdaQueryWrapper.orderByDesc(CapitalRecords::getHappenTime);
        return lambdaQueryWrapper;
    }
    @Override
    public String delete(CapitalRecordsDeleteDto dto) {
        CapitalRecords entity = this.getById(dto.getId());
        if (CapitalStatusEnum.confirmed.equals(entity.getCapitalStatus())&&dto.getIsTransDetail() == null) {
            BussinessException.E_300505.assertTrue(Boolean.FALSE);
        }
        LambdaUpdateWrapper<CapitalRecords> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(CapitalRecords::getId,dto.getId())
                .set(CapitalRecords::getIsDeleted,IsDeletedEnum.YES);
        capitalRecordsMapper.update(null,lambdaUpdateWrapper);
        List<DiffObjectVO> list = new ArrayList<>();
        list.add(objectEqualsUtil.getDeleteDiffObjectVO());
        APICapitalDataChangeRecordAddDto apiDto = new APICapitalDataChangeRecordAddDto();
        apiDto.setChangeFields(JSON.toJSONString(list));
        apiDto.setCapitalId(entity.getId());
        apiDto.setDirection(entity.getDirection());
        apiDto.setTradeCode(entity.getTradeCode());
        apiDto.setUnderlyingCode(entity.getUnderlyingCode());
        apiDto.setClientId(entity.getClientId());
        apiDto.setChangeType(DataChangeTypeEnum.delete);
        systemDataChangeRecordClient.addCapitalDataChangeRecord(apiDto);
        return "操作成功";
    }

    @Override
    public String updateCapitalStatus(CapitalRecordsUpdateCapitalStatusDto dto) {
        List<Integer> idList = dto.getIdList();
        // 校验
        for (Integer id : idList) {
            CapitalRecords entity = capitalRecordsMapper.selectById(id);
            String tradeCode = entity.getTradeCode();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(tradeCode)){
                BussinessException.E_300102.assertTrue(false,"和交易挂钩的资金记录不能在该页面操作");
            }
            CapitalStatusEnum capitalStatus = entity.getCapitalStatus();
            // 已拒绝的, 已确认的, 已结算的 不能再做任何操作
            if (capitalStatus==CapitalStatusEnum.refuse || capitalStatus==CapitalStatusEnum.confirmed || capitalStatus==CapitalStatusEnum.settlement) {
                String msg = "";
                if (capitalStatus==CapitalStatusEnum.refuse){
                    msg = msg+"已"+capitalStatus.getDesc();
                } else {
                    msg = msg+capitalStatus.getDesc();
                }
                msg+="的记录不能再次" + dto.getCapitalStatus().getDesc().replaceAll("已","");

                BussinessException.E_300102.assertTrue(false,msg);
            }
        }
        // 更新
        for (Integer id : idList) {
            LambdaUpdateWrapper<CapitalRecords> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(CapitalRecords::getId, id);
            //更新内容
            CapitalRecords capitalRecords = CapitalRecords.builder().capitalStatus(dto.getCapitalStatus()).build();
            CapitalRecords entity = capitalRecordsMapper.selectById(id); // 先查询后更新 (否则取到的是更新后的信息)
            capitalRecordsMapper.update(capitalRecords, lambdaUpdateWrapper);
            //插入更新操作记录
            CapitalDataChangeDetailVO orgVO = new CapitalDataChangeDetailVO();
            BeanUtils.copyProperties(entity,orgVO);
            if (entity.getCapitalStatus() != null){
                orgVO.setCapitalStatus(entity.getCapitalStatus().getDesc());
            }
            CapitalDataChangeDetailVO destVO = new CapitalDataChangeDetailVO();
            BeanUtils.copyProperties(entity,destVO);
            destVO.setCapitalStatus(dto.getCapitalStatus().getDesc()); // 只变更了状态 , 所以只有一个字段不同
            List<DiffObjectVO> list = objectEqualsUtil.equalsObjectField(orgVO,destVO);
            APICapitalDataChangeRecordAddDto apiDto = new APICapitalDataChangeRecordAddDto();
            apiDto.setChangeFields(JSON.toJSONString(list));
            apiDto.setCapitalId(orgVO.getId());
            apiDto.setDirection(orgVO.getDirection());
            apiDto.setTradeCode(orgVO.getTradeCode());
            apiDto.setUnderlyingCode(orgVO.getUnderlyingCode());
            apiDto.setClientId(orgVO.getClientId());
            apiDto.setChangeType(DataChangeTypeEnum.delete);
            systemDataChangeRecordClient.addCapitalDataChangeRecord(apiDto);
        }
        if (isNeedSyncToYl&&dto.getIsTransDetail()==null) {
            List<CapitalRecords> capitalRecordsList = capitalRecordsMapper.selectList(new LambdaQueryWrapper<CapitalRecords>()
                    .in(CapitalRecords::getId, idList));
            for (CapitalRecords capitalRecords : capitalRecordsList) {
                if (dto.getCapitalStatus() == CapitalStatusEnum.confirmed) {
                    rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.CAPITAL_CONFIRMED, capitalRecords);
                }
                if (dto.getCapitalStatus() == CapitalStatusEnum.refuse) {
                    rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.CAPITAL_REFUSE, capitalRecords);
                }
            }

        }
        return "操作成功";
    }
    @Override
    @Transactional
    public Integer addConfirm(CapitalRecordsAddDto dto) {
        /*
        逻辑校验 :  出入金,其他收支
        归属时间, 发生时间 不能大于当前时刻
         */
        if (dto.getVestingDate().isAfter(LocalDate.now()) || dto.getHappenTime().isAfter(LocalDateTime.now())){
            BussinessException.E_300102.assertTrue(false,"发生时间或归属时间不能大于当前时刻");
        }

        CapitalRecords capitalRecord = new CapitalRecords();
        BeanUtils.copyProperties(dto,capitalRecord);
        capitalRecord.setCapitalStatus(CapitalStatusEnum.unconfirmed);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        // 资金编号 = 当前时间的年月日时分秒毫秒
        String capitalCode = sdf.format(calendar.getTime())+calendar.get(Calendar.MILLISECOND);
        capitalRecord.setCapitalCode(capitalCode);
        AuthorizeInfo currentUser = ThreadContext.getAuthorizeInfo();
        // 处理新增时备注格式
        if (StringUtils.isNotBlank(dto.getRemark())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            String remarkMsg = time+"-"+currentUser.getName()+"："+dto.getRemark();
            capitalRecord.setRemark(remarkMsg);
        }
        if (currentUser != null){
            capitalRecord.setCreatorId(currentUser.getId());
            capitalRecord.setUpdatorId(currentUser.getId());
        }
        this.saveOrUpdate(capitalRecord);

        if (isNeedSyncToYl) {
                rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.CAPITAL_ADD_CONFIRMED, capitalRecord);
        }
        CapitalRecordsUpdateCapitalStatusDto capitalStatusDto = new CapitalRecordsUpdateCapitalStatusDto();
        capitalStatusDto.setIdList(Collections.singletonList(capitalRecord.getId()));
        capitalStatusDto.setCapitalStatus(CapitalStatusEnum.confirmed);
        capitalStatusDto.setIsTransDetail("1");
        updateCapitalStatus(capitalStatusDto);
        return capitalRecord.getId();
    }

    @Override
    @Transactional
    public Integer add(CapitalRecordsAddDto dto) {
        /*
        逻辑校验 :  出入金,其他收支
        归属时间, 发生时间 不能大于当前时刻
         */
        if (
                dto.getDirection() == CapitalDirectionEnum.out ||
                dto.getDirection() == CapitalDirectionEnum.in ||
                dto.getDirection() == CapitalDirectionEnum.otherIn ||
                dto.getDirection() == CapitalDirectionEnum.otherOut
        ){
            if (dto.getVestingDate().isAfter(LocalDate.now()) || dto.getHappenTime().isAfter(LocalDateTime.now())){
                BussinessException.E_300102.assertTrue(false,"发生时间或归属时间不能大于当前时刻");
            }
        }
        List<CapitalDirectionEnum> capitalDirectionList = CapitalDirectionEnum.getOut();
        // 出金方向金额转为负数
        if (capitalDirectionList.contains(dto.getDirection())) {
            dto.setMoney(dto.getMoney().negate());
        }
        CapitalRecords capitalRecord = new CapitalRecords();
        BeanUtils.copyProperties(dto,capitalRecord);
        capitalRecord.setCapitalStatus(CapitalStatusEnum.unconfirmed);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        // 资金编号 = 当前时间的年月日时分秒毫秒
        String capitalCode = sdf.format(calendar.getTime())+calendar.get(Calendar.MILLISECOND);
        capitalRecord.setCapitalCode(capitalCode);
        AuthorizeInfo currentUser = ThreadContext.getAuthorizeInfo();
        // 处理新增时备注格式
        if (StringUtils.isNotBlank(dto.getRemark())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            String remarkMsg = time+"-"+currentUser.getName()+"："+dto.getRemark();
            capitalRecord.setRemark(remarkMsg);
        }
        if (currentUser != null){
            capitalRecord.setCreatorId(currentUser.getId());
            capitalRecord.setUpdatorId(currentUser.getId());
        }
        this.saveOrUpdate(capitalRecord);
        if (isNeedSyncToYl) {
                rocketMQTemplate.syncSend(RocketMqConstant.SYNC_TOPIC + ":" + RocketMqConstant.CAPITAL_ADD, capitalRecord);
        }
        return capitalRecord.getId();
    }

    @Override
    public String addRemark(CapitalRecordsUpdateRemarkDto dto) {
        CapitalRecords capitalRecord = capitalRecordsMapper.selectById(dto.getId());
        String dbRemark = capitalRecord.getRemark(); // 原备注
        String orgRemark = capitalRecord.getRemark(); // 原备注
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        AuthorizeInfo currentUser = ThreadContext.getAuthorizeInfo();
        String remarkMsg = time+"-"+currentUser.getName()+"："+dto.getRemark(); // 本次待追加备注
        if(StringUtils.isNotBlank(dbRemark)) { // 追加本次填写的备注
            dbRemark = dbRemark+"\n\r"+remarkMsg;
        } else {
            dbRemark = remarkMsg;
        }
        LambdaUpdateWrapper<CapitalRecords> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(CapitalRecords::getId,dto.getId());
        CapitalRecords capitalRecords = CapitalRecords.builder().remark(dbRemark).build();
        capitalRecordsMapper.update(capitalRecords, lambdaUpdateWrapper);
        CapitalDataChangeDetailVO orgVO = new CapitalDataChangeDetailVO();
        BeanUtils.copyProperties(capitalRecord,orgVO);
        orgVO.setCapitalStatus(null);
        CapitalDataChangeDetailVO destVO = new CapitalDataChangeDetailVO();
        BeanUtils.copyProperties(capitalRecord,destVO);
        destVO.setRemark(dbRemark); // 只变更了备注 , 所以只有一个字段不同
        destVO.setCapitalStatus(null);
        List<DiffObjectVO> list = new ArrayList<>();
        DiffObjectVO diffObjectVO = objectEqualsUtil.buildDiffObjectVO("备注",orgRemark,capitalRecords.getRemark());
        list.add(diffObjectVO);
        APICapitalDataChangeRecordAddDto apiDto = new APICapitalDataChangeRecordAddDto();
        apiDto.setChangeFields(JSON.toJSONString(list));
        apiDto.setCapitalId(orgVO.getId());
        apiDto.setDirection(orgVO.getDirection());
        apiDto.setTradeCode(orgVO.getTradeCode());
        apiDto.setUnderlyingCode(orgVO.getUnderlyingCode());
        apiDto.setClientId(orgVO.getClientId());
        apiDto.setChangeType(DataChangeTypeEnum.delete);
        systemDataChangeRecordClient.addCapitalDataChangeRecord(apiDto);
        return "操作成功";
    }

    @Override
    public void export(CapitalRecordsExportDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalRecordsPageDto capitalRecordsPageDto = new CapitalRecordsPageDto();
        BeanUtils.copyProperties(dto,capitalRecordsPageDto);
        LambdaQueryWrapper<CapitalRecords> lambdaQueryWrapper = getLambdaQueryWrapper(capitalRecordsPageDto);
        List<CapitalRecords> list = capitalRecordsMapper.selectList(lambdaQueryWrapper);
        List<CapitalRecordsVO> listVO = new ArrayList<>();
        for (CapitalRecords item : list) {
            CapitalRecordsVO vo = new CapitalRecordsVO();
            BeanUtils.copyProperties(item,vo);
            vo.setCreateTimeString(getLocalDateTimeString(item.getCreateTime()));
            vo.setUpdateTimeString(getLocalDateTimeString(item.getUpdateTime()));
            listVO.add(vo);
        }
        setQuoteFiledName(listVO);
        List<CapitalRecordsExportVO> exportList = listVO.stream().map(item->{
            CapitalRecordsExportVO exportVO = new CapitalRecordsExportVO();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            exportVO.setHappenTime(formatter.format(item.getHappenTime()));
            exportVO.setVestingDate(formatter2.format(item.getVestingDate()));
            BeanUtils.copyProperties(item,exportVO);
            return exportVO;
        }).collect(Collectors.toList());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String exportFileName = format.format(new Date());
        HutoolUtil.export(exportList,"资金记录_" + exportFileName,"资金记录",CapitalRecordsExportVO.class,request,response);
       // excelTemplateExportUtil.capitalExport(listVO);
    }

    /**
     * 日期格式化并返回
     * @param time 待格式化时间
     * @return 返回格式化时间
     */
    public String getLocalDateTimeString(LocalDateTime time){
        if (time==null) {
            return "";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return  time.format(formatter);
        }
    }

    /**
     * 资金记录导入
     * 1. 日期格式校验
     * 2. 资金编号唯一性就校验
     * 3. 镒链表头的发生时间,对应我们的归属时间(只取日期,时分秒不要)
     * 4. 读取时,按照列顺序读取
     * @param file 导入的文件
     * @return 返回操作信息
     * @throws IOException 抛出异常
     */
    @Override
    public String importCapital(MultipartFile file) throws IOException {
        InputStream is = new BufferedInputStream(file.getInputStream());
        FileMagic fileMagic = FileMagic.valueOf(is);
        if (Objects.equals(fileMagic, FileMagic.OLE2)) {
            BussinessException.E_300102.assertTrue(false,"导入的文件格式不正确, 请以下载的xlsx模板为准做导入");
        } else if (Objects.equals(fileMagic, FileMagic.HTML)) {
            BussinessException.E_300102.assertTrue(false,"导入的文件格式不正确, 请以下载的xlsx模板为准做导入");
        }

        /*ExcelUtil.readBySax(is, 0, new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
                log.info("行处理事件");
            }
        });*/
        ExcelReader excelReader = ExcelUtil.getReader(is); // oom
        int totalCount = excelReader.getRowCount();
        // 所有行数据
        List<Map<String, Object>> list  = excelReader.readAll();
        List<CapitalRecordsImportVO> insertVOList = new ArrayList<>();
        for (int rowIndex=0;rowIndex<list.size();rowIndex++) {
            Map<String, Object> rowMap = list.get(rowIndex);
            CapitalRecordsImportVO vo = new CapitalRecordsImportVO();
            Set<Map.Entry<String,Object>> set = rowMap.entrySet();
            List<Map.Entry<String, Object>> dataList = new ArrayList<>(set);
            // 每一个列的表头对应单元格数据
            for (int columnIndex=0;columnIndex<dataList.size();columnIndex++) {
                Map.Entry<String,Object> entry = dataList.get(columnIndex);
                // 表头值
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                setObjectFiledValue(vo,columnIndex,value,key);
            }
            vo.setRowIndex(rowIndex);
            insertVOList.add(vo);
        }
        // 按资金编号分组
        Map<String,List<CapitalRecordsImportVO>> capitalCodeMap = insertVOList.stream().collect(Collectors.groupingBy(CapitalRecordsImportVO::getCapitalCode));
        // 导入的数据中资金编号是否重复校验
        if (capitalCodeMap.size() != insertVOList.size()){ // 分组后长度和导入的长度不一致,肯定有资金编号重复
            for (Map.Entry<String,List<CapitalRecordsImportVO>> entry : capitalCodeMap.entrySet()) {
                List<CapitalRecordsImportVO> capitalCodeList = entry.getValue();
                if (capitalCodeList.size()>1){
                    String msg = "导入的数据中资金编号重复:"+"\n\r";
                    for (CapitalRecordsImportVO item : capitalCodeList) {
                        msg=msg+"资金编号 : "+item.getCapitalCode()+" , 第 "+(item.getRowIndex()+1)+" 行"+"\n\r";
                    }
                    BussinessException.E_300102.assertTrue(false,msg);
                }
            }
        }
        // 资金编号
        List<String> capitalCode = insertVOList.stream().map(CapitalRecordsImportVO::getCapitalCode).collect(Collectors.toList());
        // 资金编号在数据库中是否已存在
        LambdaQueryWrapper<CapitalRecords> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CapitalRecords::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.in(CapitalRecords::getCapitalCode,capitalCode);
        List<CapitalRecords> capitalRecordsList = capitalRecordsMapper.selectList(lambdaQueryWrapper);
        // 如果已存在,给出提示
        if (!capitalRecordsList.isEmpty()){
            String msg = "导入的资金编号已存在:"+"\n\r";
            // 循环数据库中查出来的资金编号, 定位重复的资金编号在excel中的具体位置
            for (CapitalRecords entity : capitalRecordsList) {
                if (capitalCodeMap.containsKey(entity.getCapitalCode())) {
                    List<CapitalRecordsImportVO> voList = capitalCodeMap.get(entity.getCapitalCode());
                    for (CapitalRecordsImportVO item : voList) {
                        msg=msg+"资金编号 : "+item.getCapitalCode()+" , 第 "+(item.getRowIndex()+1)+" 行"+"\n\r";
                    }
                }
            }
            BussinessException.E_300102.assertTrue(false,msg);
        }
        // 客户名称
        Set<String> clientNameSet = insertVOList.stream().map(CapitalRecordsImportVO::getClientName).collect(Collectors.toSet());
        // 客户map , key=名称,value=ID
        Map<String,Integer> clientMap = client.getClientMapByNameList(clientNameSet);
        // 用户map , key=名称,value=ID
        Map<String,Integer> userMap = userClient.getUserList().stream().collect(Collectors.toMap(UserVo::getName,UserVo::getId,(v1,v2)->v2));
        List<CapitalRecords> insertList = new ArrayList<>();
        for (CapitalRecordsImportVO vo : insertVOList) {
            CapitalRecords entity = new CapitalRecords();
            BeanUtils.copyProperties(vo,entity);
            entity.setClientId(clientMap.get(vo.getClientName()));
            // 如果找不到对应的客户 , 不准导入
            /*if (clientMap.containsKey(vo.getClientName())){
                entity.setClientId(clientMap.get(vo.getClientName()));
            } else {
                BussinessException.E_300102.assertTrue(false,"客户不存在, 客户="+vo.getClientName());
            }*/
            entity.setCreatorId(userMap.get(vo.getCreatorName()));
            entity.setUpdatorId(userMap.get(vo.getUpdatorName()));
            //capitalRecordsMapper.insert(entity);
            insertList.add(entity);
        }
        // 批量保存
        this.saveBatch(insertList,insertList.size());
        return "导入成功";
    }

    @Override
    public Boolean capitalUpdateSync(CapitalSyncDTO dto) {
        CapitalRecords capitalRecords = new CapitalRecords();
        capitalRecords.setId(dto.getId());
        capitalRecords.setYlId(dto.getYlId());
        capitalRecords.setCapitalCode(dto.getNumber());
      return  this.updateById(capitalRecords);
    }

    /**
     * 字符串转LocalDateTime
     * @param value 待转换的字符串日期
     * @return 返回时间
     */
    public LocalDateTime getLocalDateTime(String value){
        if (StringUtils.isNotBlank(value)) {
            DateTimeFormatter dateTimeFormatter;
            value = value.trim();
            if (value.length() <= 10) { // 日期只有年月日,没有时分秒
                value+=" 00:00:00"; // 加上时分秒,方便转换
            }
            if (value.contains("-")){ // yyyy-MM-dd HH:mm:ss
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(value,dateTimeFormatter);
            } else if(value.contains("/")){// yyyy/MM/dd HH:mm:ss
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                return LocalDateTime.parse(value,dateTimeFormatter);
            } else if(value.contains("年")){// yyyy年MM月dd日 HH:mm:ss
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
                return LocalDateTime.parse(value,dateTimeFormatter);
            }
        }
        return null;
    }
    /**
     * 设置每一行中每一列的值到vo对象中去
     * @param vo 返回对象
     * @param columnIndex 列下标
     * @param value 字段值
     * @param key 字段名称
     */
    public void setObjectFiledValue(CapitalRecordsImportVO vo , int columnIndex, String value,String key) {
        switch (columnIndex) {
            case 0: // 操作时间
                LocalDateTime updateTime = getLocalDateTime(value);
                if (updateTime != null) {
                    vo.setUpdateTime(updateTime);
                }
                break;
            case 1: // 归属时间
                LocalDateTime vestingDate = getLocalDateTime(value);
                if (vestingDate != null){
                    vo.setVestingDate(vestingDate.toLocalDate());
                    vo.setHappenTime(vestingDate);
                }
                break;
            case 2: // 资金编号
                vo.setCapitalCode(value);
                break;
            case 3: // 客户名称
                vo.setClientName(value);
                break;
            case 4: // 方向类型
                CapitalDirectionEnum direction = CapitalDirectionEnum.getCapitalStatusByDesc(value);
                vo.setDirection(direction);
                break;
            case 5: // 金额
                value = value.replace(",",""); // 去除千分位的逗号
                BigDecimal money = new BigDecimal(value);
                vo.setMoney(money);
                break;
            case 6: // 币种
                vo.setCurrency(value);
                break;
            case 7: // 相关交易
                vo.setTradeCode(value);
                break;
            case 8: // 标的代码
                vo.setUnderlyingCode(value);
                break;
            case 9: // 资金状态
                CapitalStatusEnum capitalStatus = CapitalStatusEnum.getCapitalStatusByDesc(value);
                vo.setCapitalStatus(capitalStatus);
                break;
            case 10: // 操作人
                vo.setUpdatorName(value);
                break;
            case 11: // 创建人
                vo.setCreatorName(value);
                break;
            case 12: // 备注
                vo.setRemark(value);
                break;
            default:
                log.warn("多余列: "+key);
        }

    }
}
