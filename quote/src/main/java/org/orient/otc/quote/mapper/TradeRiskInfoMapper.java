package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.orient.otc.quote.entity.TradeRiskInfo;
import org.orient.otc.quote.vo.daily.PositionDailyVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 日终持仓风险DTO
 */
public interface TradeRiskInfoMapper extends BaseMapper<TradeRiskInfo> {
    /**
     * 获取最新风险数据
     * @param riskDate 风险日期
     * @param underlyingSet 合约代码列表
     * @param isClose 是否平仓交易
     * @return 风险列表
     */
    List<TradeRiskInfo> selectLiveRiskInfo(@Param("riskDate")LocalDate riskDate, @Param("underlyingSet")Set<String> underlyingSet, @Param("isClose") Boolean isClose);

    /**
     * 获取某天持仓的风险信息
     * @param page 分页信息
     * @param queryDate 查询日期
     * @param underlyingCodeList 合约列表
     * @param clientIdList 客户列表
     * @return 分页信息
     */
    Page<PositionDailyVO> selectPositionByDaily(Page<PositionDailyVO> page, @Param("queryDate") LocalDate queryDate, @Param("underlyingCodeList") List<String> underlyingCodeList, @Param("clientIdList") List<Integer> clientIdList);

    /**
     * 获取某天持仓的风险信息
     * @param queryDate 查询日期
     * @param underlyingCodeList 合约列表
     * @param clientIdList 客户列表
     * @return 交易记录
     */
    List<PositionDailyVO> selectPositionByDaily( @Param("queryDate") LocalDate queryDate,@Param("underlyingCodeList") List<String> underlyingCodeList,@Param("clientIdList")  List<Integer> clientIdList);

}
