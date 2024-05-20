package org.orient.otc.quote.feign;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.orient.otc.api.quote.dto.SyncUpdateDto;
import org.orient.otc.api.quote.feign.TradeMngCloseClient;
import org.orient.otc.api.quote.vo.TradeCloseMngFeignVo;
import org.orient.otc.quote.entity.TradeCloseMng;
import org.orient.otc.quote.service.TradeCloseMngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dzrh
 */
@RestController
@RequestMapping(value = "tradeClose")
public class TradeCloseMngFeignController implements TradeMngCloseClient {
    @Autowired
    TradeCloseMngService tradeCloseMngService;



    @Override
    public List<TradeCloseMngFeignVo> getNotSyncTradeCloseList() {
        return tradeCloseMngService.queryNotSyncTradeList();
    }

    @Override
    public boolean saveBatch(List<TradeCloseMngFeignVo> list) {
        return tradeCloseMngService.saveOrUpdateByCode(list);
    }


    @Override
    public Boolean updateSync(SyncUpdateDto dto) {
        LambdaUpdateWrapper<TradeCloseMng> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(TradeCloseMng::getId,dto.getIds());
        updateWrapper.set(TradeCloseMng::getIsSync,dto.getSyncStatus());


        return  tradeCloseMngService.update(updateWrapper);
    }

}
