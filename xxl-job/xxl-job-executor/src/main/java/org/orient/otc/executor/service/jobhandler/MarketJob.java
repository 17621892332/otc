package org.orient.otc.executor.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.market.feign.MarketClient;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class MarketJob {
    @Resource
    MarketClient marketClient;

    @Resource
    CalendarClient calendarClient;

    /**
     * 交易所收盘后执行
     */
    @XxlJob("saveCloseMarketDate")
    public void saveCloseMarketDate() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            SettlementVO settlementVo = marketClient.saveCloseMarketDate();
            if (settlementVo.getIsSuccess()) {
                XxlJobHelper.handleSuccess(settlementVo.getMsg());
            } else {
                XxlJobHelper.handleFail(settlementVo.getMsg());
            }
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }


    /**
     * 获取指数行情
     */
    @XxlJob("updateShareMarket")
    public void updateShareMarket() {
        marketClient.updateShareMarket();
    }
}
