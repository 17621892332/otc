package org.orient.otc.api.user.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AssetunitVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */

    private Integer id;

    private String name;

    private Integer groupId;

    /**
     * 基础币种
     */
    private String baseCurrency;


}
