package org.orient.otc.executor.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.orient.otc.api.dm.feign.CalendarClient;
import org.orient.otc.api.quote.feign.SettlementClient;
import org.orient.otc.api.system.feign.SystemClient;
import org.orient.otc.common.core.dto.SettlementDTO;
import org.orient.otc.common.core.vo.SettlementVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@Component
public class TradeDayJob {

    @Resource
    CalendarClient calendarClient;

    @Resource
    SettlementClient settlementClient;


    @Resource
    SystemClient systemClient;

    /**
     * 交易所收盘后执行
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @XxlJob("updateTradeObsDatePrice")
    public void updateTradeObsDatePrice()  {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            SettlementDTO settlementDto = new SettlementDTO();
            settlementDto.setSettlementDate(LocalDate.now());
            SettlementVO settlementVo = settlementClient.updateTradeObsDatePrice(settlementDto);
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
     * 每天晚上八点执行
     */
    @XxlJob("closeDate")
    public void closeDate() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
             systemClient.closeDate();
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }

    /**
     * 每天晚上四点执行
     */
    @XxlJob("settlement")
    public void settlement() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            SettlementDTO settlementDto = new SettlementDTO();
            settlementDto.setSettlementDate(LocalDate.now());
            systemClient.settlement(settlementDto);
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }

    /**
     * 每天晚上三点执行
     */
    @XxlJob("initLog")
    public void initLog() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            systemClient.initLog();
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }

    /**
     * 15点10分获取场内持仓
     */
    @XxlJob("getExchangePosition")
    public void getExchangePosition() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            settlementClient.getExchangePosition();
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }

    /**
     * 15点10分获取场内交易记录
     */
    @XxlJob("getExchangeTrade")
    public void getExchangeTrade() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            settlementClient.getExchangeTrade();
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }

    /**
     * 15点30分校验今日计算持仓
     */
    @XxlJob("checkTodayCaclPos")
    public void checkTodayCaclPos() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            settlementClient.checkTodayCaclPos();
        } else {
            XxlJobHelper.handleSuccess("非交易日，处理跳过");
        }
    }

    /**
     * 更新雪球期权的敲入标识
     */
    @XxlJob("updateKnockedIn")
    public void updateKnockedIn() {
        if (calendarClient.isTradeDay(LocalDate.now())) {
            SettlementDTO settlementDto = new SettlementDTO();
            settlementDto.setSettlementDate(LocalDate.now());
            SettlementVO settlementVo = settlementClient.updateKnockedIn(settlementDto);
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
