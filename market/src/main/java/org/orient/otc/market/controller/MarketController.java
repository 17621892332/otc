package org.orient.otc.market.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.vo.MarketInfoVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.NoCheckLogin;
import org.orient.otc.market.adapter.MarketAdapter;
import org.orient.otc.market.dto.MarketDataBatchDto;
import org.orient.otc.market.dto.MarketDataDTO;
import org.orient.otc.market.exception.BussinessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/market")
@Api(tags = "行情")
public class MarketController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UnderlyingManagerClient underlyingManagerClient;

    /**
     * 通过合约code获取合约的行情
     * @param marketDataDto 合约代码
     * @return 最新行情
     */
    @PostMapping("/getMarketByCode")
    @ApiOperation("通过合约code获取合约的行情")
    @NoCheckLogin
    public HttpResourceResponse<MarketInfoVO> getMarketByCode(@RequestBody MarketDataDTO marketDataDto){

        MarketInfoVO marketInfoVo = MarketAdapter.marketData.get(marketDataDto.getUnderlyingCode());
        //如果内存中没有就从redis中取
        if(Objects.isNull(marketInfoVo)) {
            UnderlyingManagerVO underlying = underlyingManagerClient.getUnderlyingByCode(marketDataDto.getUnderlyingCode());
            BussinessException.E_500001.assertTrue(Objects.nonNull(underlying),"合约代码"+marketDataDto.getUnderlyingCode()+"不存在");
            if (underlying != null) {
                BussinessException.E_500101.assertTrue(stringRedisTemplate.hasKey(RedisAdapter.REAL_TIME_MARKET+ underlying.getExchangeUnderlyingCode()),marketDataDto.getUnderlyingCode());
                marketInfoVo = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(RedisAdapter.REAL_TIME_MARKET+ underlying.getExchangeUnderlyingCode()), MarketInfoVO.class);
                if(marketInfoVo!=null){
                    MarketAdapter.marketData.put(marketInfoVo.getInstrumentId().toUpperCase(),marketInfoVo);
                }
            }
        }
        marketInfoVo.setLastPrice(marketInfoVo.getLastPrice());
        return HttpResourceResponse.success(marketInfoVo);
    }

    /**
     * 通过合约code数组获取合约的行情
     * @param dto 合约代码
     * @return 最新行情
     */
    @PostMapping("/getMarketByCodeList")
    @ApiOperation("通过合约codeList获取合约的行情")
    @NoCheckLogin
    public HttpResourceResponse<List<MarketInfoVO>> getMarketByCodeList(@RequestBody MarketDataBatchDto dto) {
        List<MarketInfoVO> returnList = new ArrayList<>();
        List<UnderlyingManagerVO> list = underlyingManagerClient.getUnderlyingByCodes(dto.getUnderlyingCodeList());
        for(UnderlyingManagerVO underlying : list) {
            MarketInfoVO marketInfoVo = MarketAdapter.marketData.get(underlying.getExchangeUnderlyingCode());
            //如果内存中没有就从redis中取
            if(Objects.isNull(marketInfoVo)) {
                BussinessException.E_500101.assertTrue(stringRedisTemplate.hasKey(RedisAdapter.REAL_TIME_MARKET+ underlying.getExchangeUnderlyingCode()),underlying.getUnderlyingCode());
                String s = stringRedisTemplate.opsForValue().get(RedisAdapter.REAL_TIME_MARKET+ underlying.getExchangeUnderlyingCode());
                marketInfoVo = JSONObject.parseObject(s, MarketInfoVO.class);
                if(marketInfoVo!=null){
                    MarketAdapter.marketData.put(marketInfoVo.getInstrumentId().toUpperCase(),marketInfoVo);
                }

            }
            marketInfoVo.setLastPrice(marketInfoVo.getLastPrice());
            returnList.add(marketInfoVo);
        }
        return HttpResourceResponse.success(returnList);
    }
}
