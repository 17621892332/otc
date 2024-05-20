package org.orient.otc.user.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.orient.otc.common.database.entity.BaseEntity;

import java.io.Serializable;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author 潘俊材
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class Permission extends BaseEntity implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 权限代码
     */
	private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限类型
     */
    private Integer permissionType;
    /**
     * 父级ID
     */
    private Integer parentId;
    /**
     * 权限所属类别 (1 : vue端 , 0: 客户端)
     */
    private Integer type;
    /**
     * 备注
     */
    private String notes;
    /**
     * 图标
     */
    private String icon;

    /**
     * 是否隐藏（true:隐藏，false:显示）
     */
    private Boolean hidden;

    /**
     * 前端组件名称
     */
    private String name;
    /**
     * 前端路由地址（访问地址）
     */
    private String path;
    /**
     * 前端组件路径
     */
    private String component;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 组件是否缓存(true:缓存， false:不缓存)
     */
    private Boolean isCache;
    /**
     * 菜单状态(1:正常，0:禁用)
     */
    private Integer status;

}
