package org.orient.otc.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.orient.otc.quote.dto.trade.ExchangeTradePageListDto;
import org.orient.otc.quote.entity.ExchangeTrade;
import org.orient.otc.quote.vo.ExchangeTradeExportVo;
import org.orient.otc.quote.vo.ExchangeTradeVo;

import java.util.List;


public interface ExchangeTradeMapper extends BaseMapper<ExchangeTrade> {
    // 分页和导出公用一个sql
    String sql = "<script>" +
            "select (  " +
            "select a.name   from otc_user.exchange_account ea, otc_user.assetunit a  " +
            "where ea.assetunitId =  a.id  " +
            "and et.investorId = ea.account  ) as assetUnitName,   " + // 簿记账户
            "if(i.productClass=1,\"期货\",\"期权\") as tradeType, "+ // 交易类型
            "DATE_FORMAT(et.tradeDate,\"%Y-%m-%d\") as tradeDate,   " + // 成交日期
            "if(et.direction='0',if(et.offsetFlag='0','多头开仓','空头平仓'),if(et.offsetFlag='0','空头开仓','多头平仓')) as direction, "+ // 交易方向
            "if(i.productClass=1,i.instrumentId,i.underlyingInstrId) as underlyingCode, " + // 标的代码
            "if(i.productClass=1,i.instrumentName,(select underlyingName from  otc_dm.underlying_manager um  " +
            "where um.underlyingCode = i.underlyingInstrId " +
            "))as underlyingName,  " + // 标的名称
            "et.volume ,  " + // 交易手数
            "et.volume * i.volumeMultiple as volumeCount,  " + // 交易数量
            "et.price, " + // 成交价
            "concat(DATE_FORMAT(et.tradeDate,\"%Y-%m-%d\"),\" \",et.tradeTime) as operationTime " +
            "from otc_quote.exchange_trade et,  otc_dm.instrument i  " +
            "where   " +
            "et.instrumentId = i.instrumentId   " +
            "and et.isDeleted=0 and i.isDeleted =0 "+
            "<if test='dto.assetUnitGroupIds != null and dto.assetUnitGroupIds.size > 0'> " +
                "and exists (  " +
                "select 1 from otc_user.exchange_account ea,  " +
                "otc_user.assetunit a,  " +
                "otc_user.assetunit_group ag  " +
                "where ea.assetunitId =  a.id  " +
                "and et.investorId = ea.account  " +
                "and a.groupId = ag.id   " +
                "and ag.id in "+
                "<foreach collection='dto.assetUnitGroupIds' index='index' item='gid' open='(' separator=',' close=')'>#{gid}</foreach> "+
                ")"+
            "</if>"+
            "<if test='dto.assetUnitIds != null and dto.assetUnitIds.size > 0'> " +
            "and exists (  " +
            "select 1 from otc_user.exchange_account ea,  " +
            "otc_user.assetunit a  " +
            "where ea.assetunitId =  a.id  " +
            "and et.investorId = ea.account  " +
            "and a.id in "+
            "<foreach collection='dto.assetUnitIds' index='index' item='id' open='(' separator=',' close=')'>#{id}</foreach> "+
            ")"+
            "</if>"+
            "<if test='dto.underlyingCodes != null and dto.underlyingCodes.size > 0'> " +
            "and i.instrumentId in "+
            "<foreach collection='dto.underlyingCodes' index='index' item='underlyingCode' open='(' separator=',' close=')'>#{underlyingCode}</foreach> "+
            "</if>"+
            "<if test='dto.traderType != null and dto.traderType !=\"\"'> " +
            "and i.productClass=#{dto.traderType} "+
            "</if>"+
            "<if test='dto.direction != null and dto.direction == 1'> " + // 多头开仓
            "and et.direction='0' and et.offsetFlag='0'"+
            "</if>"+
            "<if test='dto.direction != null and dto.direction == 2'> " + // 多头平仓
            "and et.direction='1' and et.offsetFlag='1'"+
            "</if>"+
            "<if test='dto.direction != null and dto.direction == 3'> " + // 空头开仓
            "and et.direction='1' and et.offsetFlag='0'"+
            "</if>"+
            "<if test='dto.direction != null and dto.direction == 4'> " + // 空头平仓
            "and et.direction='0' and et.offsetFlag='1'"+
            "</if>"+
            "<if test='dto.tradeDateStart != null and dto.tradeDateEnd != null'> " + // 空头平仓
            "and et.tradeDate &lt;= #{dto.tradeDateEnd} and et.tradeDate &lt;= #{dto.tradeDateEnd}"+
            "</if>"+
            "</script>";
    @Select(sql)
    @ResultType(value=ExchangeTradeVo.class)
    IPage<ExchangeTradeVo> selectOptionListByPage(Page page, @Param("dto") ExchangeTradePageListDto dto);

    @Select(sql)
    @ResultType(value=ExchangeTradeVo.class)
    List<ExchangeTradeExportVo> exportOptionList(@Param("dto") ExchangeTradePageListDto dto);
}
