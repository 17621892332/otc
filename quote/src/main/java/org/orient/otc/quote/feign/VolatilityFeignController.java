package org.orient.otc.quote.feign;

import cn.hutool.extra.cglib.CglibUtil;
import org.orient.otc.api.dm.feign.UnderlyingManagerClient;
import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.dto.UnderlyingVolatilityFeignDto;
import org.orient.otc.api.quote.dto.VolatilityQueryDto;
import org.orient.otc.api.quote.dto.VolatityQueryCodeListDto;
import org.orient.otc.api.quote.dto.VolatitySaveDto;
import org.orient.otc.api.quote.feign.VolatilityClient;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.quote.dto.volatility.VolatilityListDto;
import org.orient.otc.quote.dto.volatility.VolatityDataDto;
import org.orient.otc.quote.entity.Volatility;
import org.orient.otc.quote.service.VolatilityService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dzrh
 */
@RestController
@RequestMapping(value = "volatility")
@Validated
public class  VolatilityFeignController implements VolatilityClient {
    @Resource
    VolatilityService volatilityService;

    @Resource
    UnderlyingManagerClient underlyingManagerClient;

    @Override
    public String saveBatch(List<VolatitySaveDto> list) {
        VolatilityListDto dto = new VolatilityListDto();
        dto.setVolatilityList(CglibUtil.copyList(list, Volatility::new,(vo,db)->{
            db.setData(CglibUtil.copyList(vo.getData(), VolatityDataDto::new));
        }));
        return volatilityService.insertOrUpdate(dto,Boolean.FALSE);
    }

    @Override
    public List<String> getUnderlyingCodeListByVol(VolatityQueryCodeListDto dto) {
        return volatilityService.getUnderlyingCodeListByVol(dto).stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toList());
    }

    @Override
    public Boolean checkHaveVolatility(VolatilityQueryDto volatilityQueryDto) {
        return !volatilityService.getVolatility(volatilityQueryDto).isEmpty();
    }

    @Override
    public Boolean updateVolByOffset(List<UnderlyingVolatilityFeignDto> underlyingVolatilityDtoList) {
        return volatilityService.updateVolByOffset(underlyingVolatilityDtoList);
    }

    @Override
    public List<VolatilityVO> getVolListByCodeSet(Set<String> underlyingCodeSet) {

        return CglibUtil.copyList( volatilityService.getNewVolatility(underlyingCodeSet, LocalDate.now()),VolatilityVO::new);
    }

    @Override
    public List<VolatilityVO> getMainVolList() {
       List<UnderlyingManagerVO> underlyingManagerVOList= underlyingManagerClient.getMainUnderlyingList();
        return CglibUtil.copyList( volatilityService.getNewVolatility(underlyingManagerVOList.stream().map(UnderlyingManagerVO::getUnderlyingCode).collect(Collectors.toSet()), LocalDate.now()),VolatilityVO::new);
    }
}
