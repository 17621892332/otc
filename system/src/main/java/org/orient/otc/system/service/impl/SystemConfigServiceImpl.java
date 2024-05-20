package org.orient.otc.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.system.dto.SystemUpdateDTO;
import org.orient.otc.common.cache.enums.SystemConfigEnum;
import org.orient.otc.api.system.vo.SystemConfigVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.system.dto.IntegerIdDTO;
import org.orient.otc.system.entity.SystemConfig;
import org.orient.otc.system.mapper.SystemConfigMapper;
import org.orient.otc.system.service.SystemConfigService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author dzrh
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    CalendarClient calendarClient;

    @Override
    public List<SystemConfigVO> getSystemConfigList() {

        return this.listVo(new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getIsDeleted, IsDeletedEnum.NO), SystemConfigVO.class);
    }

    /**
     * 修改系统配置
     * 修改系统交易日时 , 上一个交易日同步更新
     * @param systemUpdateDTO
     * @return
     */
    @Override
    public Boolean updateSystemConfig(SystemUpdateDTO systemUpdateDTO) {
        for (SystemConfigVO vo:systemUpdateDTO.getConfigList()) {
            LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SystemConfig::getIsDeleted, IsDeletedEnum.NO);
            queryWrapper.eq(SystemConfig::getConfigKey, vo.getConfigKey());
            long count = this.count(queryWrapper);
            SystemConfig config= SystemConfig.builder().configKey(vo.getConfigKey()).configValue(vo.getConfigValue()).build();
            //更新Redis
            stringRedisTemplate.opsForHash().put(RedisAdapter.SYSTEM_CONFIG_INFO,vo.getConfigKey(),vo.getConfigValue());
            if (count>0){
                // 如果修改的是系统交易日
                if (SystemConfigEnum.tradeDay.name().equals(vo.getConfigKey())) {
                    LocalDate day = LocalDate.parse(vo.getConfigValue());
                    updateLastTradeDay(day);
                }
                this.update(config,queryWrapper);
            }else {
                this.save(config);
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 更新上一个交易日配置
     * @param sysTradeDay 系统交易日
     */
    public void updateLastTradeDay(LocalDate sysTradeDay) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.eq(SystemConfig::getConfigKey, SystemConfigEnum.lastTradeDay.name());
        long count = this.count(queryWrapper);
        // 获取上一个交易日
        LocalDate lastTradeDay = calendarClient.getLastTradeDay(sysTradeDay);
        String value = lastTradeDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SystemConfig config= SystemConfig.builder()
                .configKey(SystemConfigEnum.lastTradeDay.name())
                .configValue(value).build();
        //更新Redis
        stringRedisTemplate.opsForHash().put(RedisAdapter.SYSTEM_CONFIG_INFO,SystemConfigEnum.lastTradeDay.name(),value);
        if (count>0) {
            this.update(config,queryWrapper);
        } else {
            this.save(config);
        }
    }

    @Override
    public Boolean delSystemConfig(IntegerIdDTO integerIdDTO) {
        SystemConfig config= SystemConfig.builder().id(integerIdDTO.getId()).build();
        config.setIsDeleted(IsDeletedEnum.YES.getFlag());

        return this.updateById(config);
    }
}
