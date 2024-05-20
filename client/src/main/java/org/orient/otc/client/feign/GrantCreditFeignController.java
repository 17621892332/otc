package org.orient.otc.client.feign;

import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.client.dto.GrantCreditDTO;
import org.orient.otc.api.client.feign.GrantCreditClient;
import org.orient.otc.client.service.GrantCreditService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/grantCredit")
@Slf4j
public class GrantCreditFeignController implements GrantCreditClient {
    @Resource
    private GrantCreditService grantCreditService;

    @Override
    public Map<Integer, BigDecimal> getClientGrantCredit(GrantCreditDTO grantCreditDTO) {
        return grantCreditService.getClientGrantCredit(grantCreditDTO.getClientIdList(),grantCreditDTO.getEndDate());
    }
}
