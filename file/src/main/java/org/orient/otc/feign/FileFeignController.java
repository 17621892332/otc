package org.orient.otc.feign;

import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.file.dto.DownloadDTO;
import org.orient.otc.api.file.dto.MinioUploadDTO;
import org.orient.otc.api.file.enums.FileTypeEnum;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.orient.otc.common.core.feign.FeignConfig;
import org.orient.otc.service.MinIoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author dzrh
 */
@RestController
@RequestMapping(value = "file")
@Slf4j
public class FileFeignController {
    @Resource
    MinIoService minIoService;


    @PostMapping(value = FeignConfig.FEIGN_INSIDE_URL_PREFIX + "/upload")
    public MinioUploadVO upload(MultipartFile file, FileTypeEnum fileType) throws IOException {
        MinioUploadDTO minioUploadDTO = new MinioUploadDTO();
        minioUploadDTO.setFile(file);
        minioUploadDTO.setFileType(fileType);
        return minIoService.uploadObject(minioUploadDTO);
    }

    @PostMapping(value = FeignConfig.FEIGN_INSIDE_URL_PREFIX + "/getFile")
    public void getFile(HttpServletResponse response,@RequestBody DownloadDTO dto) {
        String fileName = dto.getPath().substring(dto.getPath().lastIndexOf("/") + 1);
        try (InputStream inputStream = minIoService.getObject(dto.getPath())) {
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.close();
        } catch (Exception e) {
            log.error("file download from minio exception, file name: {}", fileName, e);
        }

    }

}
