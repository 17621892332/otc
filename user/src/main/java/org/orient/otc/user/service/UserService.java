package org.orient.otc.user.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.database.config.IServicePlus;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.User;
import org.orient.otc.user.vo.LoginVo;
import org.orient.otc.user.vo.UserPermissionVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface UserService extends IServicePlus<User> {

    LoginVo login(UserLoginDTO userLoginDto);

    String logout(HttpServletRequest httpRequest);
    String register(UserRegisterDto userRegisterDto);

    /**
     * 保存用户信息
     * @param userSaveDto 用户保存对象
     * @return 是否保存成功
     */
    String saveUser(UserSaveDto userSaveDto);
    List<UserVo> getList();

    /**
     * 获取用户对应的权限列表
     * @param userId 用户ID
     * @return 权限code列表
     */
    List<UserPermissionVo> getUserRole(Integer userId,Integer loginFrom);

    /**
     * 获取交易员列表
     * @param ids
     * @return
     */
    List<User> queryByIds(Set<Integer> ids);

    /**
     * 获取用户列表(分页)
     * @param userDto
     * @return
     */
    IPage<org.orient.otc.user.vo.UserVo> getListBypage(UserPageListDto userDto);

    /**
     * 删除用户
     * @param userDeleteDto
     * @return
     */
    String deleteUser(UserDeleteDto userDeleteDto);

    /**
     * 获取用户详情
     * @param id
     * @return
     */
    org.orient.otc.user.vo.UserVo getUserDetails(Integer id);

    Boolean checkPassword(UserUpdatePasswordDto dto);

    String modifyPassword(UserUpdatePasswordDto dto);

    List<User> traderList();

    List<User> getAllList();
}
