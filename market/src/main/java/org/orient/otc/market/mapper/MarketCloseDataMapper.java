package org.orient.otc.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.orient.otc.market.entity.MarketCloseData;

import java.time.LocalDate;

public interface MarketCloseDataMapper extends BaseMapper<MarketCloseData> {
    @Select("select closePrice from market_close_data where UPPER(instrumentID)=UPPER(#{underlyingCode}) and tradingDay = #{date}")
    Double getCloseDatePriceByCode(String underlyingCode, String date);
}
