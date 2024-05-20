package org.orient.otc.quote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.quote.dto.daily.DailyPageDTO;
import org.orient.otc.quote.vo.daily.EquityPositionDailyVO;
import org.orient.otc.quote.vo.daily.EquityTradeDailyVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * 权益报送服务
 */
public interface EquityDailyService {

    /**
     * 分页查询权益持仓信息
     * @param dto 查询日期
     * @return 查询结果
     */
    IPage<EquityTradeDailyVO> getEquityTradeDailyVOByPage(DailyPageDTO dto);

    /**
     * 导出权益持仓信息
     * @param queryDate 查询日期
     * @param response 响应流
     * @throws IOException 文件异常
     */
    void exportEquityTradeDaily(LocalDate queryDate, HttpServletResponse response) throws IOException;


    /**
     * 分页查询权益持仓信息
     * @param dto 查询日期
     * @return 查询结果
     */
    IPage<EquityPositionDailyVO> getEquityPositionDailyByPage(DailyPageDTO dto);

    void exportEquityPositionDaily(LocalDate queryDate, HttpServletResponse response) throws IOException;
}
