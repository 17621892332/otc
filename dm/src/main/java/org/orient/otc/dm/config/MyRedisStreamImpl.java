package org.orient.otc.dm.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.dm.enums.UnderlyingState;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.database.enums.EnabledEnum;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.redisstream.RedisStreamInterface;
import org.orient.otc.common.redisstream.RedisStreamTemplate;
import org.orient.otc.common.redisstream.enums.StreamTypeEnum;
import org.orient.otc.dm.entity.Instrument;
import org.orient.otc.dm.entity.UnderlyingManager;
import org.orient.otc.dm.entity.Variety;
import org.orient.otc.dm.mapper.InstrumentMapper;
import org.orient.otc.dm.mapper.UnderlyingManagerMapper;
import org.orient.otc.dm.mapper.VarietyMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 合约信息
 */
@Component
@Slf4j
public class MyRedisStreamImpl implements RedisStreamInterface {
    @Resource
    InstrumentMapper instrumentMapper;
    @Resource
    UnderlyingManagerMapper underlyingManagerMapper;
    @Resource
    VarietyMapper varietyMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void handData(Map<String, String> data) {
        if (data.containsKey("ctpmsg")) {
            String ctpmsg = data.get("ctpmsg").trim();
            JSONObject jsonObject = JSONObject.parseObject(ctpmsg);

            if (jsonObject.containsKey("type")) {
                Integer type = jsonObject.getInteger("type");
                Instrument instrument;
                if (type == 4) {
                    try {
                        instrument = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), Instrument.class);
                    } catch (Exception e) {
                        log.error("错误数据：{}", ctpmsg);
                        return;
                    }
                    if (instrument.getInstLifePhase()!=null &&instrument.getInstLifePhase() == 0) {
                        return;
                    }
                    //插入场内合约信息
                    Instrument instrumentDb = instrumentMapper.selectById(instrument.getInstrumentId());
                    if (Objects.isNull(instrumentDb)) {
                        instrumentMapper.insert(instrument);
                    }
                    stringRedisTemplate.opsForValue().set("instrument:" + instrument.getInstrumentId(), JSONObject.toJSONString(instrument));
                    stringRedisTemplate.expire("instrument:" + instrument.getInstrumentId(), 30, TimeUnit.DAYS);
                    if (instrument.getOptionsType() == null && instrument.getInstrumentName().contains("主力")) {
                        return;
                    }
                    if (instrument.getOptionsType() != null && instrument.getOptionsType() != 0) {
                        return;
                    }
                    //更新场外合约表
                    UnderlyingManager underlyingManager = new UnderlyingManager();
                    underlyingManager.setUnderlyingCode(instrument.getInstrumentId().toUpperCase());
                    underlyingManager.setExchangeUnderlyingCode(instrument.getInstrumentId());
                    underlyingManager.setUnderlyingName(instrument.getInstrumentName());
                    underlyingManager.setExchange(instrument.getExchangeId());
                    underlyingManager.setExpireDate(instrument.getExpireDate() != null && instrument.getExpireDate().length() == 8
                            ? LocalDate.parse(instrument.getExpireDate(), DateTimeFormatter.ofPattern("yyyyMMdd")) : null);
                    underlyingManager.setStrike(instrument.getStrikePrice());
                    underlyingManager.setOptionType(instrument.getOptionsType()!=null ?instrument.getOptionsType():0);
                    underlyingManager.setPriceTick(instrument.getPriceTick());
                    underlyingManager.setContractSize(instrument.getVolumeMultiple());
                    underlyingManager.setCreateDate(instrument.getCreateTime() != null && instrument.getExpireDate().length() == 8
                            ? LocalDate.parse(instrument.getExpireDate(), DateTimeFormatter.ofPattern("yyyyMMdd")) : null);
                    UnderlyingManager dbInfo = underlyingManagerMapper.selectOne(new LambdaQueryWrapper<UnderlyingManager>()
                            .eq(UnderlyingManager::getUnderlyingCode, instrument.getInstrumentId()));
                    if (Objects.nonNull(dbInfo)) {
                        underlyingManagerMapper.update(underlyingManager, new LambdaQueryWrapper<UnderlyingManager>()
                                .eq(UnderlyingManager::getUnderlyingCode, instrument.getInstrumentId()));
                        stringRedisTemplate.delete(RedisAdapter.UNDERLYING_BY_CODE + underlyingManager.getUnderlyingCode());
                    } else {

                        Variety variety = varietyMapper.selectOne(new LambdaQueryWrapper<Variety>().eq(Variety::getIsDeleted, IsDeletedEnum.NO)
                                .eq(Variety::getVarietyCode, instrument.getInstrumentId().replaceAll("[^a-zA-Z]", "").toUpperCase()));
                        if (variety != null) {
                            underlyingManager.setVarietyId(variety.getId());
                            underlyingManager.setQuoteUnit(variety.getQuoteUnit());
                            underlyingManager.setEnabled(EnabledEnum.TRUE.getFlag());
                            underlyingManager.setUnderlyingState(UnderlyingState.Live);
                            underlyingManagerMapper.insert(underlyingManager);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void setTemplate(RedisStreamTemplate redisStreamTemplate) {
        redisStreamTemplate.setTopicName("mq_instrument_info");
        redisStreamTemplate.setGroup("groups101");
        redisStreamTemplate.setConsumer("dm");
        redisStreamTemplate.setStreamType(StreamTypeEnum.simple);
        redisStreamTemplate.setBatchSize(10);
    }

}
