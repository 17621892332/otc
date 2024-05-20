package org.orient.otc.executor.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.dm.feign.InstrumentClient;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class DmJob {
    @Resource
    InstrumentClient instrumentClient;
    @Resource
    CalendarClient calendarClient;
    @Resource
    UnderlyingManagerClient underlyingManagerClient;

    /**
     * 每天开盘前更新场内合约
     * @return
     */
    @XxlJob("updateExchangeInstrument")
    public Boolean updateExchangeInstrument() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            if (instrumentClient.updateExchangeInstrument()) {
                XxlJobHelper.handleSuccess("获取成功");
            } else {
                XxlJobHelper.handleFail("获取失败");
            }
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
        return instrumentClient.updateExchangeInstrument();
    }


    /**
     * 更新合约状态
     */
    @XxlJob("updateUnderlyingState")
    public void updateUnderlyingState() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            SettlementVO settlementVo = underlyingManagerClient.updateUnderlyingState();
            if (settlementVo.getIsSuccess()) {
                XxlJobHelper.handleSuccess(settlementVo.getMsg());
            } else {
                XxlJobHelper.handleFail(settlementVo.getMsg());
            }
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }
}
