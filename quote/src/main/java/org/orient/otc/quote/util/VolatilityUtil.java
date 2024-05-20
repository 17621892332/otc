package org.orient.otc.quote.util;

import org.orient.otc.common.core.util.BigDecimalUtil;
import org.orient.otc.common.jni.dto.VolSurface;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VolatilityUtil {
    /**
     * @param volatityDataList 波动率数据转换
     * @return 转换结果
     */
    public static VolSurface getVolSurface(List<VolatityDataDto> volatityDataList){
        VolSurface volSurface = new VolSurface();
        List<Double> strikeList = volatityDataList.stream().map(a -> a.getStrike().doubleValue()).distinct().collect(Collectors.toList());
        List<Double> expire = volatityDataList.stream().map(VolatityDataDto::getExpire).distinct().map(a -> (double) a).collect(Collectors.toList());

        //波动率曲面横轴
        volSurface.setVerticalAxis(strikeList.stream().mapToDouble(Double :: valueOf).toArray());
        volSurface.setVerticalAxisLength(strikeList.size());
        //波动率曲面纵轴
        volSurface.setHorizontalAxis(expire.stream().mapToDouble(Double :: valueOf).toArray());
        volSurface.setHorizontalAxisLength(expire.size());
        //波动率差值
        volSurface.setFlattenedVol(volatityDataList.stream().map(a -> BigDecimalUtil.percentageToBigDecimal(a.getVol()).doubleValue()).mapToDouble(Double :: valueOf).toArray());
        volSurface.setFlattenedVolLength(volatityDataList.size());
        return volSurface;
    }
    /**
     * 通过mid和ask和bid的offset计算出新的波动率曲面
     * @param mid
     * @param offset
     * @return
     */
    public static List<VolatityDataDto> getValatityDataByOffset(List<VolatityDataDto> mid,List<VolatityDataDto> offset){
        List<VolatityDataDto> result = new ArrayList<>();
        for(int i = 0; i<mid.size();i++){
            VolatityDataDto volatityDataDto = new VolatityDataDto();
            BeanUtils.copyProperties(mid.get(i),volatityDataDto);
            volatityDataDto.setVol(volatityDataDto.getVol().add(offset.get(i).getVol()));
            result.add(volatityDataDto);
        }
        return result;
    }
}
