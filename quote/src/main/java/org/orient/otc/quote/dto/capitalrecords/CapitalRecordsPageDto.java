package org.orient.otc.quote.dto.capitalrecords;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.api.quote.enums.CapitalDirectionEnum;
import org.orient.otc.api.quote.enums.CapitalStatusEnum;
import org.orient.otc.common.core.dto.BasePage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class CapitalRecordsPageDto extends BasePage {
    /**
     * 发生时间-开始
     */
    @ApiModelProperty(value = "发生时间-开始")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startHappenTime;
    /**
     * 发生时间-结束
     */
    @ApiModelProperty(value = "发生时间-结束")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endHappenTime;
    /**
     * 归属时间-开始
     */
    @ApiModelProperty(value = "归属时间-开始")
    private LocalDate startVestingDate;
    /**
     * 归属时间-结束
     */
    @ApiModelProperty(value = "归属时间-结束")
    private LocalDate endVestingDate;
    /**
     * 客户id列表
     */
    @ApiModelProperty(value = "客户id列表")
    private List<Integer> clientIdList;
    /**
     * 交易编号
     */
    @ApiModelProperty(value = "交易编号")
    private String tradeCode;
    /**
     * 资金编号
     */
    @ApiModelProperty(value = "资金编号")
    private String capitalCode;
    /**
     * 方向类型数组
     */
    @ApiModelProperty(value = "方向类型数组")
    private List<CapitalDirectionEnum> directionList;
    /**
     * 资金状态数组
     */
    @ApiModelProperty(value = "资金状态数组")
    private List<CapitalStatusEnum> capitalStatusList;
    /**
     * 操作人数组
     */
    @ApiModelProperty(value = "操作人数组")
    private List<Integer> updatorIdList;

}
