package org.orient.otc.quote.dto.volatility;

import lombok.Data;
import org.orient.otc.quote.entity.Volatility;

import java.util.List;

/**
 * 波动率保存对象
 */
@Data
public class VolatilityListDto {
    private List<Volatility> volatilityList;
}
