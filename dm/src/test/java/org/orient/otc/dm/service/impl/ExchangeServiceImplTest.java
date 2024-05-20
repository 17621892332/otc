package org.orient.otc.dm.service.impl;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.dm.dto.ExchangeDeleteDto;
import org.orient.otc.dm.dto.ExchangePageDto;
import org.orient.otc.dm.dto.ExchangeSaveDto;
import org.orient.otc.dm.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ExchangeServiceImplTest extends TestCase {

    @Autowired
    ExchangeService exchangeService;

    @Test
    public void selectListByPageTest() {
        ExchangePageDto dto = new ExchangePageDto();
        dto.setPageNo(1);
        dto.setPageSize(10);
        System.out.println(JSON.toJSONString(exchangeService.selectListByPage(dto)));
    }

    @Test
    public void insertExchangeTest() {
        ExchangeSaveDto dto = new ExchangeSaveDto();
        dto.setName("测试交易所-3");
        dto.setCode("CS");
        System.out.println(JSON.toJSONString(exchangeService.saveExchange(dto)));
    }
    @Test
    public void updateExchangeTest() {
        ExchangeSaveDto dto = new ExchangeSaveDto();
        dto.setId(10);
        dto.setName("测试交易所-1");
        System.out.println(JSON.toJSONString(exchangeService.saveExchange(dto)));
    }
    @Test
    public void deleteExchangeTest() {
        ExchangeDeleteDto dto = new ExchangeDeleteDto();
        dto.setId(10);
        System.out.println(JSON.toJSONString(exchangeService.deleteExchange(dto)));
    }

}
