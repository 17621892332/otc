package org.orient.otc.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.system.dto.capitaldatachangerecord.CapitalDataChangeRecordAddDto;
import org.orient.otc.system.dto.capitaldatachangerecord.CapitalDataChangeRecordPageDto;
import org.orient.otc.system.entity.CapitalDataChangeRecord;
import org.orient.otc.system.mapper.CapitalDataChangeRecordMapper;
import org.orient.otc.system.service.CapitalDataChangeRecordService;
import org.orient.otc.system.vo.CapitalDataChangeRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CapitalDataChangeRecordServiceImpl extends ServiceImpl<CapitalDataChangeRecordMapper, CapitalDataChangeRecord> implements CapitalDataChangeRecordService {
    @Autowired
    CapitalDataChangeRecordMapper capitalDataChangeRecordMapper;

    @Autowired
    ClientClient clientClient;
    @Autowired
    UserClient userClient;

    @Override
    public IPage<CapitalDataChangeRecordVO> selectByPage(CapitalDataChangeRecordPageDto dto) {
        LambdaQueryWrapper<CapitalDataChangeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CapitalDataChangeRecord::getIsDeleted, IsDeletedEnum.NO)
                .eq(dto.getCapitalId()!=null,CapitalDataChangeRecord::getCapitalId,dto.getCapitalId())
                .eq(dto.getClientId()!=null,CapitalDataChangeRecord::getClientId,dto.getClientId())
                .eq(StringUtils.isNotBlank(dto.getTradeCode()),CapitalDataChangeRecord::getTradeCode,dto.getTradeCode())
                .eq(StringUtils.isNotBlank(dto.getUnderlyingCode()),CapitalDataChangeRecord::getUnderlyingCode,dto.getUnderlyingCode())
                .eq(dto.getDirection()!=null,CapitalDataChangeRecord::getDirection,dto.getDirection());
        IPage<CapitalDataChangeRecord> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        // key = 客户ID , value = 客户 Obj
        Map<Integer,String> clientMap = new HashMap<>();
        // key = userID , value = user名称
        Map<Integer,String> userMap = new HashMap<>();
        if (ipage != null && CollectionUtils.isNotEmpty(ipage.getRecords())) {
            //获取所有客户id
            Set<Integer> clientIdSet = ipage.getRecords().stream().map(CapitalDataChangeRecord::getClientId).collect(Collectors.toSet());
            clientMap =  clientClient.getClientMapByIds(clientIdSet);
            // 获取所有创建人ID
            Set<Integer> userIdSet = ipage.getRecords().stream().map(BaseEntity::getCreatorId).collect(Collectors.toSet());
            userMap =  userClient.getUserMapByIds(userIdSet);
        }
        Map<Integer, String> finalClientMap = clientMap;
        Map<Integer, String> finalUserMap = userMap;
        IPage<CapitalDataChangeRecordVO> returnPage = ipage.convert(entity->{
            CapitalDataChangeRecordVO vo = new CapitalDataChangeRecordVO();
            try {
                String changeFields = entity.getChangeFields();
                if (org.apache.commons.lang.StringUtils.isNotBlank(changeFields)) {
                    JSONArray res = JSON.parseArray(changeFields);
                    List<DiffObjectVO> list = res.toJavaList(DiffObjectVO.class);
                    vo.setChangeFieldObjectList(list);
                    // 获取变更key
                    Set<String> changeKeySet  = list.stream().map(DiffObjectVO::getName).collect(Collectors.toSet());
                    String changeKey = org.apache.commons.lang.StringUtils.join(changeKeySet, ";");
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
    public void add(CapitalDataChangeRecordAddDto addDto) {
        CapitalDataChangeRecord entiry = new CapitalDataChangeRecord();
        BeanUtils.copyProperties(addDto,entiry);
        this.saveOrUpdate(entiry);
    }
}
