package org.orient.otc.api.dm.feign;

import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@FeignClient(value = "dmserver",path = "/underlying", contextId ="dm")
public interface UnderlyingManagerClient {


    /**
     * 通过合约代码获取合约信息
     * @param code 合约代码
     * @return 合约信息
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingByCode")
    UnderlyingManagerVO getUnderlyingByCode(@RequestParam String code);

    /**
     * 批量获取合约信息
     * @param codes 合约代码
     * @return 合约列表
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingByCodes")
    List<UnderlyingManagerVO> getUnderlyingByCodes(@RequestParam Set<String> codes);

    /**
     * 获取所有存活的合约列表
     * @return 合约列表
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingList")
    List<UnderlyingManagerVO> getUnderlyingList();

    /**
     * 获取所有存活的合约列表
     * @param varietyId 品种ID
     * @return 合约列表
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingListByVarietyId")
    List<UnderlyingManagerVO> getUnderlyingListByVarietyId(@RequestParam Integer varietyId);
    /**
     * 批量保存或更新合约信息
     * @param dtoList 合约列表
     * @return true 更新成功  false 更新失败
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/saveBatch")
    Boolean saveBatch(@RequestBody List<UnderlyingManagerVO> dtoList);

    /**
     * 设置合约状态
     * @apiNote 将已过期的合约进行设置为已过期
     * @return  设置结果
     */
    @PostMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/updateUnderlyingState")
    SettlementVO updateUnderlyingState();

    /**
     * 获取多个品种的合约列表
     * @param varietyIds 品种ID
     * @return 合约列表
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingListByVarietyIds")
    List<UnderlyingManagerVO> getUnderlyingListByVarietyIds(@RequestParam List<Integer> varietyIds);



    /**
     * 获取多个品种的合约列表
     * @return 合约列表
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getMainUnderlyingList")
    List<UnderlyingManagerVO> getMainUnderlyingList();

    /**
     * 根据资产类型获取合约列表
     * @param assetType 资产类型
     * @param queryDate  查询日期
     * @return 合约列表
     */
    @GetMapping(FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/getUnderlyingListByAssetType")
    List<UnderlyingManagerVO> getUnderlyingListByAssetType(@RequestParam AssetTypeEnum assetType, @RequestParam LocalDate queryDate);
}
