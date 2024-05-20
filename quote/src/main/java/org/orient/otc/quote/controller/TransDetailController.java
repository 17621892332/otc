package org.orient.otc.quote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.daily.transdetail.StatusConvertDto;
import org.orient.otc.quote.dto.daily.transdetail.TransDetailPageDto;
import org.orient.otc.quote.service.TransDetailService;
import org.orient.otc.quote.vo.transdetail.TransDetailListVO;
import org.orient.otc.quote.vo.transdetail.TransDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author xxy
 */
@RestController
@RequestMapping("/transDetail")
@Api(tags = "资金流水")
public class TransDetailController {
    @Autowired
    private TransDetailService transDetailService;

    /**
     * 拉取资金信息
     *
     * @return 拉取结果
     */
    @GetMapping("/syncTransDetail")
    @ApiOperation("拉取资金信息")
    public HttpResourceResponse<String> syncTransDetail() {
        return HttpResourceResponse.success(transDetailService.getTransDetail());
    }

    /**
     * 资金流水列表
     *
     * @return 拉取结果
     */
    @PostMapping("/getListByPage")
    @ApiOperation("资金流水列表")
    @CheckPermission
    public HttpResourceResponse<IPage<TransDetailListVO>> getListByPage(@RequestBody TransDetailPageDto dto) {
        return HttpResourceResponse.success(transDetailService.getListByPage(dto));
    }

    /**
     * 资金流水详情
     *
     * @return 拉取结果
     */
    @PostMapping("/getDetail")
    @ApiOperation("资金流水详情")
    @CheckPermission
    public HttpResourceResponse<TransDetailVO> getDetail(@RequestBody StatusConvertDto dto) {
        return HttpResourceResponse.success(transDetailService.getDetail(dto));
    }

    /**
     * 场外状态确认
     *
     * @return 拉取结果
     */
    @PostMapping("/statusConvertY")
    @ApiOperation("场外状态确认")
    @CheckPermission
    public HttpResourceResponse<String> statusConvertY(@RequestBody StatusConvertDto dto) {
        return HttpResourceResponse.success(transDetailService.statusConvertY(dto));
    }

    /**
     * 场外状态反确认
     *
     * @return 拉取结果
     */
    @PostMapping("/statusConvertN")
    @ApiOperation("场外状态反确认")
    @CheckPermission
    public HttpResourceResponse<String> statusConvertN(@RequestBody StatusConvertDto dto) {
        return HttpResourceResponse.success(transDetailService.statusConvertN(dto));
    }
}
