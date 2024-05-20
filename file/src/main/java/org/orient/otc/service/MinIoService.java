package org.orient.otc.service;

import org.orient.otc.api.file.dto.MinioUploadDTO;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface MinIoService {
    //获取列表
    List<String> listObjects();

    //删除
    void deleteObject(String objectName);

    //上传
    void uploadObject(InputStream is, String fileName, String contentType);

    /**
     * 上传文件
     * @param file 文件对象
     * @return 文件地址
     */
    String uploadObject(MultipartFile file);

    //获取minio中地址
    String getObjectUrl(String objectName);

    /**
     * 下载minio服务的文件
     * @param objectName 文件名称
     * @return 文件输入流
     */
    InputStream getObject(String objectName);

    /**
     * 创建桶
     * @param bucketName 桶名称
     */
    void createBucket(String bucketName);

    MinioUploadVO uploadObject(MinioUploadDTO dto) throws IOException;
}
