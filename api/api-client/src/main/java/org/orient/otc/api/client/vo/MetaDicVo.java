package org.orient.otc.api.client.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetaDicVo {

    /**
     * 实际受益人证件有效期
     */
    LocalDateTime actualBeneficiaryLicenseCodeDate;
    /**
     * 报送主体
     */
    String reportName;
}
