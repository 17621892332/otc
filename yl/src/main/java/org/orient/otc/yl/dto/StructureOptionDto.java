package org.orient.otc.yl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dzrh
 */
@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StructureOptionDto {
    /**
     * 结构化类型
     */
    private String structureType;

    /**
     * 期权录入对象数组长度必需大于等于2
     */
    private List<OrderOptionDto> trades;
}
