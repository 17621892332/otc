package org.orient.otc.api.client.feign;

import org.orient.otc.api.client.vo.ClientInfoListVo;
import org.orient.otc.api.client.vo.ClientLevelVo;
import org.orient.otc.api.client.vo.ClientVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pjc
 */
@FeignClient(value = "clientserver",path = "/client", contextId ="client")
public interface ClientClient {
    @GetMapping (FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientById")
    ClientVO getClientById(@RequestParam Integer id);
    @GetMapping (FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientLevel")
    ClientLevelVo getClientLevel(@RequestParam Integer clientId);

    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientMapByIds")
    Map<Integer,String> getClientMapByIds(@RequestBody Set<Integer> idSet);

    /**
     * 获取客户保证金系数
     * @param idSet 客户ID
     * @return key 客户ID value 保证金系数
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientMarginRate")
    Map<Integer, BigDecimal> getClientMarginRate(@RequestBody Set<Integer> idSet);
    /**
     * 通过客户ID获取客户列表
     * @param idSet  客户ID
     * @return 客户列表
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientListByIds")
    List<ClientVO> getClientListByIds(@RequestBody Set<Integer> idSet);
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveBatch")
    Boolean syncByYl(@RequestBody ClientInfoListVo clientInfoListVo);

    /**
     * 根据客户名称查询客户ID
     * key=客户名称 , value=客户ID
     * @param nameSet   客户名称集合
     * @return 返回map key = 客户名称 , value = 客户ID
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getClientMapByNameList")
    Map<String,Integer> getClientMapByNameList(@RequestBody Set<String> nameSet);

    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"getInsideClientIdList")
    List<Integer> getInsideClientIdList();
}
