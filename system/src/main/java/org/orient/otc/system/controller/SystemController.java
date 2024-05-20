package org.orient.otc.system.controller;

import io.swagger.annotations.Api;
import org.orient.otc.api.system.dto.SystemUpdateDTO;
import org.orient.otc.api.system.vo.SystemConfigVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.system.dto.IntegerIdDTO;
import org.orient.otc.system.service.SystemConfigService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置
 * @author dzrh
 */
@RestController
@RequestMapping("/system")
@Api(tags = "系统配置")
public class SystemController {

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取系统配置列表
     */
    @GetMapping("/systemInfo")
    public HttpResourceResponse<List<SystemConfigVO>> getSystemInfo() {
        return HttpResourceResponse.success(systemConfigService.getSystemConfigList());
    }

    @GetMapping("/refreshSystemConfig")
    public HttpResourceResponse<List<SystemConfigVO>> refreshSystemConfig() {
        List<SystemConfigVO> configVOList = systemConfigService.getSystemConfigList();
        Map<String, String> configMap = configVOList.stream().collect(Collectors.toMap(SystemConfigVO::getConfigKey, SystemConfigVO::getConfigValue));
        stringRedisTemplate.opsForHash().putAll(RedisAdapter.SYSTEM_CONFIG_INFO, configMap);
        return HttpResourceResponse.success(configVOList);
    }

    @PostMapping("/updateSystemConfig")
    public HttpResourceResponse<Boolean> updateSystemConfig(@RequestBody SystemUpdateDTO updateDTO) {
        Boolean update = systemConfigService.updateSystemConfig(updateDTO);
        if (update) {
            return HttpResourceResponse.successWithMessage("保存成功");
        } else {
            return HttpResourceResponse.error(500, Boolean.FALSE, "保存失败");
        }
    }

    @PostMapping("/delSystemConfig")
    public HttpResourceResponse<Boolean> delSystemConfig(@RequestBody IntegerIdDTO delDTO) {
        Boolean update = systemConfigService.delSystemConfig(delDTO);
        if (update) {
            return HttpResourceResponse.successWithMessage("删除成功");
        } else {
            return HttpResourceResponse.error(500, Boolean.FALSE, "删除失败");
        }
    }
}
