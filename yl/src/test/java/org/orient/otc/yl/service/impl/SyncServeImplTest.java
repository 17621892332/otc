package org.orient.otc.yl.service.impl;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.yl.service.SyncServe;
import org.orient.otc.yl.vo.TradeMngByYlVo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(properties = "spring.cloud.nacos.discovery.group=pjc")
@RunWith(SpringRunner.class)
public class SyncServeImplTest {

    @Resource
    SyncServe syncServe;

    @Test
    public void syncTradeToYl() {
        List<TradeMngByYlVo> notSyncList = JSONArray.parseArray(
                "[{\"algorithmName\":\"PDE\",\"assetId\":1,\"bankHoliday\":0,\"barrier\":100,\"barrierRelative\":true,\"bonusRateAnnulized\":true,\"bonusRateStructValue\":1,\"buyOrSell\":\"buy\",\"clientId\":1,\"combCode\":\"20231023-JFSH-02\",\"day1PnL\":-0.0004,\"delta\":0.0000,\"entryPrice\":15765.00,\"gamma\":-0.0154,\"id\":65,\"maturityDate\":\"2023-11-23\",\"midVol\":16.5,\"obsNumber\":23,\"optionType\":\"AIBreakEvenSnowBallCallPricer\",\"pv\":-0.0004,\"rebateRate\":1,\"rebateRateAnnulized\":true,\"returnRateAnnulized\":true,\"returnRateStructValue\":1,\"rho\":0.0000,\"sort\":1,\"startObsDate\":\"2023-10-24\",\"theta\":0.0001,\"totalAmount\":0,\"tradeCode\":\"20231023-JFSH-02\",\"tradeDate\":\"2023-10-23\",\"tradeObsDateList\":[{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":1,\"obsDate\":\"2023-10-24\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-10-26\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":2,\"obsDate\":\"2023-10-25\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-10-27\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":3,\"obsDate\":\"2023-10-26\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-10-30\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":4,\"obsDate\":\"2023-10-27\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-10-31\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":7,\"obsDate\":\"2023-10-30\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-01\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":8,\"obsDate\":\"2023-10-31\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-02\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":9,\"obsDate\":\"2023-11-01\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-03\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":10,\"obsDate\":\"2023-11-02\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-06\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":11,\"obsDate\":\"2023-11-03\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-07\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":14,\"obsDate\":\"2023-11-06\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-08\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":15,\"obsDate\":\"2023-11-07\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-09\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":16,\"obsDate\":\"2023-11-08\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-10\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":17,\"obsDate\":\"2023-11-09\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-13\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":18,\"obsDate\":\"2023-11-10\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-14\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":21,\"obsDate\":\"2023-11-13\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-15\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":22,\"obsDate\":\"2023-11-14\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-16\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":23,\"obsDate\":\"2023-11-15\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-17\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":24,\"obsDate\":\"2023-11-16\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-20\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":25,\"obsDate\":\"2023-11-17\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-21\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":28,\"obsDate\":\"2023-11-20\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-22\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":29,\"obsDate\":\"2023-11-21\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-23\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":30,\"obsDate\":\"2023-11-22\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-24\"},{\"barrier\":100,\"barrierRelative\":true,\"barrierShift\":0,\"dayCount\":31,\"obsDate\":\"2023-11-23\",\"rebateRate\":1,\"rebateRateAnnulized\":true,\"settlementDate\":\"2023-11-27\"}],\"tradeVol\":16.5,\"tradeVolume\":0.00,\"traderId\":166,\"tradingDay\":24,\"ttm\":32,\"underlyingCode\":\"CF405\",\"vega\":0.0000,\"workday\":24}]"
                , TradeMngByYlVo.class);
        syncServe.syncTradeToYl(notSyncList, Boolean.FALSE);
    }


}
