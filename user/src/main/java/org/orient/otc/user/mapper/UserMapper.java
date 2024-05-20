package org.orient.otc.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.user.entity.User;
import org.orient.otc.user.vo.AssetUnitUserVo;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据角色ID统计未删除的用户数量
     * @param roleId
     * @return
     */
    @Select("select count(1) from otc_user.`user`  u,otc_user.user_role ur where u.id = ur.userId and u.isDeleted = 0 and ur.roleId = #{roleId}")
    Integer countUserByRoleId(Integer roleId);

    /**
     * 根据簿记账户ID查询交易员信息
     * @return
     */
    @Select("<script> " +
            "select distinct  u.id, u.account, u.password, u.salt, u.name, u.jobNumber, u.birthday, u.sex, u.email, u.phone,  " +
            "u.roleId, u.status, u.ylUser, u.ylPassword, u.version, u.isDeleted, u.creatorId, u.createTime, u.updatorId, u.updateTime, au.assetunitId    " +
            "FROM otc_user.`user` u,otc_user.assetunit_user au  " +
            "where u.id=au.traderId  " +
            "and au.isDeleted = 0 " +
            "and u.isDeleted = 0 " +
            "and au.assetunitId in <foreach item='item' index='index' collection='assetUnitIds' open='(' separator=',' close=')'>#{item}</foreach> "+
            "</script>")
    List<AssetUnitUserVo> selectListByAssetUnitIds(@Param("assetUnitIds") List<Integer> assetUnitIds);

}
