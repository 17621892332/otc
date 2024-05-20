package org.orient.otc.service.impl;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.orient.otc.api.file.dto.MinioUploadDTO;
import org.orient.otc.api.file.vo.MinioUploadVO;
import org.orient.otc.config.MinioConfig;
import org.orient.otc.exeption.BusinessException;
import org.orient.otc.service.MinIoService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinIoServiceImpl implements MinIoService {

    @Resource
    private MinioConfig minioConfig;
    @Resource
    private MinioClient minioClient;

    @Override
    public List<String> listObjects() {
        List<String> list = new ArrayList<>();
        try {

            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build();

            Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            for (Result<Item> result : results) {
                Item item = result.get();
                log.info("{}, {}, {}", item.lastModified(), item.size(), item.objectName());
                list.add(item.objectName());
            }
        } catch (Exception e) {
            log.error("错误：{}", e.getMessage());
        }
        return list;
    }

    @Override
    public void deleteObject(String objectName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("deleteObject Error：{}", e.getMessage());
        }
    }

    @Override
    public void uploadObject(InputStream is, String fileName, String contentType) {
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .contentType(contentType)
                    .stream(is, is.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            is.close();
        } catch (Exception e) {
            log.error("uploadObjectByInputStream Error：{}", e.getMessage());
            BusinessException.E_110100.assertTrue(true);
        }
    }

    @Override
    public String uploadObject(MultipartFile file) {
        final InputStream is;
        try {
            is = file.getInputStream();
            final String fileName = file.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .stream(is, is.available(), -1)
                    .object("/" + fileName).build());
            is.close();
            return getObjectUrl(fileName);
        } catch (Exception e) {
            log.error("uploadObjectByMultipartFile Error：{}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取minio文件的下载地址
     * @param bucketName: 桶名
     * @param fileName:   文件名
     */
    public String getFileUrl(String bucketName, String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            log.error("错误：{}", e.getMessage());
        }
        return null;
    }


    //获取minio中地址
    @Override
    public String getObjectUrl(String objectName) {
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .expiry(7, TimeUnit.DAYS)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (Exception e) {
            log.error("错误：{}", e.getMessage());
        }
        return "";
    }

    @Override
    public InputStream getObject(String objectName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build();
            return minioClient.getObject(getObjectArgs);
        } catch (Exception e) {
            log.error("错误：{}", e.getMessage());
        }
        return null;
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("错误：{}", e.getMessage());
        }
    }

    @Override
    public MinioUploadVO uploadObject(MinioUploadDTO uploadDTO) throws IOException {
        String filename = uploadDTO.getFile().getOriginalFilename();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 设置存储对象名称
        String objectName = uploadDTO.getFileType().name() + "/" + sdf.format(new Date()) + "/" + filename;
        this.uploadObject(uploadDTO.getFile().getInputStream(), objectName, uploadDTO.getFile().getContentType());
        MinioUploadVO minioUploadVO = new MinioUploadVO();
        minioUploadVO.setName(filename);
        minioUploadVO.setPath(objectName);
        minioUploadVO.setUrl(minioConfig.getUrl() + "/" + minioConfig.getBucketName() +"/" +  objectName);
        return minioUploadVO;
    }
}
