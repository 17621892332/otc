package org.orient.otc.quote.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.OptionTypeEnum;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName(autoResultMap = true)
@ApiModel
public class SnowKnockedinLog extends BaseEntity implements Serializable {
    @ApiModelProperty(value = "id")
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    private LocalDate knockedInDate;
    private String underlyingCode;
    private String tradeCode;
    private OptionTypeEnum optionType;
    private String reamrks;
    private BigDecimal knockinBarrierValue;
    private BigDecimal closePrice;

}
