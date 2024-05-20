package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.orient.otc.api.dm.enums.AssetTypeEnum;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.orient.otc.api.quote.dto.CloseProfitLossDTO;
import org.orient.otc.api.quote.dto.ProfitLossAppraisementDto;
import org.orient.otc.api.quote.vo.TradeMngDetailVO;
import org.orient.otc.api.quote.vo.TradeMngVO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.confirmbook.DownloadTradeConfirmBookDTO;
import org.orient.otc.quote.dto.settlementbook.BuildSettlementConfirmBookDTO;
import org.orient.otc.quote.dto.settlementbook.DownloadSettlementConfirmBookDTO;
import org.orient.otc.quote.dto.trade.*;
import org.orient.otc.quote.entity.TradeMng;
import org.orient.otc.quote.vo.daily.TradeMngByDailyVO;
import org.orient.otc.quote.vo.trade.SettlementConfirmBookVO;
import org.orient.otc.quote.vo.trade.TradeConfirmBookVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dzrh
 */
public interface TradeMngService extends IServicePlus<TradeMng> {
    /**
     * 交易录入
     * @param tradeInsertDto 交易信息
     * @return 交易信息
     */
    List<TradeMngVO> insertTrade(TradeInsertDTO tradeInsertDto);

    /**
     * 更新交易记录
     * @param tradeUpdateDTO 交易信息
     * @return 是否更新成功
     */
    String  updateTrade(TradeUpdateDTO tradeUpdateDTO);

    /**
     * 获取交易信息
     * @param combCodeDTO 查询入参
     * @return 返回列表
     */
    List<TradeMngVO> getTradeInfo(CombCodeDTO combCodeDTO);

    /**
     * 通过交易编号列表获取交易列表
     * @param dto 交易编号
     * @return 交易列表
     */
    List<TradeMngVO> queryTradeListByTradeCodeList(TradeCodeQueryDTO dto);
    /**
     * 获取交易列表
     * @param tradeQueryDto 查询条件
     * @return 交易列表
     */
    IPage<TradeMngVO> queryTradeList(TradeQueryDTO tradeQueryDto);


    /**
     * 通过镒链交易记录入库
     * @param tradeMngVo 交易记录
     * @return 入库结果
     */
   Boolean saveTradeMngByYl(TradeMngVO tradeMngVo);
    /**
     * 获取未同步的交易记录
     * @return 交易记录
     */
    List<TradeMng> queryNotSyncTradeList();


    /**
     * 获取未平仓的交易记录
     * @param tradeDay 交易日期
     * @return 交易记录
     */
    List<TradeMngVO> getSurvivalTradeByTradeDay(LocalDate tradeDay);

    /**
     * 获取未平仓的交易记录
     * @param clientIdList 客户ID
     * @param tradeDay 交易日期
     * @return 交易记录
     */
    List<TradeMngVO> getSurvivalTradeByTradeDayAndClient(Set<Integer> clientIdList, LocalDate tradeDay);
    /**
     * 当天平仓的交易记录
     * @param tradeDay 交易日
     * @return 交易记录
     */
    List<TradeMngVO> getCloseTradeByTradeDay(LocalDate tradeDay);


    /**
     * 删除交易
     * @param combCodeDTO 交易ID
     * @return 删除信息
     */
    String delete(CombCodeDTO combCodeDTO);


    IPage<TradeMngVO> getListBypage(TradeQueryPageDto dto);

    void export(TradeQueryPageDto dto, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 交易确认书分页查询
     * @param queryDTO 查询条件
     * @return 交易确认书数据
     */
    IPage<TradeConfirmBookVO> tradeConfirmBookSelectByPage(TradeConfirmBookQueryDTO queryDTO);
    /**
     * 生成交易确认书
     * 根据交易编号 ,获取交易信息并组装客户,观察日等相关信息
     * @param dto 入参
     * @return 返回 生成的确认书路径信息
     * @throws Exception 异常
     */
    List<MinioUploadVO>  buildTradeConfirmBook(BuildTradeConfirmBookDto dto) throws Exception;

    void batchDownloadTradeConfirmBook(DownloadTradeConfirmBookDTO downloadTradeConfirmBookDTO, HttpServletResponse response);

    /**
     * 结算确认书分页查询
     * @param dto 请求参数
     * @return 结算确认书数据
     */
    IPage<SettlementConfirmBookVO> settlementConfirmBookSelectByPage(TradeSettlementConfirmBookQueryDTO dto);

    List<MinioUploadVO> buildSettlementConfirmBook(BuildSettlementConfirmBookDTO dto) throws Exception;

    void batchDownloadSettlementConfirmBook(DownloadSettlementConfirmBookDTO downloadSettlementConfirmBookDTO, HttpServletResponse response);

    BigDecimal getRealizeProfitLoss(ProfitLossAppraisementDto dto);

    /**
     * 获取客户的平仓盈亏
     * @param closeProfitLossDTO 客户信息
     * @return key 客户ID value 平仓盈亏
     */
    Map<Integer,BigDecimal> getProfitLossByClient(CloseProfitLossDTO closeProfitLossDTO);

    /**
     * 查看交易明细-东证方向
     * @param dto 入参
     * @return 返回交易详情
     */
    TradeMngDetailVO getByTradeCode(TradeDetailDto dto);

    /**
     * 通过交易代码获取交易信息
     * @param tradeCodeSet 交易代码
     * @return 交易记录
     */
    List<TradeMng> getTradeMngListByTradeCodeSet(@NotNull Set<String> tradeCodeSet);

    /**
     * 获取指定日期的交易数据
     * @param queryDate  查询日期
     * @param isNotInside  是否仅获取外部客户交易
     * @param assetType  资产类型
     * @return 交易数据
     */
    List<TradeMngByDailyVO> getTradeMngByDaily(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType);

    /**
     * 获取指定日期的交易数据
     * @param queryDate  查询日期
     * @param isNotInside  是否仅获取外部客户交易
     * @param assetType  资产类型
     * @param pageNo  页码
     * @param pageSize 分页大小
     * @return 交易数据
     */
    Page<TradeMngByDailyVO> getTradeMngByDaily(LocalDate queryDate, Boolean isNotInside, AssetTypeEnum assetType, Integer pageNo, Integer pageSize);


    /**
     * 更新交易的结算方式
     * @param updateTradeSettleTypeDTO 更新参数
     * @return 更新结果
     */
    String updateTradeSettleType(UpdateTradeSettleTypeDTO updateTradeSettleTypeDTO);


}
