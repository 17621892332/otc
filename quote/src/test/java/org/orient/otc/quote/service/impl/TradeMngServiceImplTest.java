package org.orient.otc.quote.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.quote.service.TradeMngService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TradeMngServiceImplTest {

    @Resource
    private TradeMngService tradeMngService;
    @Test
    public void getSurvivalTradeByTradeDay() {
        tradeMngService.getSurvivalTradeByTradeDay(LocalDate.now());
    }

    @Test
    public void getCloseTradeByTradeDay() {
    }
}
