package org.orient.otc.quote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.orient.otc.api.quote.dto.risk.RiskMarkDto;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.quote.config.multidb.DB;
import org.orient.otc.quote.entity.RiskMark;
import org.orient.otc.quote.mapper.RiskMarkMapper;
import org.orient.otc.quote.service.RiskMarkService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

@Service
public class RiskMarkServiceImpl implements RiskMarkService {
    @Resource
    RiskMarkMapper riskMarkMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    private SystemConfigUtil systemConfigUtil;
    @Override
    @Transactional
    public String insertRiskMark(RiskMark riskMark) {
        RiskMark r = riskMarkMapper.selectOne(new LambdaQueryWrapper<RiskMark>().eq(RiskMark::getUnderlyingCode, riskMark.getUnderlyingCode()).eq(RiskMark::getIsDeleted, 0));
        if(Objects.nonNull(r)){
            r.setMarkValue(riskMark.getMarkValue());
            riskMarkMapper.updateById(r);
        }else {
            riskMarkMapper.insert(riskMark);
        }
        stringRedisTemplate.opsForHash().put(RedisAdapter.RISK_MARK+systemConfigUtil.getTradeDay(),riskMark.getUnderlyingCode(),riskMark.getMarkValue().toString());
        return "riskMark插入成功";
    }

    @Override
    @Transactional
    public String deleteRiskMark(RiskMarkDto riskMarkDto) {
        RiskMark r = new RiskMark();
        r.setIsDeleted(1);
        riskMarkMapper.update(r,new LambdaQueryWrapper<RiskMark>().eq(RiskMark::getUnderlyingCode, riskMarkDto.getUnderlyingCode()));
        stringRedisTemplate.opsForHash().put(RedisAdapter.RISK_MARK+systemConfigUtil.getTradeDay(),riskMarkDto.getUnderlyingCode(),BigDecimal.ZERO.toString());
        return "riskMark删除成功";
    }

    @Override
    @DB
    public BigDecimal getRiskMark(RiskMarkDto riskMarkDto) {
      Object riskMarkStr=  stringRedisTemplate.opsForHash().get(RedisAdapter.RISK_MARK+systemConfigUtil.getTradeDay(), riskMarkDto.getUnderlyingCode());
      if (Objects.isNull(riskMarkStr)){
          RiskMark r = riskMarkMapper.selectOne(new LambdaQueryWrapper<RiskMark>().eq(RiskMark::getUnderlyingCode, riskMarkDto.getUnderlyingCode()).eq(RiskMark::getIsDeleted, 0));
          if(Objects.nonNull(r)) {
              stringRedisTemplate.opsForHash().put(RedisAdapter.RISK_MARK+systemConfigUtil.getTradeDay(), riskMarkDto.getUnderlyingCode(), r.getMarkValue().toString());
              return r.getMarkValue();
          }else {
              stringRedisTemplate.opsForHash().put(RedisAdapter.RISK_MARK+systemConfigUtil.getTradeDay(), riskMarkDto.getUnderlyingCode(), BigDecimal.ZERO.toString());
              return BigDecimal.ZERO;
          }
      }else {
          return  new BigDecimal(riskMarkStr.toString());
      }
    }
}
