package org.orient.otc.yl.service;

import org.orient.otc.yl.dto.*;
import org.orient.otc.yl.vo.BaseResult;
import org.orient.otc.yl.vo.OpenCloseInfoVo;

public interface OrderService{
    /**
     * 期权交易录入
     * @param dto 镒链期权交易入库
     * @return true 成功 false失败
     */
    BaseResult<String> option(OrderOptionDto dto);

    /**
     * 远期交易同步
     * @param dto 远期请求对象
     * @return true 成功 false失败
     */
    BaseResult<String> forwardTradeSave(ForwardTradeSaveDto dto);

    /**
     * 场外交易-删除交易
     * @param dto 交易编号
     */
    BaseResult<String> tradeInvalid(TradeInvalidDto dto);

    /**
     * 平仓同步
     * @param dto 平仓请求对象
     * @return true 成功 false失败
     */
    BaseResult<String> tradeClose(TradeCloseDto dto);

    /**
     * 获取平仓列表
     * @param dto 交易编码
     * @return 平仓列表
     */
    OpenCloseInfoVo tradeOpenCloseInfos(OpenCloseInfoDto dto);

    BaseResult<String> structureOptions(StructureOptionDto dto);
}
