package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.orient.otc.quote.entity.Volatility;

import java.time.LocalDate;
import java.util.List;

public interface VolatilityMapper extends BaseMapper<Volatility> {
    @Select("<script> select * from volatility</script>")
    List<Volatility> getList();

    /**
     * 复制波动率到下一个工作日
     * @param nextWorkDay 下一个工作日
     * @param today 当前工作日
     * @param underlyingCodes 有效的合约代码
     * @return 更新数量
     */
    @Insert({"<script> insert into volatility (underlyingCode, quotationDate, volType, data, deltaData, interpolationMethod)\n" ,
            "select  underlyingCode, #{nextWorkDay}, volType, data, deltaData, interpolationMethod\n" ,
            "from volatility v  where quotationDate= #{today} " ,
            "and isDeleted=0</script>"})
    Integer copVolToNextWorkDay(@Param("nextWorkDay") LocalDate nextWorkDay,@Param("today") LocalDate today);
}
