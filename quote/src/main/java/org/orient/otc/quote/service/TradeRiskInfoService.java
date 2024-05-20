package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.quote.vo.TradeRiskPVInfoVO;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.TradeRiskInfoPvDTO;
import org.orient.otc.quote.dto.settlementReport.SettlementReportDTO;
import org.orient.otc.quote.entity.TradeRiskInfo;
import org.orient.otc.quote.vo.AccSummaryVO;
import org.orient.otc.quote.vo.TradeRiskInfoVo;
import org.orient.otc.quote.vo.daily.PositionDailyVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author dzrh
 */
public interface TradeRiskInfoService extends IServicePlus<TradeRiskInfo> {

    /**
     * 获取某一天的实时风险数据
     * @param clientIdList 客户ID列表
     * @param riskDate 风险日期
     * @return 风险数据
     */
    List<TradeRiskInfo> selectTradeRiskInfoListByRiskDate(Set<Integer> clientIdList,LocalDate riskDate);
    /**
     * 获取实时风险数据
     * @param clientIdList 客户ID列表
     * @param riskDate 风险日期
     * @return 风险数据
     */
    List<TradeRiskInfo> selectNewTradeRiskInfoListByRiskDate(Set<Integer> clientIdList, LocalDate riskDate);
    /**
     * 重算某一天的风险今日盈亏
     * @param riskDate 风险日期
     */
    void reSetTodayPnl(LocalDate riskDate);

    /**
     * 重算风险的今日盈亏
     * @param riskDate 风险日期
     * @param isHavingNext 是否包含后续交易日
     * @return 是否成功
     */
    Boolean reSetTodayPnl(LocalDate riskDate,Boolean isHavingNext);


    /**
     * 初始化累计盈亏
     * @param initDate 风险日期
     * @return 是否成功
     */
    Boolean initTotalPnl(LocalDate initDate);
    /**
     * 将风险计算结果落库
     * @param settlementDto 计算日期
     * @param dbList        计算数据
     */
    void saveTradeRiskInfoBatch(SettlementDTO settlementDto, List<TradeRiskInfo> dbList);

    /**
     * 结算报告->持仓明细
     * @param settlementReportDTO 客户信息
     * @return 查询结果
     */
    IPage<TradeRiskInfoVo> getRiskInfoListByPage(SettlementReportDTO settlementReportDTO);

    /**
     * 结算报告->累计汇总
     * 对应的方向为客户方向
     * @param settlementReportDTO 客户信息
     * @return 查询结果
     */
    List<AccSummaryVO> getAccSummaryList(SettlementReportDTO settlementReportDTO);

    /**
     * 获取累计汇总列表
     * @param accList 累计期权信息
     * @param forwardList 对应的远期信息
     * @return 累计汇总数据
     */
     List<AccSummaryVO> getAccSummaryList( List<TradeRiskInfo> accList,List<TradeRiskInfo> forwardList);
    /**
     * 保存已平仓的累计盈亏
     */
    void saveCloseTradeTotalPnl();

    /**
     * 根据日期获取风险快照信息
     * @param riskDate 风险日期
     * @param isLive  是否只获取存活的合约
     * @param isClose 是否只获取已平仓数据
     * @return 风险数据
     */

     List<TradeRiskInfo> getTradeTotalPnl(LocalDate riskDate,Boolean isLive, Boolean isClose);

    /**
     * 日终风险导入维护
     * @param file 导入文件
     * @return 导入结果
     * @throws IOException 文件解析异常
     */
     String importRiskInfo(MultipartFile file) throws IOException;


    /**
     * 获取指定日期的持仓数据
     * @param queryDate  查询日期
     * @param isNotInside  是否仅获取外部客户交易
     * @param assetType  资产类型
     * @return 交易数据
     */
    List<PositionDailyVO> getPositionDailyList(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType);

    /**
     * 获取指定日期的交持仓数据
     * @param queryDate  查询日期
     * @param isNotInside  是否仅获取外部客户交易
     * @param assetType  资产类型
     * @param pageNo  页码
     * @param pageSize 分页大小
     * @return 交易数据
     */
    Page<PositionDailyVO> getPositionDailyByPage(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType, Integer pageNo, Integer pageSize);

    /**
     * 获取指定日期的持仓保证金数据
     * @param clientId 客户ID
     * @param riskDate 风险日期
     * @return 保证金数据
     */
    List<TradeRiskPVInfoVO> getRiskInfoListByRiskDate(Integer clientId, LocalDate riskDate);

    /**
     * 分页查询风险维护信息
     * @param riskInfoPvDTO 查询条件
     * @return 分页数据
     */
    IPage<TradeRiskPVInfoVO> getTradeRiskPvByPage(TradeRiskInfoPvDTO riskInfoPvDTO);
}
