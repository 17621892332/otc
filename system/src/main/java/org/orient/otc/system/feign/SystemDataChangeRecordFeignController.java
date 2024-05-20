package org.orient.otc.system.feign;

import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.system.dto.APICapitalDataChangeRecordAddDto;
import org.orient.otc.api.system.dto.APIGrantCreditDataChangeRecordAddDto;
import org.orient.otc.api.system.dto.clientdatachangerecord.APIClientDataChangeRecordAddDto;
import org.orient.otc.api.system.dto.tradedatachangerecord.TradeDataChangeRecordDTO;
import org.orient.otc.api.system.feign.SystemDataChangeRecordClient;
import org.orient.otc.common.core.util.ObjectEqualsUtil;
import org.orient.otc.system.dto.capitaldatachangerecord.CapitalDataChangeRecordAddDto;
import org.orient.otc.system.dto.clientdatachangerecord.ClientDataChangeRecordAddDto;
import org.orient.otc.system.dto.grantcreditdatachangerecord.GrantCreditDataChangeRecordAddDto;
import org.orient.otc.system.service.CapitalDataChangeRecordService;
import org.orient.otc.system.service.ClientDataChangeRecordService;
import org.orient.otc.system.service.GrantCreditDataChangeRecordService;
import org.orient.otc.system.service.TradeDataChangeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/systemDataChangeRecord")
@Slf4j
public class SystemDataChangeRecordFeignController implements SystemDataChangeRecordClient {
    @Autowired
    private TradeDataChangeRecordService tradeDataChangeRecordService;

    @Autowired
    private ClientDataChangeRecordService clientDataChangeRecordService;

    @Autowired
    private CapitalDataChangeRecordService capitalDataChangeRecordService;

    @Autowired
    GrantCreditDataChangeRecordService grantCreditDataChangeRecordService;

    @Autowired
    ObjectEqualsUtil objectEqualsUtil;

    /**
     * 交易变更记录新增
     * @param tradeDataChangeRecordDTO      变更对象
     */
    @Override
    public void addTradeDataChangeRecord(TradeDataChangeRecordDTO tradeDataChangeRecordDTO){
        tradeDataChangeRecordService.add(tradeDataChangeRecordDTO);
    }

    /**
     *客户变更记录新增
     */
    @Override
    public void addClientDataChangeRecord(APIClientDataChangeRecordAddDto apiDto) {
        ClientDataChangeRecordAddDto dto = new ClientDataChangeRecordAddDto();
        BeanUtils.copyProperties(apiDto,dto);
        clientDataChangeRecordService.add(dto);
    }

    @Override
    public void addCapitalDataChangeRecord(APICapitalDataChangeRecordAddDto apiDto) {
        CapitalDataChangeRecordAddDto dto = new CapitalDataChangeRecordAddDto();
        BeanUtils.copyProperties(apiDto,dto);
        capitalDataChangeRecordService.add(dto);
    }

    @Override
    public void addGrantCreditDataChangeRecord(APIGrantCreditDataChangeRecordAddDto apiDto) {
        GrantCreditDataChangeRecordAddDto dto = new GrantCreditDataChangeRecordAddDto();
        BeanUtils.copyProperties(apiDto,dto);
        grantCreditDataChangeRecordService.add(dto);
    }
}
