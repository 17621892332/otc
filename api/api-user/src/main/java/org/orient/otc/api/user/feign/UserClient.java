package org.orient.otc.api.user.feign;

import org.orient.otc.api.user.dto.UserDto;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "userserver",path = "/user", contextId ="user")
public interface UserClient {
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/list")
    public List<UserVo> getUserList();
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/insert")
    int insert(@RequestBody UserDto user);

    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUserById")
    UserVo getUserById(@RequestParam Integer id);

    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUserMapByIds")
    Map<Integer,String> getUserMapByIds(@RequestBody Set<Integer> idSet);

}
