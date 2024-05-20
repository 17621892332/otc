package org.orient.otc.yl.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
public class EodPricesDto {
    /**
     * 收盘日期
     */
    LocalDate valueDate;
    /**
     * 标的代码列表，为空则返回所有
     */
    List<String> underlyingCodes;
}
