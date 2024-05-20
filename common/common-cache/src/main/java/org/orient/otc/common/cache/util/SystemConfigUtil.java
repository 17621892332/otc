package org.orient.otc.common.cache.util;

import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 获取系统配置信息
 * @author dzrh
 */
@Component
public class SystemConfigUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取当前系统交易日
     * @return 系统交易日
     */
   public LocalDate getTradeDay() {
        return LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.tradeDay.name())).toString());
    }

    /**
     * 获取上一个交易日
     * @return 上一个交易日
     */
    public LocalDate getLastTradeDay() {
        return LocalDate.parse(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.lastTradeDay.name())).toString());
    }

    /**
     * 获取当前系统分红率
     * @return 系统分红率
     */
    public BigDecimal getDividendYield() {
        return new BigDecimal(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.dividendYield.name())).toString());
    }

    /**
     * 获取当前系统无风险利率
     * @return 无风险利率
     */
    public BigDecimal getRiskFreeInterestRate() {
        return new BigDecimal(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.riskFreeInterestRate.name())).toString());
    }

    /**
     * 获取报送主体名称
     * @return 报送主体名称
     */
    public String getMainName(){
        return  Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.mainName.name())).toString();
    }

    /**
     * 获取报告主体的统一社会信用代码
     * @return 报告主体的统一社会信用代码
     */
    public String getMainLicenseCode(){
        return  Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.mainLicenseCode.name())).toString();
    }
    /**
     * 获取报告主体的统一社会信用代码
     * @return 报告主体的统一社会信用代码
     */
    public Integer getPathNumber(){
        return Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.pathNumber.name())).toString());
    }
    /**
     * 获取报告主体的统一社会信用代码
     * @return 报告主体的统一社会信用代码
     */
    public Integer getThreadNumber(){
        return  Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForHash()
                .get(RedisAdapter.SYSTEM_CONFIG_INFO, SystemConfigEnum.threadNumber.name())).toString());
    }
}
