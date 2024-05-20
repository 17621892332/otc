package org.orient.otc.quote.vo;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.annotation.PropIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.orient.otc.common.core.config.BigDecimalFormatter;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 资金记录导出VO
 * 每一个导出的属性都要使用@Alias修饰
 * 不需要导出的属性
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CapitalRecordsExportVO implements Serializable {
    @Alias(value = "操作时间")
    private String happenTime;
    @Alias(value = "归属时间")
    private String vestingDate;
    @Alias(value = "资金编号")
    private String capitalCode;
    @Alias(value = "客户名称")
    private String clientName;
    @Alias(value = "方向类型")
    private String directionName;
    @Alias(value = " 金额")
    @BigDecimalFormatter(shape = JsonFormat.Shape.STRING,pattern = "###0.00")
    private BigDecimal money;
    @Alias(value = "币种")
    private String currency;
    @Alias(value = " 相关交易")
    private String tradeCode;
    @Alias(value = "标的代码")
    private String underlyingCode;
    @Alias(value = "资金状态")
    private String capitalStatusName;
    @Alias(value = "操作人")
    private String updatorName;
    @Alias(value = "创建人")
    private String creatorName;
    @Alias(value = "客户id")
    private Integer clientId;
}
