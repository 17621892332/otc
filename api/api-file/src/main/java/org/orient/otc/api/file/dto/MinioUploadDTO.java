package org.orient.otc.api.file.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.file.enums.FileTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MinioUploadDTO  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 附件
     */
    private MultipartFile file = null;
    /**
     * 附件类型
     */
    private FileTypeEnum fileType;
}
