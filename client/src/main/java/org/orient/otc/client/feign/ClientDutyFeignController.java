package org.orient.otc.client.feign;

import cn.hutool.extra.cglib.CglibUtil;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.client.feign.ClientDutyClient;
import org.orient.otc.api.client.vo.ClientDutyVO;
import org.orient.otc.client.service.ClientDutyService;
import org.orient.otc.client.vo.ClientDutyVo;
import org.orient.otc.client.vo.ClientMailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/clientDuty")
@Slf4j
public class ClientDutyFeignController implements ClientDutyClient {
    @Autowired
    ClientDutyService clientDutyService;
    @Override
    public List<ClientDutyVO> getClientDutyByClientId(Integer clientId) {
        List<ClientDutyVo> list = clientDutyService.list(String.valueOf(clientId));
        return CglibUtil.copyList(list,ClientDutyVO::new);
    }

    @Override
    public Map<String, Set<String>> getMapByClientId(String id) {
        Map<String, List<ClientMailVO>> map = clientDutyService.getMapByClientId(id);
        Map<String, Set<String>> returMap= new HashMap<>();
        for (Map.Entry<String, List<ClientMailVO> >  entry : map.entrySet()){
            String key = entry.getKey();
            List<ClientMailVO> valueList = entry.getValue();
            Set<String> set = new HashSet<>();
            for (ClientMailVO item : valueList) {
                set.addAll(item.getEmailSet());
            }
            returMap.put(key,set);
        }
        return returMap;
    }
}
