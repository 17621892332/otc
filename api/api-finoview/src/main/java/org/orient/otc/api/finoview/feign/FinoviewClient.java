package org.orient.otc.api.finoview.feign;

import org.orient.otc.api.finoview.config.FinoviewFeignConfig;
import org.orient.otc.api.finoview.dto.FinoviewVolDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 繁微API
 */
@FeignClient(value = "finoviewServer",url = "${finoview.url}", contextId ="finoviewServer",configuration = FinoviewFeignConfig.class)
public interface FinoviewClient {



    /**
     * 推送波动率到泛微
     * @return 推送结果
     */
    @PostMapping("/receive_vol_data")
    String sendVolToFinoview(@RequestBody FinoviewVolDTO dto);

}
