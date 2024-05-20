package org.orient.otc.yl.dto;

import lombok.Data;
import org.orient.otc.yl.entity.SingleVol;

import java.util.List;

@Data
public class SaveVolatilityDto {
    /**
     * 标的代码
     */
    String contractCode;
    /**
     * 波动率类型
     */

    String volType;
    /**
     * 报价日期，格式：yyyy-MM-dd
     */
    String quotationDate;
    /**
     * 插值方法，默认为“BiLinear”
     */
    String interpolationMethod;
    /**
     * 波动率曲面
     */
    List<SingleVol> volTable;
    /**
     * 如果传入的合约代码为连续合约则覆盖同合约其它标的 这个参数只适用于api/v2/saveVolatility
     */
    Boolean overridByMainCode;

}
