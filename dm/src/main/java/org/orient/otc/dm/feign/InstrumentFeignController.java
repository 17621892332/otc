package org.orient.otc.dm.feign;

import org.orient.otc.api.dm.feign.InstrumentClient;
import org.orient.otc.api.dm.vo.InstrumentInfoVo;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.dm.service.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/instrument")
public class InstrumentFeignController implements InstrumentClient {
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ExchangeAccountClient exchangeAccountClient;
    @Override
    public InstrumentInfoVo getInstrumentInfo(String instID) {
        return instrumentService.getInstrumentInfo(instID);
    }

    @Override
    public Boolean updateExchangeInstrument() {
        List<ExchangeAccountFeignVO> list = exchangeAccountClient.getList();
        HashMap map = new HashMap<>();
        map.put("ctpmsg", "{\"type\":101,\"data\":{\"UserID\":\"" + list.get(0).getAccount() + "\"}}");
        stringRedisTemplate.opsForStream().add("ctp_mq", map);
        return null;
    }

    @Override
    public List<InstrumentInfoVo> getInstrumentInfoByIds(Set<String> instIDs) {
        return instrumentService.getInstrumentInfoByIds(instIDs);
    }

    @Override
    public List<InstrumentInfoVo> getInstrumentInfoByUndeingCodes(Set<String> codes) {
        return instrumentService.getInstrumentInfoByUndeingCodes(codes);
    }
}
