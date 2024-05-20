package org.orient.otc.api.user.feign;

import org.orient.otc.api.user.vo.AssetunitGroupVo;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dzrh
 */
@FeignClient(value = "userserver",path = "/assetUnit", contextId ="assetUnit")
public interface AssetUnitClient {
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getAssetUnitList")
    List<AssetunitVo> getAssetUnitList(@RequestBody Set<Integer> idSet);
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getAssetUnitMapByIds")
    Map<Integer,String> getAssetUnitMapByIds(@RequestBody Set<Integer> idSet);
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"getAssetunitById")
    AssetunitVo getAssetunitById(@RequestParam Integer id);

    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getAssetunitByGroupIds")
    List<AssetunitVo> getAssetunitByGroupIds(@RequestParam Set<Integer> ids);

    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getAssetUnitGroupById")
    AssetunitGroupVo getAssetUnitGroupById(@RequestBody Integer id);

    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getAssetUnitGroupByIds")
    List<AssetunitGroupVo> getAssetUnitGroupByIds(@RequestBody Set<Integer> ids);

    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getVoByAccounts")
    List<AssetunitVo> getVoByAccounts(@RequestBody Set<String> accounts);

    /** 通过对冲账户account字段,查询簿记账户信息
     *  key = 对冲账户account , value=簿记账户
     * @param accounts
     * @return
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getMapByAccounts")
    Map<String,AssetunitVo> getMapByAccounts(@RequestBody Set<String> accounts);

}
