package org.orient.otc.quote.service.impl;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.quote.QuoteApplication;
import org.orient.otc.quote.dto.TradeRiskInfoDto;
import org.orient.otc.quote.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QuoteApplication.class)
public class ServiceImplTest extends TestCase {
    @Autowired
    RiskService riskService;
    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;
    @Test
    public void getAllMailTemplateTest(){
        TradeRiskInfoDto dto = new TradeRiskInfoDto();
        LocalDate date = LocalDate.of(2023,1,9);
        dto.setSettlementDate(date);
        riskService.getExportDefinitionRisk(dto,request,response);
    }

}
