package org.orient.otc.client.vo;

import lombok.Data;

import java.util.Set;

/**
 * <p>
 * 客户邮件VO
 * </p>
 * @author chengqiang
 */
@Data
public class ClientMailVO {
    /**
     * 客户联系人ID
     */
    private Integer id;
    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * Email , 多个之间用逗号拼接
     */
    private String email;

    /**
     * 邮箱列表
     */
    private Set<String> emailSet;
}
