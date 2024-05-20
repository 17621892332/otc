package org.orient.otc.yl.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dzrh
 */
@Data
public class UnderlyingVolSurfaceDto {
    /**
     * 需要查看的标的代码列表
     */
    List<String> underlyingCodes;
    /**
     * 波动率类型
     */
    String volType;
    /**
     * 查看波动率曲面的日期
     */
    LocalDate systemDate;

}
