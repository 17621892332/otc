package org.orient.otc.dm.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.enums.UnderlyingState;
import org.orient.otc.api.dm.vo.UnderlyingQuoteVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.dm.dto.underlyingQuote.UnderlyingQuoteUpdateDTO;
import org.orient.otc.dm.entity.UnderlyingManager;
import org.orient.otc.dm.entity.UnderlyingQuote;
import org.orient.otc.dm.exception.BussinessException;
import org.orient.otc.dm.mapper.UnderlyingManagerMapper;
import org.orient.otc.dm.mapper.UnderlyingQuoteMapper;
import org.orient.otc.dm.service.UnderlyinQuoteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合约服务实现
 */
@Service
public class UnderlyingQuoteServiceImpl extends ServiceImpl<UnderlyingQuoteMapper, UnderlyingQuote> implements UnderlyinQuoteService {
    @Resource
    private UnderlyingQuoteMapper underlyingQuoteMapper;
    @Resource
    private UnderlyingManagerMapper underlyingManagerMapper;

    @Override
    public List<UnderlyingQuoteVO> getList() {
        // 获取基础报价数据列表
        List<UnderlyingQuoteVO> list = underlyingQuoteMapper.selectJoinData();
        Set<Integer> varietySet = list.stream().map(UnderlyingQuoteVO::getVarietyId).collect(Collectors.toSet());
        // 构建查询条件
        LambdaQueryWrapper<UnderlyingManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UnderlyingManager::getVarietyId, varietySet);
        queryWrapper.eq(UnderlyingManager::getUnderlyingState, UnderlyingState.Live);
        queryWrapper.eq(UnderlyingManager::getIsDeleted, IsDeletedEnum.NO);
        // 查询相关的 UnderlyingManager 数据
        List<UnderlyingManager> underlyingManagers = underlyingManagerMapper.selectList(queryWrapper);
        Map<Integer, List<UnderlyingManager>> underlyingMap = underlyingManagers.stream().collect(Collectors.groupingBy(UnderlyingManager::getVarietyId));
        for (UnderlyingQuoteVO underlyingQuoteVo : list) {
            // 获取逗号分隔的字符串 underlyingCodes
            String underlyingCode = underlyingQuoteVo.getUnderlyingCodes();
            // 将 underlyingCodes 拆分成 List<String>，去除空格
            List<String> underlyingCodesList = StringUtils.isNotBlank(underlyingCode)
                    ? Arrays.asList(underlyingCode.split(","))
                    : Collections.emptyList();
            // 提取 UnderlyingManager 的 underlyingCode 属性，构建一个列表
            List<String> underlyingCodes = underlyingMap.get(underlyingQuoteVo.getVarietyId()).stream()
                    .map(UnderlyingManager::getUnderlyingCode)
                    .collect(Collectors.toList());
            // 设置 UnderlyingManager 列表到 UnderlyingQuoteVo 对象中
            underlyingQuoteVo.setUnderlyingCodesList(underlyingCodesList);
            underlyingQuoteVo.setUnderlyingManagerList(underlyingCodes);
        }
        return list;
    }

    @Override
    public List<UnderlyingQuoteVO> getQuoteList() {
        // 获取基础报价数据列表
        List<UnderlyingQuoteVO> list = underlyingQuoteMapper.selectJoinData();
        list = list.stream().filter(a -> a.getNeedQuote() != null && a.getNeedQuote()).collect(Collectors.toList());
        for (UnderlyingQuoteVO underlyingQuoteVo : list) {
            // 获取逗号分隔的字符串 underlyingCodes
            String underlyingCode = underlyingQuoteVo.getUnderlyingCodes();
            // 将 underlyingCodes 拆分成 List<String>，去除空格
            List<String> underlyingCodesList = StringUtils.isNotBlank(underlyingCode)
                    ? Arrays.asList(underlyingCode.split(","))
                    : Collections.emptyList();
            // 设置 UnderlyingManager 列表到 UnderlyingQuoteVo 对象中
            underlyingQuoteVo.setUnderlyingCodesList(underlyingCodesList);
        }
        return list;
    }


    @Override
    @Transactional
    public String update(List<UnderlyingQuoteUpdateDTO> dtoList) {
        for (UnderlyingQuoteUpdateDTO dto : dtoList) {
            if (dto.getNeedQuote() != null && dto.getNeedQuote()) {
                BussinessException.E_600103.assertTrue(!dto.getUnderlyingCodesList().isEmpty(), "合约不能为空");
            }
        }
        List<UnderlyingQuote> dbList = CglibUtil.copyList(dtoList, UnderlyingQuote::new, (vo, db) -> {
            // 拼接 underlyingCodesList 到 underlyingCodes 字段
            if (CollectionUtils.isNotEmpty(vo.getUnderlyingCodesList())) {
                db.setUnderlyingCodes(String.join(",", vo.getUnderlyingCodesList()));
            }else {
                db.setUnderlyingCodes("");
            }
        });
        // 保存或更新 UnderlyingQuote 对象
        this.saveOrUpdateBatch(dbList);
        return "修改报价合约信息成功";
    }

}
