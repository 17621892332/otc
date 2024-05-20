package org.orient.otc.client.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.client.dto.client.*;
import org.orient.otc.client.entity.GrantCredit;
import org.orient.otc.client.vo.client.GrantCreditVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.database.config.IServicePlus;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public interface GrantCreditService extends IServicePlus<GrantCredit> {
    IPage<GrantCreditVO> getListByPage(GrantCreditPageDto dto);

    HttpResourceResponse add(GrantCreditAddDto dto);

    HttpResourceResponse updateGrantCredit(GrantCreditUpdateDto dto);

    HttpResourceResponse deleteGrantCredit(GrantCreditDeleteDto dto);

    HttpResourceResponse check(GrantCreditCheckDto dto);



    /**
     * 获取区间内客户的授信额度
     * @param clientIdList 客户ID列表

     * @param endDate 结束日期
     * @return key 客户ID value 授信额度
     */
    Map<Integer,BigDecimal> getClientGrantCredit(Set<Integer> clientIdList, LocalDate endDate);

    String importGrant(MultipartFile file) throws Exception;
}
