package org.orient.otc.yl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.yl.dto.*;
import org.orient.otc.yl.enums.YlApiUrl;
import org.orient.otc.yl.service.OrderService;
import org.orient.otc.yl.service.YlService;
import org.orient.otc.yl.vo.BaseResult;
import org.orient.otc.yl.vo.OpenCloseInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    YlService ylService;



    /**
     * @param dto
     * @return
     */
    @Override
    public BaseResult<String> option(OrderOptionDto dto) {
        String url = YlApiUrl.ORDER_OPTION_URL.getPath();
        String res = ylService.postNeedLog(url, dto);
        return  JSONObject.parseObject(res, new TypeReference<BaseResult<String>>() {
        });
    }
    @Override
    public BaseResult<String> forwardTradeSave(ForwardTradeSaveDto dto) {
        String url = YlApiUrl.FORWARD_TRADE_SAVE.getPath();
        String res = ylService.postNeedLog(url, dto);

        return JSONObject.parseObject(res, new TypeReference<BaseResult<String>>() {
        });
    }

    @Override
    public BaseResult<String> tradeInvalid(TradeInvalidDto dto) {
        String url = YlApiUrl.TRADE_INVALID.getPath();
        String res = ylService.postNeedLog(url, dto);
        return JSONObject.parseObject(res, new TypeReference<BaseResult<String>>() {
        });
    }

    @Override
    public BaseResult<String> tradeClose(TradeCloseDto dto) {
        String res = ylService.postNeedLog(YlApiUrl.TRADE_CLOSE.getPath(), dto);
        return JSONObject.parseObject(res, new TypeReference<BaseResult<String>>() {
        });
    }

    @Override
    public OpenCloseInfoVo tradeOpenCloseInfos(OpenCloseInfoDto dto) {
        String res = ylService.post(YlApiUrl.OPEN_CLOSE_INFOS.getPath(), dto);
        BaseResult<OpenCloseInfoVo> accessTokenBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<OpenCloseInfoVo>>() {
        });
        return accessTokenBaseResult.getData();
    }

    @Override
    public BaseResult<String> structureOptions(StructureOptionDto dto) {
        String res = ylService.postNeedLog(YlApiUrl.STRUCTURE_OPTION.getPath(), dto);
        return JSONObject.parseObject(res, new TypeReference<BaseResult<String>>() {
        });
    }
}
