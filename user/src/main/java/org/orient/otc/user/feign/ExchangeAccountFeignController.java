package org.orient.otc.user.feign;

import com.alibaba.fastjson.JSONObject;
import org.orient.otc.api.user.dto.ExchangeAccountQueryDto;
import org.orient.otc.api.user.feign.ExchangeAccountClient;
import org.orient.otc.api.user.vo.ExchangeAccountFeignVO;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.security.exception.BussinessException;
import org.orient.otc.user.service.ExchangeAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/exchangeAccount")
public class ExchangeAccountFeignController implements ExchangeAccountClient {
    @Autowired
    ExchangeAccountService exchangeAccountService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public List<ExchangeAccountFeignVO> getList() {
        return exchangeAccountService.getList();
    }

    @Override
    public ExchangeAccountFeignVO getVoByname(ExchangeAccountQueryDto exchangeAccountQuery) {
        Object voStr =  stringRedisTemplate.opsForHash().get(RedisAdapter.EXCHANGE_ACCOUNT,exchangeAccountQuery.getAccount());
        if (Objects.nonNull(voStr)){
            return JSONObject.parseObject(voStr.toString(), ExchangeAccountFeignVO.class);
        }else {
            ExchangeAccountFeignVO exchangeAccountFeignVO = exchangeAccountService.getVoByname(exchangeAccountQuery);
            BussinessException.E_200002.assertTrue(Objects.nonNull(exchangeAccountFeignVO),exchangeAccountQuery.getAccount());
            stringRedisTemplate.opsForHash().put(RedisAdapter.EXCHANGE_ACCOUNT,exchangeAccountQuery.getAccount(),JSONObject.toJSONString(exchangeAccountFeignVO));
            return exchangeAccountFeignVO;
        }
    }

    @Override
    public List<ExchangeAccountFeignVO> getVoByAssetUnitIds(Set<Integer> ids) {
        return exchangeAccountService.getVoByAssetUnitIds(ids);
    }

    @Override
    public List<ExchangeAccountFeignVO>  getVoByAccounts(Set<String> accounts) {
        return exchangeAccountService.getVoByAccounts(accounts);
    }
}
