package org.orient.otc.yl.service;

import org.orient.otc.api.client.vo.ClientInfoDetailAllVo;
import org.orient.otc.api.client.vo.ClientInfoDetailVo;
import org.orient.otc.yl.dto.*;
import org.orient.otc.yl.vo.*;

import java.util.List;

/**
 * @author dzrh
 */
public interface YlService  {


    /**
     * 当本Service没有实现某个API的时候，可以用这个，针对所有API中的POST请求.
     *
     * @param postData 请求参数json值
     * @param url      请求接口地址
     * @return 接口响应字符串
     */
    String post(String url, Object postData);

    String postNeedLog(String url, Object postData);

    YlAccessToken getAccessToken(String userInfo);

    /**
     * 获取镒链token
     * @return token字符串
     */
    String getAccessToken();

    /**
     * 获取客户列表
     * @return
     */
    List<ClientInfoVo> getClientInfoList();

    /**
     * 获取客户列表
     * @return
     */
    ClientInfoDetailVo getClientInfo(ClientInfoDetailDto dto);
    /**
     * 获取客户详细信息
     *
     * @return
     */
    ClientInfoDetailAllVo getClientInfo2(ClientInfoDetailDto dto);
    /**
     * 获取合约列表
     * @return 合约列表
     */
    List<UnderlyingVo> getUnderlyingList();
    /**
     * 获取客户持仓信息
     * @param dto
     * @return
     */
    PageInfo<ClientPositionVo> getClientPositionListV1(ClientPositionDto dto);
    /**
     * 获取客户持仓信息
     * @param dto
     * @return
     */
    PageInfo<ClientPositionVo> getClientPositionListV3(ClientPositionDto dto);
    /**
     * 获取客户持仓信息
     * @param dto
     * @return
     */
    PageInfo<ClientPositionVo> getClientPositionListV2(ClientPositionDto dto);


    /**
     * 获取镒链波动率数据
     * @param dto
     * @return
     */
    List<UnderlyingVolVo> getUnderlyingVolLis(UnderlyingVolSurfaceDto dto);

    void saveVolatility(SaveVolatilityDto saveVolatilityDto);

    List<EodPricesVo> getEodPricesList(EodPricesDto eodPricesDto);

    /**
     * 添加客户出入金
     * @param clientCashInCashOutDTO 出入金参数
     * @return ID 资金编号
     */
    ClientCashInoCashOutVO addClientCashInCashOut(ClientCashInCashOutDTO clientCashInCashOutDTO);

    /**
     * 推送资金记录确认
     * @param ylId 镒链ID
     */
    void clientCashInCashOutConfirmed(Integer ylId);
    /**
     * 推送资金记录拒绝
     * @param ylId 镒链ID
     */
    void clientCashInCashOutRefuse(Integer ylId);

    /**
     * 更新风险
     * @param customTradeRiskDTO 风险信息
     */
    BaseResult<String>  updateCustomTradeRisk(UpdateCustomTradeRiskDTO customTradeRiskDTO);

    /**
     * 更新持仓保证金
     * @param marginDTO 保证金信息
     */
    BaseResult<String>  updateTradePositionMargin(UpdateTradePositionMarginDTO marginDTO);
}
