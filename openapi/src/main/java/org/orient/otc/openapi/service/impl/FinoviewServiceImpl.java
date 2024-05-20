package org.orient.otc.openapi.service.impl;

import cn.hutool.extra.cglib.CglibUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.finoview.dto.FinoviewVolDTO;
import org.orient.otc.api.finoview.dto.VolatilityDTO;
import org.orient.otc.api.finoview.dto.VolatilityDataDTO;
import org.orient.otc.api.finoview.dto.VolatityDeltaDataDTO;
import org.orient.otc.api.finoview.feign.FinoviewClient;
import org.orient.otc.api.quote.feign.VolatilityClient;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.openapi.service.FinoviewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 繁微服务实现
 */
@Service
@Slf4j
public class FinoviewServiceImpl implements FinoviewService {

    @Resource
    private VolatilityClient volatilityClient;

    @Resource
    private FinoviewClient finoviewClient;

    @Override
    public Integer sendAllVolToFinoview() {
        List<VolatilityVO> volatilityVOList = volatilityClient.getVolListByCodeSet(new HashSet<>());
        List<VolatilityDTO> volatilityDTOList = CglibUtil.copyList(volatilityVOList, VolatilityDTO::new, (vo, dto) -> {
            if (vo.getData() != null) {
                dto.setData(CglibUtil.copyList(vo.getData(), VolatilityDataDTO::new));
            }
            if (vo.getDeltaData() != null){
                dto.setDeltaData(CglibUtil.copyList(vo.getDeltaData(), VolatityDeltaDataDTO::new));
            }
            dto.setVolType(vo.getVolType().name());
            dto.setInterpolationMethod(vo.getInterpolationMethod().name());
        });
        Map<String, List<VolatilityDTO>> volMap = volatilityDTOList.stream().collect(Collectors.groupingBy(VolatilityDTO::getUnderlyingCode));
        for (List<VolatilityDTO> list : volMap.values()) {
            this.sendVolToFinoview(list);
        }

        return volatilityVOList.size();
    }

    @Override
    public void sendVolToFinoview(List<VolatilityDTO> volatilityDTOList) {
        log.info("发送给繁微波动率数据内容:{}", JSONObject.toJSONString(volatilityDTOList));
        FinoviewVolDTO finoviewVolDTO = new FinoviewVolDTO();
        finoviewVolDTO.setData(volatilityDTOList);
       String res= finoviewClient.sendVolToFinoview(finoviewVolDTO);
       log.info("推送结果:"+res);
    }
}
