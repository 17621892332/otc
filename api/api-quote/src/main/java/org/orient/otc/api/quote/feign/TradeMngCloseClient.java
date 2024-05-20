package org.orient.otc.api.quote.feign;

import org.orient.otc.api.quote.dto.SyncUpdateDto;
import org.orient.otc.api.quote.vo.TradeCloseMngFeignVo;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author dzrh
 */
@FeignClient(value = "quoteserver",path = "/tradeClose", contextId ="tradeClose")
public interface TradeMngCloseClient {
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getNotSyncTradeCloseList")
    List<TradeCloseMngFeignVo> getNotSyncTradeCloseList();

    /**
     * 保存从镒链同步回来的数据
     * @param list
     * @return
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveBatch")
    boolean saveBatch(@RequestBody List<TradeCloseMngFeignVo> list);

    /**
     * 更新同步交易记录至镒链的同步状态
     * @param dto 请求对象
     * @return true 更新成功 false 更新失败
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateSync")
    Boolean updateSync(@RequestBody SyncUpdateDto dto);


}
