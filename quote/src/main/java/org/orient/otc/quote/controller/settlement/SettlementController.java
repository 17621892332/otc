package org.orient.otc.quote.controller.settlement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.service.SettlementService;
import org.orient.otc.quote.service.TradeMngService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/settlement")
@Api(tags = "生命周期管理")
public class SettlementController {
    @Resource
    private SettlementService settlementService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TradeMngService tradeMngService;

    /**
     * 生成远期
     * @return 生成结果
     */
    @PostMapping("/updateTradeObsDatePrice")
    @ApiOperation("生成远期")
    public HttpResourceResponse<Boolean> updateTradeObsDatePrice() {
        LocalDate localDate=LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash().get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
        return HttpResourceResponse.success(settlementService.updateTradeObsDatePrice(localDate).getIsSuccess());
    }

    /**
     * 重新收场内盘
     * @param settlementDto 收盘日期
     * @return 结算结果
     */
    @PostMapping("/exchangeSettlement")
    @ApiOperation("场内重新收盘")
    public HttpResourceResponse<SettlementVO> exchangeSettlement(@RequestBody SettlementDTO settlementDto){
       settlementService.updateTodayPosData(settlementDto);
       return HttpResourceResponse.success(settlementService.saveExchangeTradeRiskInfo(settlementDto));
    }

    /**
     * 计算客户保证金
     * @param settlementDto 结算日期
     * @return key 交易代码 value 保证金
     */
    @PostMapping("/getTradeMargin")
    @ApiOperation("计算保证金")
    public HttpResourceResponse<Map<String, BigDecimal>> getTradeMarin(@RequestBody SettlementDTO settlementDto){
        return HttpResourceResponse.success(settlementService.getTradeMargin( tradeMngService.getSurvivalTradeByTradeDay(settlementDto.getSettlementDate())
                ,settlementDto.getSettlementDate()));
    }
}
