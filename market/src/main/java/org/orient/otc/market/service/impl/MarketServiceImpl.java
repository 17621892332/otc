package org.orient.otc.market.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.market.dto.CloseDatePriceByDateDto;
import org.orient.otc.api.market.vo.MarketInfoVO;
import org.orient.otc.api.system.feign.SystemClient;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.security.adapter.AuthAdapter;
import org.orient.otc.market.adapter.MarketAdapter;
import org.orient.otc.market.dto.AuthDTO;
import org.orient.otc.market.dto.RiceQuantApiDTO;
import org.orient.otc.market.entity.MarketCloseData;
import org.orient.otc.market.exception.BussinessException;
import org.orient.otc.market.mapper.MarketCloseDataMapper;
import org.orient.otc.market.service.MarketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 行情服务实现
 */
@Service
@Slf4j
public class MarketServiceImpl extends ServiceImpl<BaseMapper<MarketCloseData>, MarketCloseData> implements MarketService {
    @Resource
    private MarketCloseDataMapper marketCloseDataMapper;
    @Resource
    private SystemClient systemClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${riceQuant.url}")
    private String riceQuantUrl;

    @Value("${riceQuant.account}")
    private String riceQuantAccount;

    @Value("${riceQuant.password}")
    private String riceQuantPassword;

    @Override
    public BigDecimal getSettlementPriceByUnderlyingCode(String underlyingCode) {
       LambdaQueryWrapper<MarketCloseData> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(MarketCloseData::getInstrumentID,underlyingCode);
       queryWrapper.orderByDesc(MarketCloseData::getTradingDay);
       queryWrapper.last( " limit 1");
        MarketCloseData marketCloseData = marketCloseDataMapper.selectOne(queryWrapper);
        BussinessException.E_500103.assertTrue(Objects.nonNull(marketCloseData));
        return marketCloseData.getSettlementPrice();
    }


    @Override
    @Transactional
    public SettlementVO saveCloseMarketDate() {
        SettlementVO settlementVo = new SettlementVO();
        settlementVo.setIsSuccess(Boolean.FALSE);
        Set<String> keys = stringRedisTemplate.keys(RedisAdapter.REAL_TIME_MARKET + "*");
        if (keys != null && !keys.isEmpty()) {
            settlementVo.setIsSuccess(Boolean.TRUE);
            Map<String, String> stringBigDecimalMap = new HashMap<>();
            List<MarketCloseData> list = new ArrayList<>();
            for (String key : keys) {
                String data = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.isNotBlank(data)) {
                    data = data.replaceAll("(?i)" + Double.MAX_VALUE, "null");
                    MarketCloseData marketCloseData = JSONObject.parseObject(data, MarketCloseData.class);
                    marketCloseData.setClosePrice(marketCloseData.getClosePrice() == null
                            ? BigDecimal.ZERO : marketCloseData.getClosePrice().setScale(2, RoundingMode.HALF_UP));
                    //将合约代码的key转为大写
                    marketCloseData.setInstrumentID(marketCloseData.getInstrumentID().toUpperCase());
                    stringBigDecimalMap.put(marketCloseData.getInstrumentID()
                            , marketCloseData.getClosePrice().toString());
                    list.add(marketCloseData);
                }
            }
            //保存到redis
            stringRedisTemplate.opsForHash().putAll(RedisAdapter.TRADE_DAY_CLOSE_MARKET+systemClient.getTradeDay(), stringBigDecimalMap);
            stringRedisTemplate.expire(RedisAdapter.TRADE_DAY_CLOSE_MARKET+systemClient.getTradeDay(),10,TimeUnit.DAYS);
            //保存到数据库
            loadYlCloseMarketData(list);
            settlementVo.setMsg(JSONObject.toJSONString((stringBigDecimalMap)));
            return settlementVo;
        }
        settlementVo.setMsg("获取不到行情数据，未处理任何数据");
        return settlementVo;
    }

    @Override
    @Transactional
    @Async
    public void loadYlCloseMarketData(List<MarketCloseData> list) {
        for (MarketCloseData marketCloseData : list) {
            MarketCloseData dbInfo = marketCloseDataMapper.selectOne(new LambdaQueryWrapper<MarketCloseData>()
                    .eq(MarketCloseData::getInstrumentID, marketCloseData.getInstrumentID())
                    .eq(MarketCloseData::getTradingDay, marketCloseData.getTradingDay()));
            if (Objects.nonNull(dbInfo)) {
                marketCloseDataMapper.update(marketCloseData, new LambdaQueryWrapper<MarketCloseData>()
                        .eq(MarketCloseData::getInstrumentID, marketCloseData.getInstrumentID())
                        .eq(MarketCloseData::getTradingDay, marketCloseData.getTradingDay()));
            } else {
                marketCloseDataMapper.insert(marketCloseData);
            }
        }
    }

    @Override
    public Map<String, BigDecimal> getCloseDatePriceByCode(String underlyingCode) {
        LambdaQueryWrapper<MarketCloseData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketCloseData::getInstrumentID, underlyingCode);
        List<MarketCloseData> list = marketCloseDataMapper.selectList(queryWrapper);
        return list.stream().collect(Collectors.toMap(MarketCloseData::getTradingDay, MarketCloseData::getClosePrice));
    }

    @Override
    public Map<String, BigDecimal> getClosePrice(CloseDatePriceByDateDto dto) {
        LambdaQueryWrapper<MarketCloseData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MarketCloseData::getInstrumentID, dto.getUnderlyingCodes());
        queryWrapper.eq(MarketCloseData::getTradingDay, dto.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        List<MarketCloseData> list = marketCloseDataMapper.selectList(queryWrapper);
        if (log.isDebugEnabled()) {
            log.debug(dto.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "号的行情" + JSON.toJSONString(list));
        }
        return list.stream().collect(Collectors.toMap(item -> item.getInstrumentID().toUpperCase(), MarketCloseData::getClosePrice, (v1, v2) -> v2));
    }

    @Override
    public Map<String, BigDecimal> getCloseMarketDataByDate(LocalDate closeDate) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisAdapter.TRADE_DAY_CLOSE_MARKET + closeDate))){
            Map<Object, Object> marketEntries = stringRedisTemplate.opsForHash().entries(RedisAdapter.TRADE_DAY_CLOSE_MARKET + closeDate);
            return marketEntries.entrySet().stream().collect(
                    Collectors.toMap(e -> String.valueOf(e.getKey()), e -> new BigDecimal(e.getValue().toString())));
        }else {
            String closeDateStr = closeDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            LambdaQueryWrapper<MarketCloseData> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MarketCloseData::getTradingDay, closeDateStr);
            List<MarketCloseData> list = marketCloseDataMapper.selectList(queryWrapper);
            //保存到redis
            Map<String, BigDecimal> closeMap = list.stream().collect(Collectors.toMap(a -> a.getInstrumentID().toUpperCase(), MarketCloseData::getClosePrice));
            stringRedisTemplate.opsForHash().putAll(RedisAdapter.TRADE_DAY_CLOSE_MARKET + closeDate, closeMap.entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> e.getValue().toString())));
            stringRedisTemplate.expire(RedisAdapter.TRADE_DAY_CLOSE_MARKET + closeDate, 10, TimeUnit.DAYS);
            return closeMap;
        }
    }

    @Override
    public void getShareMarket() throws IOException, CsvValidationException {
        if (!LocalDate.now().isEqual(systemClient.getTradeDay())){
            return;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put(AuthAdapter.token, getAccessToken());
        RiceQuantApiDTO riceQuantApiDTO = new RiceQuantApiDTO();
        riceQuantApiDTO.setMethod("current_snapshot");
        //000300.SH','000905.SH','000016.SH','000852.SH'
        Map<String, String> codeMap = new HashMap<String, String>() {{
            put("000300.XSHG", "000300.SH");
            put("000905.XSHG", "000905.SH");
            put("000016.XSHG", "000016.SH");
            put("000852.XSHG", "000852.SH");
            put("AU9999.SGEX","AU9999");
        }};
        riceQuantApiDTO.setOrderBookIds(codeMap.keySet());
        String body = JSONObject.toJSONString(riceQuantApiDTO);
        String res = HttpUtil.createPost(riceQuantUrl + "/api").addHeaders(headers).body(body).execute().body();
        CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(res.getBytes())));
        reader.readNext();
        String[] line;
        while ((line = reader.readNext()) != null) {
            MarketInfoVO marketVO = new MarketInfoVO();
            marketVO.setInstrumentId(codeMap.get(line[0]));
            marketVO.setClosePrice(line[1].isEmpty() ? null : new BigDecimal(line[1]));
            marketVO.setSettlementPrice(line[1].isEmpty() ? null : new BigDecimal(line[1]));
            LocalDateTime localDateTime = DateUtil.parse(line[2], DatePattern.NORM_DATETIME_MS_FORMAT).toLocalDateTime();
            marketVO.setUpdateTime(String.valueOf(localDateTime.toLocalTime()));
            marketVO.setTradingDay(DateUtil.format(localDateTime, DatePattern.PURE_DATE_PATTERN));
            marketVO.setHighestPrice(new BigDecimal(line[3]));
            marketVO.setLastPrice(new BigDecimal(line[5]));
            marketVO.setLowestPrice(new BigDecimal(line[8]));
            MarketAdapter.marketData.put(marketVO.getInstrumentId(),marketVO);
            stringRedisTemplate.opsForValue().set(RedisAdapter.REAL_TIME_MARKET + marketVO.getInstrumentId(), JSONObject.toJSONString(marketVO), 30, TimeUnit.DAYS);
        }
    }

    private String getAccessToken() {
        String accessTokenString = stringRedisTemplate.opsForValue().get(RedisAdapter.RICE_QUANT_TOKEN);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(accessTokenString)) {
            return accessTokenString;
        }
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUserName(riceQuantAccount);
        authDTO.setPassword(riceQuantPassword);
        String body = JSONObject.toJSONString(authDTO);
        String res = HttpUtil.createPost(riceQuantUrl + "/auth").body(body).execute().body();
        if (res != null && res.length() > 200) {
            stringRedisTemplate.opsForValue().set(RedisAdapter.RICE_QUANT_TOKEN, res, 2, TimeUnit.HOURS);
            return res;
        }
        return res;
    }

    @Override
    public Map<String, BigDecimal> getSettlementMarketDataByDate(String closeDate) {
        LambdaQueryWrapper<MarketCloseData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MarketCloseData::getTradingDay, closeDate);
        List<MarketCloseData> list = marketCloseDataMapper.selectList(queryWrapper);
        return list.stream().collect(Collectors.toMap(a -> a.getInstrumentID().toUpperCase(), a-> Optional.ofNullable(a.getSettlementPrice()).orElse(BigDecimal.ZERO), (v1, v2) -> v2));
    }
}
