package org.orient.otc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.file.dto.MinioUploadDTO;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.service.MinIoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * MinIO对象存储管理
 *
 * @author macro
 * &#064;date  2019/12/25
 */
@Api(tags = "MinIO对象存储管理")
@Controller
@RequestMapping("/minio")
@Slf4j
public class FileController {

    @Resource
    MinIoService minIoService;

    @ApiOperation("文件上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public HttpResourceResponse<MinioUploadVO> upload(MinioUploadDTO uploadDTO) throws IOException {
        return HttpResourceResponse.success(minIoService.uploadObject(uploadDTO));
    }
}
