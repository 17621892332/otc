package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.orient.otc.api.client.feign.ClientClient;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.dto.trade.TradeDetailPageListDto;
import org.orient.otc.quote.entity.ObsTradeDetail;
import org.orient.otc.quote.mapper.ObsTradeDetailMapper;
import org.orient.otc.quote.service.TradeDetailService;
import org.orient.otc.quote.vo.trade.ObsTradeDetailVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TradeDetailServiceImpl extends ServiceImpl<BaseMapper<ObsTradeDetail>, ObsTradeDetail> implements TradeDetailService {
    @Autowired
    ObsTradeDetailMapper obsTradeDetailMapper;

    @Autowired
    ClientClient client;

    @Override
    public IPage<ObsTradeDetailVo> selectListByPage(TradeDetailPageListDto dto) {
        LambdaQueryWrapper<ObsTradeDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(!StringUtils.isEmpty(dto.getObsDate()),ObsTradeDetail::getObsDate,dto.getObsDate())
                .eq(!StringUtils.isEmpty(dto.getTradeCode()),ObsTradeDetail::getTradeCode,dto.getTradeCode())
                .eq(!StringUtils.isEmpty(dto.getClientId()),ObsTradeDetail::getClientId,dto.getClientId())
                .in(CollectionUtils.isNotEmpty(dto.getOptionTypeList()),ObsTradeDetail::getOptionType,dto.getOptionTypeList())
                .eq(!StringUtils.isEmpty(dto.getUnderlyingCode()),ObsTradeDetail::getUnderlyingCode,dto.getUnderlyingCode())
                .eq(ObsTradeDetail::getIsDeleted, IsDeletedEnum.NO);
        IPage<ObsTradeDetail> ipage = this.page(new Page<>(dto.getPageNo(),dto.getPageSize()),lambdaQueryWrapper);
        Set<Integer> clientIdsSet = ipage.getRecords().stream().map(item->item.getClientId()).collect(Collectors.toSet());
        Map<Integer, String> clientMap = getClientMap(clientIdsSet);
        IPage<ObsTradeDetailVo> returnPage = ipage.convert(item->{
            ObsTradeDetailVo vo = new ObsTradeDetailVo();
            BeanUtils.copyProperties(item,vo);
            // 期权类型
            if (null != item.getOptionType()) {
                vo.setOptionType(item.getOptionType().getDesc());
            }
            // 客户名称
            vo.setClientName(
                    clientMap.get(item.getClientId()));
            return vo;
        });
        return returnPage;
    }

    /** 获取客户名称
     *  key=客户id , value = 客户名称
     * @param ids
     * @return
     */
    public Map<Integer, String> getClientMap(Set<Integer> ids) {
        List<ClientVO> list = client.getClientListByIds(ids);
        return list.stream().collect(Collectors.toMap(ClientVO::getId, ClientVO::getName,(v1, v2)->v2));
    }

}
