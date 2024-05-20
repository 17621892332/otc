package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.database.entity.BaseEntity;
import org.orient.otc.quote.handler.QuoteTemplateContentDataTypeHandler;
import org.orient.otc.quote.handler.VolatityDeltaDataListTypeHandler;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 交易模板
 * @author dzrh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class QuoteTemplateContent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 模板id
     */
    private Integer templateId;

    /**
     * 到期日
     */
    private LocalDate maturityDate;

    /**
     * 顺序
     */
    private Integer sort;

    @TableField(typeHandler = QuoteTemplateContentDataTypeHandler.class)
    private QuoteTemplateContentData data;
}
