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
import org.orient.otc.api.user.feign.AssetUnitClient;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.core.vo.DiffObjectVO;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDetailDto;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDTO;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordPageDto;
import org.orient.otc.system.entity.TradeDataChangeRecord;
import org.orient.otc.system.mapper.TradeDataChangeRecordMapper;
import org.orient.otc.system.service.TradeDataChangeRecordService;
import org.orient.otc.system.vo.TradeDataChangeRecordVO;
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
public class TradeDataChangeRecordServiceImpl extends ServiceImpl<BaseMapper<TradeDataChangeRecord>,TradeDataChangeRecord> implements TradeDataChangeRecordService {
    @Autowired
    TradeDataChangeRecordMapper tradeDataChangeRecordMapper;
    @Autowired
    ClientClient clientClient;
    @Autowired
    UserClient userClient;
    @Autowired
    AssetUnitClient assetUnitClient;

    @Override
    public String add(TradeDataChangeRecordDTO dto) {
        TradeDataChangeRecord entity = new TradeDataChangeRecord();
        BeanUtils.copyProperties(dto,entity);
        this.saveOrUpdate(entity);
        return "新增日志成功";
    }

    /**
     * 获取某一天的0点0分0秒
     * @param date
     * @return
     */
    public LocalDateTime getStartDateTime(LocalDate date){
        if (date != null) {
            LocalDateTime dateTime = LocalDateTime.of(
                    date.getYear(),
                    date.getMonth(),
                    date.getDayOfMonth(),
                    0,
                    0,
                    0
            );
            return dateTime;
        } else {
            return  null;
        }
    }
    /**
     * 获取某一天的23点59分59秒
     * @param date
     * @return
     */
    public LocalDateTime getEndDateTime(LocalDate date){
        if (date != null) {
            LocalDateTime dateTime = LocalDateTime.of(
                    date.getYear(),
                    date.getMonth(),
                    date.getDayOfMonth(),
                    23,
                    59,
                    59
            );
            return dateTime;
        } else {
            return  null;
        }
    }

    @Override
    public IPage<TradeDataChangeRecordVO> selectByPage(TradeDataChangeRecordPageDto dto) {
        LambdaQueryWrapper<TradeDataChangeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TradeDataChangeRecord::getIsDeleted, IsDeletedEnum.NO);
        lambdaQueryWrapper.ge(dto.getStartDate() != null,TradeDataChangeRecord::getCreateTime, getStartDateTime(dto.getStartDate()));
        lambdaQueryWrapper.le(dto.getEndDate() != null,TradeDataChangeRecord::getCreateTime, getEndDateTime(dto.getEndDate()));
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(dto.getTradeCode()),TradeDataChangeRecord::getTradeCode,dto.getTradeCode());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getClientIdList()),TradeDataChangeRecord::getClientId,dto.getClientIdList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getAssetunitIdList()),TradeDataChangeRecord::getAssetunitId,dto.getAssetunitIdList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getTypeList()),TradeDataChangeRecord::getChangeType,dto.getTypeList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getTradeStateList()),TradeDataChangeRecord::getTradeState,dto.getTradeStateList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getUserIdList()),TradeDataChangeRecord::getCreatorId,dto.getUserIdList());
        IPage<TradeDataChangeRecord> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        // key = 客户ID , value = 客户 Obj
        Map<Integer,String> clientMap = new HashMap<>();
        // key = userID , value = user名称
        Map<Integer,String> userMap = new HashMap<>();
        // key=簿记ID , value=簿记名称
        Map<Integer,String> assetunitMap = new HashMap<>();
        if (ipage != null && CollectionUtils.isNotEmpty(ipage.getRecords())) {
            //获取所有客户id
            Set<Integer> clientIdSet = ipage.getRecords().stream().map(TradeDataChangeRecord::getClientId).collect(Collectors.toSet());
            clientMap =  clientClient.getClientMapByIds(clientIdSet);
            // 获取所有创建人ID
            Set<Integer> userIdSet = ipage.getRecords().stream().map(BaseEntity::getCreatorId).collect(Collectors.toSet());
            userMap =  userClient.getUserMapByIds(userIdSet);
            // 获取所有簿记ID
            Set<Integer> assetunitIdSet = ipage.getRecords().stream().map(TradeDataChangeRecord::getAssetunitId).collect(Collectors.toSet());
            assetunitMap = assetUnitClient.getAssetUnitList(assetunitIdSet).stream().collect(Collectors.toMap(AssetunitVo::getId, AssetunitVo::getName));
        }
        Map<Integer, String> finalClientMap = clientMap;
        Map<Integer, String> finalUserMap = userMap;
        Map<Integer, String> finalAssetunitMap = assetunitMap;
        IPage<TradeDataChangeRecordVO> returnPage = ipage.convert(entity->{
            TradeDataChangeRecordVO vo = new TradeDataChangeRecordVO();
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
                    vo.setAssetunitName(finalAssetunitMap.get(entity.getAssetunitId()));
                    if (entity.getBuyOrSell()!=null) {
                        vo.setBuyOrSell(entity.getBuyOrSell().getDesc());
                    }
                    if (entity.getOptionType()!=null) {
                        vo.setOptionType(entity.getOptionType().getDesc());
                    }
                }
            } catch (Exception e) { // 解析异常打印
                e.printStackTrace();
            }
            BeanUtils.copyProperties(entity,vo);
            if (entity.getTradeState()!=null) {
                vo.setTradeState(entity.getTradeState().getDesc());
            }
            return vo;
        });
        return returnPage;
    }


    @Override
    public TradeDataChangeRecordVO getDetails(TradeDataChangeRecordDetailDto dto) {
        TradeDataChangeRecord tradeDataChangeRecord = this.getById(dto.getId());
        TradeDataChangeRecordVO vo = new TradeDataChangeRecordVO();
        BeanUtils.copyProperties(tradeDataChangeRecord,vo);
        String changeFields = vo.getChangeFields();
        if (StringUtils.isNotBlank(changeFields)) {
            JSONArray res = JSON.parseArray(changeFields);
            List<DiffObjectVO> list = res.toJavaList(DiffObjectVO.class);
            vo.setChangeFieldObjectList(list);
        }
        ClientVO clientVo = clientClient.getClientById(tradeDataChangeRecord.getClientId());
        if(clientVo!=null) {
            vo.setClientName(clientVo.getName());
        }
        UserVo userVO = userClient.getUserById(tradeDataChangeRecord.getCreatorId());
        if(userVO != null){
            vo.setCreatorName(userVO.getName());
        }
        AssetunitVo assetunitVo = assetUnitClient.getAssetunitById(tradeDataChangeRecord.getAssetunitId());
        if (assetunitVo != null) {
            vo.setAssetunitName(assetunitVo.getName());
        }
        if (tradeDataChangeRecord.getTradeState()!=null) {
            vo.setTradeState(tradeDataChangeRecord.getTradeState().getDesc());
        }
        if (tradeDataChangeRecord.getBuyOrSell()!=null) {
            vo.setBuyOrSell(tradeDataChangeRecord.getBuyOrSell().getDesc());
        }
        if (tradeDataChangeRecord.getOptionType()!=null) {
            vo.setOptionType(tradeDataChangeRecord.getOptionType().getDesc());
        }

        return vo;
    }
}
