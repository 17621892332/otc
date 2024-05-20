package org.orient.otc.quote.controller.quote;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.quote.dto.QueryQctDto;
import org.orient.otc.quote.entity.QuotationControlTable;
import org.orient.otc.quote.service.QuotationControlTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/quotationControl")
@Api(tags = "交易配置")
public class QuotationControlController {
    @Autowired
    private QuotationControlTableService quotationControlTableService;
    @PostMapping("/queryQct")
    @ApiOperation("获取交易配置信息")
    @CheckPermission("system::quotationControl::queryQct")
    public HttpResourceResponse<Object> queryQct(@RequestBody @Valid QueryQctDto queryQctDto){

        LambdaQueryWrapper<QuotationControlTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(queryQctDto.getOptionType()),QuotationControlTable::getOptiontype,queryQctDto.getOptionType());
        queryWrapper.orderByAsc(QuotationControlTable::getItemsort);
        return HttpResourceResponse.success(quotationControlTableService.list(queryWrapper));
    }
}
