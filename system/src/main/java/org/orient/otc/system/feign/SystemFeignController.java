package org.orient.otc.system.feign;

import org.orient.otc.api.system.dto.SystemUpdateDTO;
import org.orient.otc.api.system.feign.SystemClient;
import org.orient.otc.api.system.vo.SystemConfigVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.cache.util.SystemConfigUtil;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.system.service.SystemCloseDayLogService;
import org.orient.otc.system.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system")
public class SystemFeignController implements SystemClient {
    @Autowired
    private SystemCloseDayLogService systemCloseDayLogService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SystemConfigUtil systemConfigUtil;
    @Override
    public List<SettlementVO> closeDate() {
        return systemCloseDayLogService.closeDate();
    }

    @Override
    public List<SettlementVO> settlement(@RequestBody SettlementDTO settlementDto) {
        return systemCloseDayLogService.settlement(settlementDto);
    }

    @Override
    public Boolean initLog() {
        return systemCloseDayLogService.initLog();
    }

    @Override
    public Map<String,String> getSystemInfo() {
        Map<Object,Object> entries =  stringRedisTemplate.opsForHash().entries(RedisAdapter.SYSTEM_CONFIG_INFO);
        if (!entries.isEmpty()){
            return entries.entrySet().stream().collect(
                    Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
        }else {
            Map<String,String> configMap =  systemConfigService.getSystemConfigList().stream().collect(Collectors.toMap(SystemConfigVO::getConfigKey,SystemConfigVO::getConfigValue));
            stringRedisTemplate.opsForHash().putAll(RedisAdapter.SYSTEM_CONFIG_INFO,configMap);
            return configMap;
        }
    }

    @Override
    public LocalDate getTradeDay() {
        return systemConfigUtil.getTradeDay();
    }

    @Override
    public Boolean updateSystemInfo(@RequestBody SystemUpdateDTO systemUpdateDTO) {
        return systemConfigService.updateSystemConfig(systemUpdateDTO);
    }
}
