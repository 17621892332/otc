package org.orient.otc.market.feign;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.market.dto.CloseDatePriceByDateDto;
import org.orient.otc.api.market.dto.MarketCloseDataSaveDto;
import org.orient.otc.api.market.dto.MarketCodeDto;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.api.market.vo.MarketInfoVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.market.adapter.MarketAdapter;
import org.orient.otc.market.entity.MarketCloseData;
import org.orient.otc.market.exception.BussinessException;
import org.orient.otc.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/market")
public class MarketFeignController implements MarketClient {

    @Resource
    private UnderlyingManagerClient underlyingManagerClient;
    @Autowired
    private MarketService marketService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public BigDecimal getSettlementPriceByUnderlyingCode(String  underlyingCode) {
        return marketService.getSettlementPriceByUnderlyingCode(underlyingCode);
    }

    @Override
    public MarketInfoVO getLastMarketDataByCode(String underlyingCode) {
        return getLastMarketInfo(underlyingCode);
    }

    @Override
    public Map<String, BigDecimal> getAllCloseDatePriceByCode(MarketCodeDto dto) {
     String underlyingCode=   dto.getUnderlyingCode();
        String  cloePriceMapStr = stringRedisTemplate.opsForValue().get(RedisAdapter.ALL_CLOSE_DATE_PRICE_BY_CODE+underlyingCode);
        if (StringUtils.isBlank(cloePriceMapStr)) {
            Map<String, BigDecimal> closeDatePriceByCodeMap= marketService.getCloseDatePriceByCode(underlyingCode);
            stringRedisTemplate.opsForValue().set(RedisAdapter.ALL_CLOSE_DATE_PRICE_BY_CODE+underlyingCode, JSON.toJSONString(closeDatePriceByCodeMap),10, TimeUnit.MINUTES);
            return closeDatePriceByCodeMap;
        }else {
            return JSON.parseObject(cloePriceMapStr,new TypeReference<Map<String,BigDecimal>>(){});
        }
    }

    @Override
    public Map<String, BigDecimal> getLastPriceByUnderlyingCodeList(Set<String> underlyingCodeList) {
        Map<String, BigDecimal> priceMap = new HashMap<>();
        for (String underlyingCode: underlyingCodeList){
         MarketInfoVO marketInfoVO=   getLastMarketInfo(underlyingCode);
         priceMap.put(marketInfoVO.getInstrumentId().toUpperCase(),marketInfoVO.getLastPrice());
        }
        return priceMap;
    }

    @Override
    public Map<String, BigDecimal> getCloseMarketDataByDate(LocalDate closeDate) {
        return marketService.getCloseMarketDataByDate(closeDate);
    }

    @Override
    public Map<String, BigDecimal> getSettlementMarketDataByDate(LocalDate closeDate) {
        return marketService.getSettlementMarketDataByDate(closeDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    @Override
    public void updateShareMarket()  {
        try {
            marketService.getShareMarket();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public SettlementVO saveCloseMarketDate() {
      return  marketService.saveCloseMarketDate();
    }

    @Override
    public void loadYlCloseMarketData(List<MarketCloseDataSaveDto> list) {
        List<MarketCloseData> dbList= CglibUtil.copyList(list,MarketCloseData::new);
        marketService.loadYlCloseMarketData(dbList);
    }

    @Override
    public Map<String,BigDecimal> getClosePriceByDateAndCode(CloseDatePriceByDateDto dto) {
        return marketService.getClosePrice(dto);
    }

    private MarketInfoVO getLastMarketInfo(String underlyingCode){
        MarketInfoVO marketInfoVo = MarketAdapter.marketData.get(underlyingCode);
        //如果内存中没有就从redis中取
        if(Objects.isNull(marketInfoVo)) {
            UnderlyingManagerVO underlying = underlyingManagerClient.getUnderlyingByCode(underlyingCode);
            BussinessException.E_500001.assertTrue(Objects.nonNull(underlying),"合约代码"+underlyingCode+"不存在");
            if (underlying != null) {
                BussinessException.E_500101.assertTrue(stringRedisTemplate.hasKey(RedisAdapter.REAL_TIME_MARKET+ underlying.getExchangeUnderlyingCode()),underlyingCode);
                String s = stringRedisTemplate.opsForValue().get(RedisAdapter.REAL_TIME_MARKET+ underlying.getExchangeUnderlyingCode());
                marketInfoVo = JSONObject.parseObject(s, MarketInfoVO.class);
                if(marketInfoVo!=null){
                    MarketAdapter.marketData.put(marketInfoVo.getInstrumentId().toUpperCase(),marketInfoVo);
                }
            }
        }
        marketInfoVo.setLastPrice(marketInfoVo.getLastPrice().setScale(2, RoundingMode.HALF_UP));
        return marketInfoVo;
    }
}
