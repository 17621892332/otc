package org.orient.otc.quote.dto.quote;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.orient.otc.common.database.config.LocalDateDeserializer;
import org.orient.otc.common.database.config.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel
public class TradeObsDateDto {
    @ApiModelProperty(value = "观察日期")
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate obsDate;


    /**
     * 结算日期
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate settlementDate;

    /**
     * 结算天数
     */
    private Integer dayCount;

    @ApiModelProperty(value = "观察价格")
    private BigDecimal price;
    /**
     * 障碍价格
     */
    @ApiModelProperty(value = "障碍价格")
    private BigDecimal barrier;

    /**
     * 障碍是否为相对水平值
     */
    @ApiModelProperty(value = "障碍是否为相对水平值")
    private Boolean barrierRelative;
    /**
     * 障碍Shift
     */
    @ApiModelProperty(value = "障碍Shift")
    private BigDecimal barrierShift;

    /**
     * 敲出票息
     */
    @ApiModelProperty(value = "敲出票息")
    private BigDecimal rebateRate;

    /**
     * 敲出票息是否年化
     */
    @ApiModelProperty(value = "敲出票息是否年化")
    private Boolean rebateRateAnnulized;

}
