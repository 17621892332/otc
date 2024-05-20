package org.orient.otc.common.core.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author chengqiang
 */
public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 创建FileItem
     * @param file          文件对象
     * @param fieldName     文件名
     * @return  返回FileItem
     */
    public static FileItem createFileItem(File file, String fieldName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }


    /**
     * file转MultipartFile
     * @param file file对象
     * @return 返回MultipartFile对象
     */
    public static MultipartFile file2MultipartFile(File file){
        log.info("file入参="+file.getName());
        FileItem fileItem = createFileItem(file,file.getName());
        return new CommonsMultipartFile(fileItem);
    }
}
