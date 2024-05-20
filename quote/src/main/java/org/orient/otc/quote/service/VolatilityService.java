package org.orient.otc.quote.service;

import org.orient.otc.api.dm.vo.UnderlyingManagerVO;
import org.orient.otc.api.quote.dto.UnderlyingVolatilityFeignDto;
import org.orient.otc.api.quote.dto.VolatilityQueryDto;
import org.orient.otc.api.quote.dto.VolatityQueryCodeListDto;
import org.orient.otc.api.quote.enums.VolTypeEnum;
import org.orient.otc.api.quote.vo.VolatilityVO;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.quote.dto.volatility.DeltaVolToStrikeVolDto;
import org.orient.otc.quote.dto.volatility.LinearInterpVolSurfaceDto;
import org.orient.otc.quote.dto.volatility.VolatilityListDto;
import org.orient.otc.quote.entity.Volatility;
import org.orient.otc.quote.vo.volatility.DeltaVolToStrikeVolVo;
import org.orient.otc.quote.vo.volatility.LinearInterpVolSurfaceVo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 波动率服务
 */
public interface VolatilityService extends IServicePlus<Volatility> {
    /**
     * 查询波动率
     * @param volatilityQueryDto 查询条件
     * @return 波动率列表
     */
    List<Volatility> getVolatility(VolatilityQueryDto volatilityQueryDto);

    /**
     * 获取交易日有波动率的合约代码列表
     * @param localDate 交易日期
     * @return 合约代码
     */
    List<String> getUnderlyingCodeByVol(LocalDate localDate);

    /**
     * 获取交易日有波动率的合约代码列表
     * @return 波动率列表
     */
    List<String> getNewUnderlyingCodeByVol();
    /**
     * 获取最新波动率
     * @param underlyingCode 合约代码
     * @param type 波动率类别
     * @return 波动率列表
     */
    VolatilityVO getNewVolatilityByType(String underlyingCode, VolTypeEnum type);
    /**
     * 获取最新波动率
     * @param underlyingCode 合约代码
     * @param tradeDate  交易日期
     * @param type 波动率类别
     * @return 波动率列表
     */
    VolatilityVO getVolatilityByTypeAndDate(String underlyingCode,LocalDate tradeDate, VolTypeEnum type);
    /**
     * 获取最新波动率
     * @param underlyingCodeList 合约代码列表
     * @param tradeDate 交易日期
     * @return 波动率列表
     */
     List<Volatility> getNewVolatility(Set<String> underlyingCodeList, LocalDate tradeDate);
    /**
     * 保存更新波动率
     * @param volatilityList 波动率列表
     * @param isNeedSync 是否需要同步镒链
     * @return 保存信息
     */
    String insertOrUpdate(VolatilityListDto volatilityList,Boolean isNeedSync);

    /**
     * 计算波动率插值
     * @param linearInterpVolSurfaceDto  插值参数
     * @return min和交易波动率
     */
    LinearInterpVolSurfaceVo linearInterpVolSurface(LinearInterpVolSurfaceDto linearInterpVolSurfaceDto);

    /**
     * 批量计算波动率
     * @param list
     * @return
     */
    List<LinearInterpVolSurfaceVo> linearInterpVolSurfaceBatch(List<LinearInterpVolSurfaceDto> list);

    /**
     * 波动率转换
     * @param data delta数据
     * @return 转换后的数据
     */
    DeltaVolToStrikeVolVo deltaVolToStrikeVol(DeltaVolToStrikeVolDto data);

    /**
     * 获取存活的合约列表，并且有波动率的
     * @param dto  交易日期
     * @return 合约代码列表
     */
    List<UnderlyingManagerVO> getUnderlyingCodeListByVol(VolatityQueryCodeListDto dto);

    /**
     * 将波动率数据落库保存
     * @param tradeDay 工作日
     * @return true 复制成功 false 复制失败
     */
    Boolean saveVolToTradeDay(LocalDate tradeDay);

    /**
     * 保存波动率
     * @param volatilityList 波动率列表
     * @return 保存结果
     */
    String saveVolatility(VolatilityListDto volatilityList);

    /**
     * 保存波动率偏移量
     * @param underlyingVolatilityDtoList 波动率合约列表
     * @return 是否保存成功
     */
    Boolean updateVolByOffset(List<UnderlyingVolatilityFeignDto> underlyingVolatilityDtoList);

}
