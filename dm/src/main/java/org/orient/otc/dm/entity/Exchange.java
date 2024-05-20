package org.orient.otc.dm.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;

/**
 * (Exchange)表实体类
 * @author makejava
 * @since 2023-07-20 13:44:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(autoResultMap = true)
public class Exchange extends BaseEntity {
    //主键
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    //交易所代码
    private String code;
    //交易所名称
    private String name;
    //交易所中文简称
    private String shortname;
}

