package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.orient.otc.quote.entity.QuoteTemplate;

import java.util.List;

@Mapper
public interface QuoteTemplateMapper extends BaseMapper<QuoteTemplate> {
    @Select(
            {"<script> "+
            "SELECT distinct m.id, m.name, m.isDeleted,m.isPublic, m.creatorId, m.createTime, m.updatorId, m.updateTime " +
            "FROM otc_quote.quote_template m " +
            "left join otc_quote.quote_template_content d on m.id  = d.templateId " +
            "where "+
            "m.isDeleted =0 " +
            "and DATEDIFF(d.tradeDate,sysdate()) >=0 " +
            "and (m.isPublic=1 or m.creatorId=#{userId}) "+
            "order by m.createTime "+
            "</script> "
            })
    List<QuoteTemplate> selectListByUserId(@Param("userId") Integer userId);
}
