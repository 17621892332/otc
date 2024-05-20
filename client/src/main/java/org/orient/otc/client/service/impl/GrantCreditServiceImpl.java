package org.orient.otc.client.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.extra.cglib.CglibUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.client.enums.GrantCreditApprovalStatusEnum;
import org.orient.otc.api.system.dto.APIGrantCreditDataChangeRecordAddDto;
import org.orient.otc.api.system.enums.DataChangeTypeEnum;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.orient.otc.api.system.vo.GrantCreditDataChangeDetailVO;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.client.dto.client.*;
import org.orient.otc.client.entity.Client;
import org.orient.otc.client.entity.ClientLevel;
import org.orient.otc.client.entity.GrantCredit;
import org.orient.otc.client.exception.BussinessException;
import org.orient.otc.client.mapper.GrantCreditMapper;
import org.orient.otc.client.service.ClientLevelService;
import org.orient.otc.client.service.ClientService;
import org.orient.otc.client.service.GrantCreditService;
import org.orient.otc.client.vo.client.GrantCreditImportVO;
import org.orient.otc.client.vo.client.GrantCreditVO;
import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GrantCreditServiceImpl extends ServiceImpl<GrantCreditMapper, GrantCredit> implements GrantCreditService {

    @Resource
    private GrantCreditMapper grantCreditMapper;

    @Resource
    private ClientService clientService;

    @Resource
    private ClientLevelService clientLevelService;

    @Resource
    private UserClient userClient;
    @Autowired
    SystemDataChangeRecordClient systemDataChangeRecordClient;
    @Resource
    ObjectEqualsUtil objectEqualsUtil;

    @Override
    public IPage<GrantCreditVO> getListByPage(GrantCreditPageDto dto) {
        LambdaQueryWrapper<GrantCredit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GrantCredit::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(GrantCredit::getDirection, dto.getDirection());
        lambdaQueryWrapper.eq(dto.getClientId()!=null,GrantCredit::getClientId, dto.getClientId());
        lambdaQueryWrapper.ge(dto.getStartMaturityDate()!=null,GrantCredit::getEndDate, dto.getStartMaturityDate());
        lambdaQueryWrapper.le(dto.getEndMaturityDate()!=null,GrantCredit::getEndDate, dto.getEndMaturityDate());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getApprovalStatusList()),GrantCredit::getApprovalStatus, dto.getApprovalStatusList());
        lambdaQueryWrapper.orderByDesc(GrantCredit::getCreateTime);
        IPage<GrantCredit> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        if (ipage.getTotal()==0){
            return ipage.convert(item-> new GrantCreditVO());
        }
        // 设置客户名称
        Set<Integer> clientIdSet = ipage.getRecords().stream().map(GrantCredit::getClientId).collect(Collectors.toSet());
        List<Client> clientList = clientService.queryByIds(clientIdSet);
        // key = 客户ID value = 客户obj
        Map<Integer,Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, item->item));
        // 设置创建人和修改人名称
        Set<Integer> createrUserIdSet = ipage.getRecords().stream().map(BaseEntity::getCreatorId).collect(Collectors.toSet());
        Set<Integer> updateUserIdSet = ipage.getRecords().stream().map(BaseEntity::getUpdatorId).collect(Collectors.toSet());

        Set<Integer> userIdSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(createrUserIdSet)) {
            userIdSet.addAll(createrUserIdSet);
        }
        if (CollectionUtils.isNotEmpty(updateUserIdSet)) {
            userIdSet.addAll(updateUserIdSet);
        }
        Map<Integer,String> userMap = userClient.getUserMapByIds(userIdSet);
        // 取所有的客户等级信息 (数据量很少)
        LambdaQueryWrapper<ClientLevel> clientLevelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        clientLevelLambdaQueryWrapper.eq(ClientLevel::getIsDeleted,IsDeletedEnum.NO);
        List<ClientLevel> clientLevelList = clientLevelService.list(clientLevelLambdaQueryWrapper);
        // key = 等级ID , value = 客户等级Obj
        Map<Integer,ClientLevel> clientLevelMap = clientLevelList.stream().collect(Collectors.toMap(ClientLevel::getId, item->item,(v1, v2)->v2));
        return ipage.convert(item->{
            GrantCreditVO vo = new GrantCreditVO();
            BeanUtils.copyProperties(item,vo);
            vo.setCreatorName(userMap.get(item.getCreatorId()));
            vo.setUpdatorName(userMap.get(item.getUpdatorId()));
            if(clientMap.containsKey(item.getClientId())) {
                Client client = clientMap.get(item.getClientId());
                vo.setClientName(client.getName());
                vo.setClientCode(client.getCode());
                vo.setClientLevel(clientLevelMap.get(client.getLevelId()));
            }
            vo.setApprovalStatusName(item.getApprovalStatus().getDesc());
            return vo;
        });
    }

    /**
     * 新增授信
     * 其中给客户授信需要校验授信额度不能大于审批额度
     * 同一客户的授信开始日和结束日期不能重叠
     * A : 数据库中开始日期 ~ 数据库中结束日期
     * 只要dto中的开始日期和结束日期有一个在A的范围中 , 视为日期存在重叠
     *
     * @param dto   入参
     * @return  返回值
     */
    @Override
    public HttpResourceResponse add(GrantCreditAddDto dto) {
        if (dto.getDirection() == 0 && dto.getApprovalScale() != null && dto.getAmount().compareTo(dto.getApprovalScale())>0) {
            return HttpResourceResponse.error(-1,"授信额度不能大于审批额度");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            return HttpResourceResponse.error(-1,"结束日期不能小于开始日期");
        }
        LambdaQueryWrapper<GrantCredit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GrantCredit::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(GrantCredit::getClientId,dto.getClientId());
        lambdaQueryWrapper.eq(GrantCredit::getDirection,dto.getDirection());
        lambdaQueryWrapper.and(
                wrapper->wrapper
                        .and(query-> // dto的开始日期是否在范围中
                                query
                                    .le(GrantCredit::getStartDate,dto.getStartDate())
                                    .ge(GrantCredit::getEndDate,dto.getStartDate())
                        )
                        .or(query-> // dto的结束日期是否在范围中
                                query
                                    .le(GrantCredit::getStartDate,dto.getEndDate())
                                    .ge(GrantCredit::getEndDate,dto.getEndDate())
                        )


        );
        long count = grantCreditMapper.selectCount(lambdaQueryWrapper);
        if (count>0) {
            return HttpResourceResponse.error(-1,"同一客户授信有效时间不能重合，请检查该客户已存在的授信记录");
        }
        GrantCredit grantCredit = new GrantCredit();
        BeanUtils.copyProperties(dto,grantCredit);
        grantCredit.setApprovalStatus(GrantCreditApprovalStatusEnum.unapproved);
        grantCreditMapper.insert(grantCredit);
        return HttpResourceResponse.success("操作成功");
    }

    /**
     * 修改授信
     * 其中给客户授信需要校验授信额度不能大于审批额度
     * 同一客户的授信开始日和结束日期不能重叠
     * A : 数据库中开始日期 ~ 数据库中结束日期
     * 只要dto中的开始日期和结束日期有一个在A的范围中 , 视为日期存在重叠
     *
     * @param dto   入参
     * @return  返回值
     */
    @Override
    public HttpResourceResponse updateGrantCredit(GrantCreditUpdateDto dto) {
        if (dto.getDirection() == 0 && dto.getApprovalScale() != null && dto.getAmount().compareTo(dto.getApprovalScale())>0) {
            return HttpResourceResponse.error(-1,"授信额度不能大于审批额度");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            return HttpResourceResponse.error(-1,"结束日期不能小于开始日期");
        }
        LambdaQueryWrapper<GrantCredit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GrantCredit::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.eq(GrantCredit::getClientId,dto.getClientId());
        lambdaQueryWrapper.eq(GrantCredit::getDirection,dto.getDirection());
        lambdaQueryWrapper.ne(GrantCredit::getId,dto.getId());
        lambdaQueryWrapper.and(
                wrapper->wrapper
                        .and(query->
                                query
                                        .le(GrantCredit::getStartDate,dto.getStartDate())
                                        .ge(GrantCredit::getEndDate,dto.getStartDate())
                        )
                        .or(query->
                                query
                                        .le(GrantCredit::getStartDate,dto.getEndDate())
                                        .ge(GrantCredit::getEndDate,dto.getEndDate())
                        )


        );
        Long count = grantCreditMapper.selectCount(lambdaQueryWrapper);
        if (count>0) {
            return HttpResourceResponse.error(-1,"同一客户授信有效时间不能重合，请检查该客户已存在的授信记录");
        }
        GrantCredit grantCredit = new GrantCredit();
        BeanUtils.copyProperties(dto,grantCredit);
        grantCredit.setApprovalStatus(GrantCreditApprovalStatusEnum.unapproved);
        GrantCredit entity = this.getById(dto.getId());
        this.saveOrUpdate(grantCredit);

        // 添加授信修改的历史记录
        GrantCreditDataChangeDetailVO orgVO = new GrantCreditDataChangeDetailVO();
        BeanUtils.copyProperties(entity,orgVO);
        orgVO.setApprovalStatus(null); // 修改授信时, 审批状态不会发生变化
        GrantCreditDataChangeDetailVO destVO = new GrantCreditDataChangeDetailVO();
        BeanUtils.copyProperties(entity,destVO); // 先取数据库中的信息
        CopyOptions copyOption = CopyOptions.create(null, true);// 复制bean忽略空值
        BeanUtil.copyProperties(grantCredit, destVO, copyOption); // 再取修改后的信息
        destVO.setApprovalStatus(null); // 修改授信时, 审批状态不会发生变化
        List<DiffObjectVO> list = objectEqualsUtil.equalsObjectField(orgVO,destVO);
        APIGrantCreditDataChangeRecordAddDto apiDto = new APIGrantCreditDataChangeRecordAddDto();
        apiDto.setChangeFields(JSON.toJSONString(list));
        apiDto.setClientId(orgVO.getClientId());
        apiDto.setGrantCreditId(orgVO.getId());
        apiDto.setStartDate(orgVO.getStartDate());
        apiDto.setEndDate(orgVO.getEndDate());
        apiDto.setDirection(orgVO.getDirection());
        apiDto.setChangeType(DataChangeTypeEnum.update);
        systemDataChangeRecordClient.addGrantCreditDataChangeRecord(apiDto);
        return HttpResourceResponse.success("操作成功");
    }

    /**
     * 删除授信
     * @param dto 入参
     * @return 返回
     */
    @Override
    public HttpResourceResponse deleteGrantCredit(GrantCreditDeleteDto dto) {
        LambdaUpdateWrapper<GrantCredit> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(GrantCredit::getId,dto.getId());
        lambdaUpdateWrapper.set(GrantCredit::getIsDeleted,IsDeletedEnum.YES);
        grantCreditMapper.update(null,lambdaUpdateWrapper);
        GrantCredit entity = this.getById(dto.getId());
        // 添加授信删除的历史记录
        List<DiffObjectVO> list = new ArrayList<>();
        list.add(objectEqualsUtil.getDeleteDiffObjectVO());
        APIGrantCreditDataChangeRecordAddDto apiDto = new APIGrantCreditDataChangeRecordAddDto();
        apiDto.setChangeFields(JSON.toJSONString(list));
        apiDto.setClientId(entity.getClientId());
        apiDto.setGrantCreditId(entity.getId());
        apiDto.setStartDate(entity.getStartDate());
        apiDto.setEndDate(entity.getEndDate());
        apiDto.setDirection(entity.getDirection());
        apiDto.setChangeType(DataChangeTypeEnum.delete);
        systemDataChangeRecordClient.addGrantCreditDataChangeRecord(apiDto);
        return HttpResourceResponse.success("操作成功");
    }

    /**
     * 审批授信 修改 审批状态
     * @param dto 入参
     * @return 返回值
     */
    @Override
    public HttpResourceResponse check(GrantCreditCheckDto dto) {
        LambdaUpdateWrapper<GrantCredit> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(GrantCredit::getId,dto.getId());
        lambdaUpdateWrapper.set(GrantCredit::getApprovalStatus,dto.getApprovalStatus());
        lambdaUpdateWrapper.set(GrantCredit::getUpdateTime, LocalDateTime.now());
        GrantCredit entity = this.getById(dto.getId());
        grantCreditMapper.update(null,lambdaUpdateWrapper);

        // 添加授信审批的历史记录
        GrantCreditDataChangeDetailVO orgVO = new GrantCreditDataChangeDetailVO();
        BeanUtils.copyProperties(entity,orgVO);
        if (entity.getApprovalStatus()!=null) {
            orgVO.setApprovalStatus(entity.getApprovalStatus().getDesc()); // 授信审批时, 审批状态会发生变化
        }
        GrantCreditDataChangeDetailVO destVO = new GrantCreditDataChangeDetailVO();
        BeanUtils.copyProperties(entity,destVO); // 先取数据库中的信息
        CopyOptions copyOption = CopyOptions.create(null, true);// 复制bean忽略空值
        BeanUtil.copyProperties(entity, destVO, copyOption); // 再取修改后的信息
        if (dto.getApprovalStatus()!=null){
            destVO.setApprovalStatus(dto.getApprovalStatus().getDesc());
        }
        List<DiffObjectVO> list = objectEqualsUtil.equalsObjectField(orgVO,destVO);
        APIGrantCreditDataChangeRecordAddDto apiDto = new APIGrantCreditDataChangeRecordAddDto();
        apiDto.setChangeFields(JSON.toJSONString(list));
        apiDto.setClientId(orgVO.getClientId());
        apiDto.setGrantCreditId(orgVO.getId());
        apiDto.setStartDate(orgVO.getStartDate());
        apiDto.setEndDate(orgVO.getEndDate());
        apiDto.setDirection(orgVO.getDirection());
        apiDto.setChangeType(DataChangeTypeEnum.update);
        systemDataChangeRecordClient.addGrantCreditDataChangeRecord(apiDto);
        return HttpResourceResponse.success("操作成功");
    }

    @Override
    public Map<Integer, BigDecimal> getClientGrantCredit(Set<Integer> clientIdList, LocalDate endDate) {
        //获取未来到期的授信
        LambdaQueryWrapper<GrantCredit> grantCreditLambdaQueryWrapper = new LambdaQueryWrapper<>();
        grantCreditLambdaQueryWrapper.eq(GrantCredit::getIsDeleted,IsDeletedEnum.NO);
        grantCreditLambdaQueryWrapper.in(clientIdList!=null && !clientIdList.isEmpty(),GrantCredit::getClientId,clientIdList);
        grantCreditLambdaQueryWrapper.eq(GrantCredit::getDirection,0);
        grantCreditLambdaQueryWrapper.eq(GrantCredit::getApprovalStatus,GrantCreditApprovalStatusEnum.approved);
        grantCreditLambdaQueryWrapper.ge(GrantCredit::getEndDate,endDate);

        List<GrantCredit> grantCreditList = grantCreditMapper.selectList(grantCreditLambdaQueryWrapper);
        return grantCreditList.stream().collect(Collectors.groupingBy(GrantCredit::getClientId))//根据客户ID进行分组
                .entrySet().stream()
                //获取每个分组最小日期的金额
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Collections.min(e.getValue(),
                        Comparator.comparing(GrantCredit::getEndDate)).getAmount()));
    }

    /**
     * 暂时只考虑给客户授信数据做导入
     * 校验同一客户授信日期是否存在交叉, 并给出提示
     * @param file 导入文件
     * @return 返回错误提示信息
     * @throws Exception
     */
    @Override
    public String importGrant(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (!filename.endsWith(".xlsx")) {
            BussinessException.E_700102.assertTrue(false,"导入文件必须是.xlsx");
        }
        InputStream is = new BufferedInputStream(file.getInputStream());
        ExcelReader excelReader = ExcelUtil.getReader(is);
        // 导入文件中的每一行数据
        List<Map<String, Object>> rowList  = excelReader.readAll();
        // 存放导入的所有行数据
        List<GrantCreditImportVO> importDataList = new ArrayList<>();
        for (int rowIndex=0;rowIndex<rowList.size();rowIndex++) {
            // 获取当前行数据
            Map<String, Object> rowMap = rowList.get(rowIndex);
            Set<Map.Entry<String,Object>> set = rowMap.entrySet();
            // 行数据以map的形式转成list , 根据列下标取值
            List<Map.Entry<String, Object>> dataList = new ArrayList<>(set);
            // 待填充数据对象
            GrantCreditImportVO importVO = new GrantCreditImportVO();
            // 每一个列的表头对应单元格数据
            for (int columnIndex=0;columnIndex<dataList.size();columnIndex++) {
                Map.Entry<String, Object> entry = dataList.get(columnIndex);
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                setObjectFiledValue(importVO,columnIndex,key,value);
            }
            importVO.setRowIndex(rowIndex);
            importDataList.add(importVO);
        }
        // 存储授信开始或结束日期为空的行号
        List<Integer> dateNullIndexList = new ArrayList<>();
        // 存储客户名称或客户编号为空的行号
        List<Integer> clientNUllIndexList = new ArrayList<>();
        // 校验授信开始和结束日期不能为空
        for (GrantCreditImportVO vo : importDataList){
            if (vo.getStartDate() == null || vo.getEndDate() == null) {
                dateNullIndexList.add(vo.getRowIndex()+1);
            }
            if (StringUtils.isBlank(vo.getCreatorName())){
                clientNUllIndexList.add(vo.getRowIndex()+1);
            }
        }
        if(dateNullIndexList.size()>0){
            BussinessException.E_700102.assertTrue(false,"授信开始日期和结束日期不能为空,请检查第 "+ JSON.toJSONString(dateNullIndexList)+" 行数据");
        }
        if(clientNUllIndexList.size()>0){
            BussinessException.E_700102.assertTrue(false,"客户名称不能为空,请检查第 "+ JSON.toJSONString(dateNullIndexList)+" 行数据");
        }
        Set<String> clientNameSet = importDataList.stream().filter(item-> StringUtils.isNotBlank(item.getClientCode())).map(item->item.getClientName()).collect(Collectors.toSet());
        // 导入的客户中, 数据库中已有的授信记录
        Map<String,List<GrantCreditImportVO>> importClientGrantCreditMap = getImportClientGrantCreditList(clientNameSet);
        //按照客户名称分组 , 校验数据
        Map<String,List<GrantCreditImportVO>> sameClientNameMap = importDataList.stream().collect(Collectors.groupingBy(item->item.getClientName()));
        for (Map.Entry<String,List<GrantCreditImportVO>> entry : sameClientNameMap.entrySet()){
            String clientName = entry.getKey();
            // 同一客户授信数据
            List<GrantCreditImportVO> sameClientList = entry.getValue();
            // 已校验日期数据
            List<GrantCreditImportVO> hasCheckDateList = new ArrayList<>();
            // 该客户在数据库中已存在的授信记录
            List<GrantCreditImportVO> list = importClientGrantCreditMap.get(clientName);
            if (CollectionUtils.isNotEmpty(list)) {
                // 数据库中授信记录都是合法的(已校验的数据)
                hasCheckDateList.addAll(list);
            }
            for (GrantCreditImportVO vo : sameClientList){
                // 校验导入的授信日期不能重叠
                if(CollectionUtils.isNotEmpty(hasCheckDateList)){
                    // 授信日重叠
                    List<GrantCreditImportVO> overlapList = hasCheckDateList.stream().filter(item->{
                        boolean startFlag = item.getStartDate().compareTo(vo.getStartDate())<=0 && item.getEndDate().compareTo(vo.getStartDate())>=0;
                        boolean endFlag = item.getStartDate().compareTo(vo.getEndDate())<=0 && item.getEndDate().compareTo(vo.getEndDate())>=0;
                        return startFlag || endFlag ;
                    }).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(overlapList)){
                        BussinessException.E_700102.assertTrue(false,"客户 : "+vo.getClientName()+"授信日期重合了,请检查第 "+(vo.getRowIndex()+2)+" 行附近的数据");
                    }
                } else {
                    hasCheckDateList.add(vo);
                }
            }
        }
        List<Client> clientList = clientService.selectByClientNameSet(clientNameSet);
        // key=用户名称 value=用户ID
        Map<String,Integer> userMap = userClient.getUserList().stream().collect(Collectors.toMap(item->item.getName(),item->item.getId(),(v1,v2)->v2));
        // 客户map , key=客户名称 , value= 客户ID
        Map<String,Integer> clientMap= clientList.stream().collect(Collectors.toMap(item->item.getName(),item->item.getId(),(v1,v2)->v2));
        List<GrantCredit> entityList = CglibUtil.copyList(importDataList,GrantCredit::new,(vo,db)->{
            // 给客户授信
            db.setDirection(0);
            db.setClientId(clientMap.get(vo.getClientName()));
            if (userMap.containsKey(vo.getCreatorName())){
                db.setCreatorId(userMap.get(vo.getCreatorName()));
            }
            if (userMap.containsKey(vo.getUpdatorName())){
                db.setUpdatorId(userMap.get(vo.getUpdatorName()));
            }
        });
        this.saveBatch(entityList);
        return "导入成功";
    }

    /**
     * 导入的客户中, 数据库中已有的授信记录
     * @param clientNameSet  客户名称
     * @return 返回客户授信记录
     */
    public Map<String,List<GrantCreditImportVO>> getImportClientGrantCreditList (Set<String> clientNameSet) {
        // 根据导入的客户编号, 查询所有的客户
        List<Client> clientList = clientService.selectByClientNameSet(clientNameSet);
        if (clientNameSet.size() != clientList.size()) {
            Set<String> clientNameSetTemp = clientList.stream().map(item->item.getName()).collect(Collectors.toSet());
            clientNameSet.removeAll(clientNameSetTemp);
            BussinessException.E_700102.assertTrue(false,"未查询到对应客户 : "+JSON.toJSONString(clientNameSet));
        }
        // 客户map , key = 客户ID value=客户名称
        Map<Integer,String> clientmap = clientList.stream().collect(Collectors.toMap(item->item.getId(),item->item.getName(),(v1,v2)->v2));
        Set<Integer> clientIdSet = clientList.stream().map(item->item.getId()).collect(Collectors.toSet());
        LambdaQueryWrapper<GrantCredit> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GrantCredit::getDirection,0);
        lambdaQueryWrapper.eq(GrantCredit::getIsDeleted,IsDeletedEnum.NO);
        lambdaQueryWrapper.in(GrantCredit::getClientId,clientIdSet);
        // 导入的客户中, 数据库中已有的授信记录
        List<GrantCredit> clientGrantCreditListTemp = grantCreditMapper.selectList(lambdaQueryWrapper);
        List<GrantCreditImportVO> clientGrantCreditList = CglibUtil.copyList(clientGrantCreditListTemp,GrantCreditImportVO::new,(db,vo)->{
            vo.setClientName(clientmap.get(db.getClientId()));
        });
        Map<String,List<GrantCreditImportVO>> returnMap = clientGrantCreditList.stream().collect(Collectors.groupingBy(item->item.getClientName()));
        return returnMap;
    }

    /**
     * 组转导入数据
     * @param importVO      待填充数据对象
     * @param columnIndex   列下标
     * @param key           表头
     * @param value         单元格值
     */
    private void setObjectFiledValue(GrantCreditImportVO importVO, int columnIndex, String key, String value) {
        switch (columnIndex) {
            // 审批状态
            case 0:
                importVO.setApprovalStatus(GrantCreditApprovalStatusEnum.getCapitalStatusByDesc(value));
                break;
            // 审批角色 (此列忽略)
            case 1:
                break;
            // 客户编号
            case 2:
                importVO.setClientCode(value);
                break;
            // 客户名称
            case 3:
                importVO.setClientName(value);
                break;
            // 授信额度
            case 4:
                if(value != null) {
                    importVO.setAmount(BigDecimalUtil.getString2BigDecimal(value));
                } else {
                    importVO.setAmount(BigDecimal.ZERO);
                }
                break;
            // 客户审批规模
            case 5:
                if(value != null) {
                    importVO.setApprovalScale(BigDecimalUtil.getString2BigDecimal(value));
                } else {
                    importVO.setApprovalScale(BigDecimal.ZERO);
                }
                break;
            // 生效日期
            case 6:
                if(value != null) {
                    LocalDate date = getLocalDate(value);
                    importVO.setStartDate(date);
                }
                break;
            // 到期日期
            case 7:
                if(value != null) {
                    LocalDate date = getLocalDate(value);
                    importVO.setEndDate(date);
                }
                break;
            // 说明
            case 8:
                importVO.setRemark(value);
                break;
            // 创建人名称
            case 9:
                importVO.setCreatorName(value);
                break;
            // 操作人名称
            case 10:
                importVO.setUpdatorName(value);
                break;
            default:
                log.warn("多余列: "+key);
        }
    }
    /**
     * 字符串转LocalDateTime
     * @param value 待转换的字符串日期
     * @return 返回时间
     */
    public LocalDate getLocalDate(String value){
        if (StringUtils.isNotBlank(value)) {
            DateTimeFormatter dateTimeFormatter;
            value = value.trim();
            if (value.length() <= 10) { // 日期只有年月日,没有时分秒
                value+=" 00:00:00"; // 加上时分秒,方便转换
            }
            if (value.contains("-")){ // yyyy-MM-dd HH:mm:ss
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(value,dateTimeFormatter).toLocalDate();
            } else if(value.contains("/")){// yyyy/MM/dd HH:mm:ss
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                return LocalDateTime.parse(value,dateTimeFormatter).toLocalDate();
            } else if(value.contains("年")){// yyyy年MM月dd日 HH:mm:ss
                dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
                return LocalDateTime.parse(value,dateTimeFormatter).toLocalDate();
            }
        }
        return null;
    }
}
