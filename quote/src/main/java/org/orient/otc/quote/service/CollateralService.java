package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.collateral.*;
import org.orient.otc.quote.entity.Collateral;
import org.orient.otc.quote.vo.collateral.CollateralVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CollateralService {
    IPage<CollateralVO> selectListByPage(CollateralPageListDto dto);

    String add(CollateralAddDto dto);

    /**
     * 审核 对于当前抵押的记录进行确认或者拒绝(执行状态)
     * 批量或者单个 , 不管是确认还是拒绝 , 本次操作的所有数据状态
     * 确认操作 : 本次操作的所有数据状态必须都是未确认
     * 拒绝操作 : 本次操作的所有数据状态必须都是未确认
     * @param dto
     * @return
     */
    HttpResourceResponse check(CollateralCheckDto dto);

    HttpResourceResponse redemption(CollateralRedemptionDto dto);

    /**
     * 修改盯市价格
     * @param dto
     * @return
     */
    String updateMarketPrice(CollateralUpdateMarketPriceDto dto);

    /**
     * 获取抵押品盯市价格
     * 获取标的品种对应的合约最新的收盘价
     * @param dto
     * @return
     */
    BigDecimal getMarketPrice(CollateralGetMarketPriceDto dto);

    String update(CollateralUpdateDto dto);

    /**
     * 获取指定客户在某抵押区间内的所有抵押品价值
     * @param clientIdList 客户ID列表
     * @param endDate 结束日期
     * @return key 客户ID value 抵押品市值
     */
    Map<Integer,BigDecimal> getCollateralPrice(Set<Integer> clientIdList, LocalDate endDate);

    /**
     * 获取客户在指定日期时质押列表
     * @param clientId 客户ID
     * @param endDate 查询日期
     * @return 抵押品列表
     */
    List<Collateral> getCollateral(Integer clientId,  LocalDate endDate);

}
