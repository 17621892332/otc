package org.orient.otc.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordAddDto;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordDetailDto;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordPageDto;
import org.orient.otc.system.entity.ClientDataChangeRecord;
import org.orient.otc.system.service.ClientDataChangeRecordService;
import org.orient.otc.system.vo.ClientDataChangeRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientDataChangeRecordServiceImpl extends ServiceImpl<BaseMapper<ClientDataChangeRecord>,ClientDataChangeRecord> implements ClientDataChangeRecordService {
    @Autowired
    ClientClient clientClient;
    @Autowired
    UserClient userClient;

    /**
     * 获取某一天的0点0分0秒
     * @param date 日期
     * @return 返回时间
     */
    public LocalDateTime getStartDateTime(LocalDate date){
        if (date != null) {
            return LocalDateTime.of(
                    date.getYear(),
                    date.getMonth(),
                    date.getDayOfMonth(),
                    0,
                    0,
                    0
            );
        } else {
            return  null;
        }
    }
    /**
     * 获取某一天的23点59分59秒
     * @param date 日期
     * @return 时间
     */
    public LocalDateTime getEndDateTime(LocalDate date){
        if (date != null) {
            return LocalDateTime.of(
                    date.getYear(),
                    date.getMonth(),
                    date.getDayOfMonth(),
                    23,
                    59,
                    59
            );
        } else {
            return  null;
        }
    }

    @Override
    public IPage<ClientDataChangeRecordVO> selectByPage(ClientDataChangeRecordPageDto dto) {
        LambdaQueryWrapper<ClientDataChangeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClientDataChangeRecord::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.ge(dto.getStartDate() != null, ClientDataChangeRecord::getCreateTime, getStartDateTime(dto.getStartDate()));
        lambdaQueryWrapper.le(dto.getEndDate() != null,ClientDataChangeRecord::getCreateTime, getEndDateTime(dto.getEndDate()));
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getTypeList()), ClientDataChangeRecord::getChangeType,dto.getTypeList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getUserIdList()),ClientDataChangeRecord::getCreatorId,dto.getUserIdList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getClientIdList()),ClientDataChangeRecord::getClientId,dto.getClientIdList());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(dto.getClientCode()),ClientDataChangeRecord::getClientCode, dto.getClientCode());
        IPage<ClientDataChangeRecord> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        // key = 客户ID , value = 客户 Obj
        Map<Integer,String> clientMap = new HashMap<>();
        // key = userID , value = user名称
        Map<Integer,String> userMap = new HashMap<>();
        if (ipage != null && CollectionUtils.isNotEmpty(ipage.getRecords())) {
            //获取所有客户id
            Set<Integer> clientIdSet = ipage.getRecords().stream().map(ClientDataChangeRecord::getClientId).collect(Collectors.toSet());
            clientMap =  clientClient.getClientMapByIds(clientIdSet);
            // 获取所有创建人ID
            Set<Integer> userIdSet = ipage.getRecords().stream().map(BaseEntity::getCreatorId).collect(Collectors.toSet());
            userMap =  userClient.getUserMapByIds(userIdSet);
        }
        Map<Integer, String> finalClientMap = clientMap;
        Map<Integer, String> finalUserMap = userMap;
        IPage<ClientDataChangeRecordVO> returnPage = ipage.convert(entity->{
            ClientDataChangeRecordVO vo = new ClientDataChangeRecordVO();
            try {
                String changeFields = entity.getChangeFields();
                if (StringUtils.isNotBlank(changeFields)) {
                    JSONArray res = JSON.parseArray(changeFields);
                    List<DiffObjectVO> list = res.toJavaList(DiffObjectVO.class);
                    vo.setChangeFieldObjectList(list);
                    // 获取变更key
                    Set<String> changeKeySet  = list.stream().map(DiffObjectVO::getName).collect(Collectors.toSet());
                    String changeKey = StringUtils.join(changeKeySet, ";");
                    vo.setChangeKey(changeKey);
                    vo.setClientName(finalClientMap.get(entity.getClientId()));
                    vo.setCreatorName(finalUserMap.get(entity.getCreatorId()));
                }
            } catch (Exception e) { // 解析异常打印
                e.printStackTrace();
            }
            BeanUtils.copyProperties(entity,vo);
            return vo;
        });
        return returnPage;
    }

    @Override
    public ClientDataChangeRecordVO getDetails(ClientDataChangeRecordDetailDto dto) {
        ClientDataChangeRecord entity = this.getById(dto.getId());
        ClientDataChangeRecordVO vo = new ClientDataChangeRecordVO();
        BeanUtils.copyProperties(entity,vo);
        String changeFields = vo.getChangeFields();
        if (StringUtils.isNotBlank(changeFields)) {
            // json字符串先解析成json数组
            JSONArray res = JSON.parseArray(changeFields);
            // 数组解析成对应的obj
            List<DiffObjectVO> list = res.toJavaList(DiffObjectVO.class);
            vo.setChangeFieldObjectList(list);
        }
        ClientVO clientVo = clientClient.getClientById(entity.getClientId());
        if(clientVo!=null) {
            vo.setClientName(clientVo.getName());
        }
        UserVo userVO = userClient.getUserById(entity.getCreatorId());
        if(userVO != null){
            vo.setCreatorName(userVO.getName());
        }
        return vo;
    }

    @Override
    public String add(ClientDataChangeRecordAddDto dto) {
        ClientDataChangeRecord entity = new ClientDataChangeRecord();
        BeanUtils.copyProperties(dto,entity);
        this.saveOrUpdate(entity);
        return "新增日志成功";
    }
}
