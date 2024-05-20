package org.orient.otc.api.client.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

/**
 * 授信管理请求参数
 * @author dzrh
 */
@Data
public class GrantCreditDTO {
    private Set<Integer> clientIdList;
    private LocalDate endDate;
}
