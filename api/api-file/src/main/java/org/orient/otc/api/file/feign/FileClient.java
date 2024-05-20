package org.orient.otc.api.file.feign;

import feign.Response;
import org.orient.otc.api.file.dto.DownloadDTO;
import org.orient.otc.api.file.enums.FileTypeEnum;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author dzrh
 */
@FeignClient(value = "fileserver",path = "/file", contextId ="file")
public interface FileClient {
    /**
     * 发送邮件
     * @param file 文件内容
     * @param fileType 文件类型
     */
    @PostMapping(value=FeignConfig.FEIGN_INSIDE_URL_PREFIX+"/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    MinioUploadVO upload(@RequestPart("file")MultipartFile file, @RequestParam("fileType")FileTypeEnum fileType) throws IOException;

    @PostMapping(value = FeignConfig.FEIGN_INSIDE_URL_PREFIX + "/getFile")
    Response getFile(@RequestBody DownloadDTO dto) throws IOException;
}
