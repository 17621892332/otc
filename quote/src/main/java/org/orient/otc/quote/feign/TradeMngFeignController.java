package org.orient.otc.quote.feign;

import cn.hutool.extra.cglib.CglibUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.orient.otc.api.quote.dto.ProfitLossAppraisementDto;
import org.orient.otc.api.quote.dto.RiskVolUpdateDto;
import org.orient.otc.api.quote.dto.SyncUpdateDto;
import org.orient.otc.api.quote.feign.TradeMngClient;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.quote.entity.TradeMng;
import org.orient.otc.quote.service.TradeMngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dzrh
 */
@RestController
@RequestMapping(value = "trade")
public class TradeMngFeignController implements TradeMngClient {
    @Autowired
    TradeMngService tradeMngService;

    /**
     * @return
     */
    @Override
    public TradeMngVO getTraderById(Integer id) {
        return tradeMngService.getVoById(id, TradeMngVO.class);
    }

    @Override
    public TradeMngVO getTraderByTradeCode(String tradeCode) {
        LambdaQueryWrapper<TradeMng> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeMng::getTradeCode,tradeCode);
        queryWrapper.eq(TradeMng::getIsDeleted, IsDeletedEnum.NO);
        return tradeMngService.getVoOne(queryWrapper, TradeMngVO.class);
    }

    /**
     * @return
     */
    @Override
    public List<TradeMngVO> getNotSyncTradeList() {
        List<TradeMng> list = tradeMngService.queryNotSyncTradeList();
        return CglibUtil.copyList(list,TradeMngVO::new);
    }


    @Override
    public Boolean saveTradeByYl(TradeMngVO tradeMngVO) {
        return tradeMngService.saveTradeMngByYl(tradeMngVO);
    }

    /**
     * @param dto
     */
    @Override
    public Boolean updateSync(SyncUpdateDto dto) {
        LambdaUpdateWrapper<TradeMng> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(TradeMng::getId,dto.getIds());
        updateWrapper.set(TradeMng::getIsSync,dto.getSyncStatus());
        updateWrapper.set(TradeMng::getSyncMsg,dto.getMsg());
      return  tradeMngService.update(updateWrapper);
    }

    @Override
    public Boolean updateRiskVol(RiskVolUpdateDto riskVolUpdateDto) {
        LambdaUpdateWrapper<TradeMng> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TradeMng::getTradeCode,riskVolUpdateDto.getTradeCode());
        updateWrapper.set(TradeMng::getRiskVol,riskVolUpdateDto.getRiskVol());
        return  tradeMngService.update(updateWrapper);
    }

    @Override
    public BigDecimal getRealizeProfitLoss(ProfitLossAppraisementDto dto) {
        return tradeMngService.getRealizeProfitLoss(dto);
    }
}
