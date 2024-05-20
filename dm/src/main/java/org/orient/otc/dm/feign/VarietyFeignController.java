package org.orient.otc.dm.feign;

import org.orient.otc.api.dm.feign.VarietyClient;
import org.orient.otc.api.dm.vo.VarietyVo;
import org.orient.otc.dm.entity.Variety;
import org.orient.otc.dm.service.VarietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dzrh
 */
@RestController
@RequestMapping("/variety")
public class VarietyFeignController implements VarietyClient {
    @Autowired
    private VarietyService varietyService;




    /**
     * 获取品种代码Map
     * @return key 品种ID  value 品种code
     */
    @Override
    public Map<String,Integer> getVarietyMap() {
        List<Variety> list = varietyService.list();
        return list.stream().collect(Collectors.toMap(Variety::getVarietyCode,Variety::getId));
    }

    @Override
    public VarietyVo getVarietyById(Integer varietyId) {
        return varietyService.getVarietyById(varietyId);
    }

    @Override
    public Map<Integer, String> getVarietyNameMap() {
        List<Variety> list = varietyService.list();
        return list.stream().collect(Collectors.toMap(Variety::getId,Variety::getVarietyName));
    }

    @Override
    public Map<Integer, String> getVarietyTypeNameMap(Set<Integer> idSet) {
        List<VarietyVo> list = varietyService.queryVarietyListById(idSet);
        return list.stream().collect(Collectors.toMap(VarietyVo::getId,VarietyVo::getVarietyTypeName));
    }
}
