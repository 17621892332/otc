package org.orient.otc.quote.controller.daily;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.quote.dto.daily.DailyPageDTO;
import org.orient.otc.quote.service.EquityDailyService;
import org.orient.otc.quote.vo.daily.EquityPositionDailyVO;
import org.orient.otc.quote.vo.daily.EquityTradeDailyVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 权益日报
 */
@RestController
@RequestMapping("/equityDaily")
@Api(tags = "权益日报")
public class EquityDailyController {

    @Resource
    private EquityDailyService equityDailyService;

    /**
     * 权益交易记录分页
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("权益交易记录分页")
    @PostMapping("/getEquityTradeDailyByPage")
    public HttpResourceResponse<IPage<EquityTradeDailyVO>> getEquityTradeDailyByPage(@RequestBody @Valid DailyPageDTO dto){
        return HttpResourceResponse.success(equityDailyService.getEquityTradeDailyVOByPage(dto));
    }
    /**
     * 结算报告导出
     * @param dailyPageDTO 结算报告请求参数
     * @param response 文件流
     * @throws Exception IO异常
     */
    @PostMapping("/reportEquityTradeDaily")
    public void reportEquityTradeDaily(@RequestBody @Valid DailyPageDTO dailyPageDTO, HttpServletResponse response) throws Exception {
        equityDailyService.exportEquityTradeDaily(dailyPageDTO.getQueryDate(),response);
    }
    /**
     * 权益交易记录分页
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("权益交易记录分页")
    @PostMapping("/getEquityPositionDailyByPage")
    public HttpResourceResponse<IPage<EquityPositionDailyVO>> getEquityPositionDailyByPage(@RequestBody @Valid DailyPageDTO dto){
        return HttpResourceResponse.success(equityDailyService.getEquityPositionDailyByPage(dto));
    }
    /**
     * 结算报告导出
     * @param dailyPageDTO 结算报告请求参数
     * @param response 文件流
     * @throws Exception IO异常
     */
    @PostMapping("/reportEquityPositionDaily")
    public void reportEquityPositionDaily(@RequestBody @Valid DailyPageDTO dailyPageDTO, HttpServletResponse response) throws Exception {
        equityDailyService.exportEquityPositionDaily(dailyPageDTO.getQueryDate(),response);
    }
}
