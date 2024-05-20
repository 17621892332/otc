package org.orient.otc.message.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

/**
 * @author chengqiang
 */
@Data
public class MultiSendMailDto {
    /**
     * 客户id集合
     */
    @NotEmpty(message = "客户不能为空")
    List<Integer> clientIdList;

    /**
     * 查询日期
     */
    @NotEmpty(message = "查询日期不能为空")
    private LocalDate queryDate;
}
