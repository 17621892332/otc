package org.orient.otc.dm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.orient.otc.dm.entity.UnderlyingQuote;
import org.orient.otc.api.dm.vo.UnderlyingQuoteVO;

import java.util.List;

public interface UnderlyingQuoteMapper extends BaseMapper<UnderlyingQuote> {
    @Select(" SELECT u.*,v.varietyCode,v.varietyName, if(vp.typeName='有色金属' or vp.typeName='贵金属','有色贵金属',vp.typeName) as varietyTypeName FROM underlying_quote u " +
            " left JOIN variety v ON u.varietyId = v.id " +
            " left JOIN variety_type vp ON v.varietyTypeId = vp.id " +
            " WHERE u.isDeleted = 0 order by  u.sort")
    List<UnderlyingQuoteVO> selectJoinData();
}
