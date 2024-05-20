package org.orient.otc.yl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author dzrh
 */
@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenCloseInfoDto {
    /**
     * 交易编号
     */
    private  String tradeNumber;
}
