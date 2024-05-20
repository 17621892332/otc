package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.quote.enums.OptionCombTypeEnum;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.riskearlywaring.RiskEarlyWarningAddDto;
import org.orient.otc.quote.dto.riskearlywaring.RiskEarlyWarningItemDto;
import org.orient.otc.quote.dto.riskearlywaring.RiskEarlyWarningPageDto;
import org.orient.otc.quote.entity.RiskEarlyWarning;
import org.orient.otc.quote.mapper.RiskEarlyWarningMapper;
import org.orient.otc.quote.service.RiskEarlyWarningService;
import org.orient.otc.quote.vo.riskearlywaring.RiskEarlyWarningVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 风险预警实现
 */
@Service
public class RiskEarlyWarningServiceImpl extends ServiceImpl<RiskEarlyWarningMapper, RiskEarlyWarning> implements RiskEarlyWarningService {
    @Resource
    private  ClientClient client;
    @Resource
   private UserClient userClient;

    @Override
    public IPage<RiskEarlyWarningVO> selectListByPage(RiskEarlyWarningPageDto dto) {
        LambdaQueryWrapper<RiskEarlyWarning> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getTraderIdList()),RiskEarlyWarning::getTraderId,dto.getTraderIdList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getClientIdList()),RiskEarlyWarning::getClientId,dto.getClientIdList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getOptionTypeList()),RiskEarlyWarning::getOptionType,dto.getOptionTypeList());
        lambdaQueryWrapper.in(CollectionUtils.isNotEmpty(dto.getUnderlyingCodeList()),RiskEarlyWarning::getUnderlyingCode,dto.getUnderlyingCodeList());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getWarningText()),RiskEarlyWarning::getWarningText,dto.getWarningText());
        lambdaQueryWrapper.eq(StringUtils.isNotBlank(dto.getType()),RiskEarlyWarning::getType,dto.getType());
        lambdaQueryWrapper.eq(dto.getWaringStatus()!=null,RiskEarlyWarning::getWaringStatus,dto.getWaringStatus());
        if (dto.getWaringTimeStart() != null) {
            lambdaQueryWrapper.ge(RiskEarlyWarning::getWaringTime,dto.getWaringTimeStart().atStartOfDay());
        }
        if (dto.getWaringTimeEnd() != null) {
            lambdaQueryWrapper.le(RiskEarlyWarning::getWaringTime,dto.getWaringTimeEnd().atTime(23,59,59));
        }
        // 创建时间倒序排列
        lambdaQueryWrapper.orderByDesc(RiskEarlyWarning::getCreateTime);
        lambdaQueryWrapper.eq(RiskEarlyWarning::getIsDeleted, IsDeletedEnum.NO);
        IPage<RiskEarlyWarning> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        // key=客户ID, value=客户名称
        Map<Integer,String>  clientMap = new HashMap<>();
        // key=交易员ID, value=交易员名称
        Map<Integer,String>  tradeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ipage.getRecords())) {
            Set<Integer> clientIdSet = ipage.getRecords().stream().map(RiskEarlyWarning::getClientId).collect(Collectors.toSet());
            clientMap = client.getClientMapByIds(clientIdSet);
            Set<Integer> tradeIdSet = ipage.getRecords().stream().map(RiskEarlyWarning::getTraderId).collect(Collectors.toSet());
            tradeMap = userClient.getUserMapByIds(tradeIdSet);
        }
        Map<Integer, String> finalTradeMap = tradeMap;
        Map<Integer, String> finalClientMap = clientMap;
        return ipage.convert(item->{
            RiskEarlyWarningVO vo = new RiskEarlyWarningVO();
            BeanUtils.copyProperties(item,vo);
            vo.setTraderName(finalTradeMap.get(item.getTraderId()));
            vo.setClientName(finalClientMap.get(item.getClientId()));
            if (item.getOptionType()!=null) {
                // 期权类型或者组合类型
                String optionTypeKey = item.getOptionType();
                OptionTypeEnum optionTypeEnum = OptionTypeEnum.getTradeTypeByName(optionTypeKey);
                if (optionTypeEnum != null) {
                    vo.setOptionType(optionTypeEnum.getDesc());
                } else {
                    OptionCombTypeEnum optionCombTypeEnum = OptionCombTypeEnum.getTradeTypeByName(optionTypeKey);
                    if (optionCombTypeEnum != null) {
                        vo.setOptionType(optionCombTypeEnum.getDesc());
                    }
                }
            }
            return vo;
        });
    }

    @Override
    @Transactional
    public String add(RiskEarlyWarningAddDto dto) {
        List<RiskEarlyWarning> list = new ArrayList<>();
        for (RiskEarlyWarningItemDto item : dto.getList()) {
            RiskEarlyWarning entity = new RiskEarlyWarning();
            BeanUtils.copyProperties(item,entity);
            entity.setWaringTime(LocalDateTime.now());
            entity.setWaringStatus(0);
            entity.setId(null);
            entity.setType("定价计算");
            list.add(entity);
        }
        this.saveOrUpdateBatch(list);
        return "新增风险预警信息成功";
    }
}
