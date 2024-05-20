package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.file.enums.FileTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
/**
 * 交易相关文件
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
public class TradeContractDocument extends BaseEntity implements Serializable {

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 合约编号
     */
    private String contractCode;

    /**
     * 文件类型
     */
    private FileTypeEnum fileType;
    /**
     * 文件URL
     */
    private String url;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 文件名称
     */
    private String fileName;
}
