package org.orient.otc.dm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.dm.dto.UnderlyingVolatilityDTO;
import org.orient.otc.dm.dto.UnderlyingVolatilityDelDto;
import org.orient.otc.dm.dto.underlying.*;
import org.orient.otc.dm.entity.UnderlyingManager;
import org.orient.otc.dm.vo.UnderlyingVolatilityVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 合约服务
 */
public interface UnderlyingManagerService extends IServicePlus<UnderlyingManager> {

    /**
     * 获取合约列表
     * @param underlyingManagerQueryDto 查询条件
     * @return 合约列表
     */
    List<UnderlyingManager> getList(UnderlyingManagerQueryDto underlyingManagerQueryDto);

    /**
     * 通过合约代码获取合约信息
     * @param underlyingCode 合约代码
     * @return 合约信息
     */
    UnderlyingManagerVO getUnderlyingVoByCode(String underlyingCode);

    /**
     * 批量保存或更新合约信息
     * @param list 合约列表
     * @return true 更新成功  false 更新失败
     */
    Boolean saveOrUpdateBatchByCode(List<UnderlyingManager> list );

    /**
     * 设置合约状态
     * @apiNote 将已过期的合约进行设置为已过期
     * @return 设置结果
     */
    SettlementVO updateUnderlyingState();

    /**
     * 设置主力合约附合约分红率
     * @param underlyingVolatilityDTOList 合约信息
     * @return  true 更新成功  false 更新失败
     */
    Boolean setUnderlyingVolatility(List<UnderlyingVolatilityDTO> underlyingVolatilityDTOList);
    /**
     * 删除主力合约附合约分红率
     * @param delDto 合约信息
     * @return  true 更新成功  false 更新失败
     */
    Boolean delUnderlyingVolatility(UnderlyingVolatilityDelDto delDto);

    /**
     * 通过品种获取Benchmark列表
     * @param varietyId 品种ID
     * @return Benchmark列表
     */
    List<UnderlyingVolatilityVO> getUnderlyingVolatility(Integer varietyId);


    /**
     * 分页获取合约信息
     * @param pageQueryDto 查询条件
     * @return 合约分邺信息
     */
    Page<UnderlyingManagerVO> queryUnderlyingList(UnderlyingManagerPageQueryDto pageQueryDto);

    /**
     * 新增合约信息
     * @param addDto 新增参数
     * @return 新增结果
     */
    String addUnderlying(UnderlyingManagerAddDto addDto);
    /**
     * 修改合约信息
     * @param editDto 修改参数
     * @return 修改结果
     */
    String editUnderlying(UnderlyingManagerEditDto editDto);

    /**
     * 更新合约是否启用标识
     * @param enableDto 更新参数
     * @return 更新结果
     */
    String updateUnderlyingEnable(UnderlyingManagerEnableDto enableDto);

    /**
     * 通过品种ID更新合约涨跌停信息
     * @param varietyId   品种ID
     * @param upDownLimit 合约涨跌停信息
     */
    void updateUnderlyingUpDownLimit(Integer varietyId, BigDecimal upDownLimit);
    /**
     * 通过合约代码获取合约列表
     * @param redisNoCode 合约代码
     * @return 合约列表
     */
    List<UnderlyingManagerVO> getUnderlyingVoByCodes(Set<String> redisNoCode);

    /**
     * 获取处于禁止交易的合约列表
     * @return  禁用的合约
     */
    Set<String> disableList();

    /**
     * 获取所有主力合约
     * @return 合约列表
     */
    List<UnderlyingManagerVO> getMainUnderlyingList();

    /**
     * 通过品种列表获取合约列表
     * @param varietyIdList 品种ID
     * @param queryDate 查询日期
     * @return 合约列表
     */
    List<UnderlyingManagerVO> getUnderlyingByVarietyList(List<Integer> varietyIdList, LocalDate queryDate);
}
