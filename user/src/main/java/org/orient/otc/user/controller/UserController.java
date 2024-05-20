package org.orient.otc.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.orient.otc.common.core.web.HttpResourceResponse;
import org.orient.otc.common.security.annotion.CheckPermission;
import org.orient.otc.common.security.annotion.NoCheckLogin;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.User;
import org.orient.otc.user.service.UserService;
import org.orient.otc.user.vo.LoginVo;
import org.orient.otc.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户 API", description = "提供用户注册登录等接口")
public class UserController {
    @Autowired
    UserService userService;
    /**
     * 登录
     * @param userLoginDto 登录参数
     * @return 登录返回信息
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    @NoCheckLogin
    @CheckPermission(value = "user::user::login",isNeedCheck = false)
    public HttpResourceResponse<LoginVo> login(@RequestBody @Valid UserLoginDTO userLoginDto){
        return HttpResourceResponse.success(userService.login(userLoginDto));
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    @NoCheckLogin
    @CheckPermission(value = "user::user::logout",isNeedCheck = false)
    public   HttpResourceResponse<String> logout(HttpServletRequest httpRequest){
        return  HttpResourceResponse.success(userService.logout(httpRequest));
    }
    @ApiOperation("注册用户")
    @PostMapping("/register")
    @CheckPermission("user::user::register")
    public  HttpResourceResponse<String> register(@RequestBody @Valid UserRegisterDto userRegisterDto){
        return HttpResourceResponse.success(userService.register(userRegisterDto));
    }
    @ApiOperation("保存用户")
    @PostMapping("/saveUser")
    @CheckPermission("user::user::saveUser")
    public HttpResourceResponse<String> saveUser(@RequestBody @Valid UserSaveDto userSaveDto){
        return HttpResourceResponse.success(userService.saveUser(userSaveDto));
    }

    @ApiOperation("用户分页查询")
    @PostMapping("/getListBypage")
    @CheckPermission("user::user::getListBypage")
    public HttpResourceResponse<IPage<UserVo>> getListBypage(@RequestBody UserPageListDto userDto){
        return HttpResourceResponse.success(userService.getListBypage(userDto));
    }
    @ApiOperation("删除用户")
    @PostMapping("/deleteUser")
    @CheckPermission("user::user::deleteUser")
    public HttpResourceResponse<String> deleteUser(@RequestBody @Valid UserDeleteDto userDeleteDto){
        return HttpResourceResponse.success(userService.deleteUser(userDeleteDto));
    }

    @ApiOperation("获取用户详情")
    @PostMapping("/getUserDetails")
    @CheckPermission("user::user::getUserDetails")
    public HttpResourceResponse<UserVo> getUserDetails(@RequestBody @Valid UserDetailsDto dto){
        return HttpResourceResponse.success(userService.getUserDetails(dto.getId()));
    }

    @ApiOperation("校验输入的密码是否正确")
    @PostMapping("/checkPassword")
    @CheckPermission("user::user::checkPassword")
    public HttpResourceResponse<Boolean> checkPassword(@RequestBody @Valid UserUpdatePasswordDto dto){
        return HttpResourceResponse.success(userService.checkPassword(dto));
    }
    @ApiOperation("修改密码")
    @PostMapping("/modifyPassword")
    @CheckPermission("user::user::modifyPassword")
    public HttpResourceResponse<String> modifyPassword(@RequestBody @Valid UserUpdatePasswordDto dto){
        return HttpResourceResponse.success(userService.modifyPassword(dto));
    }

    @ApiOperation("获取交易员列表")
    @PostMapping("/traderList")
    public HttpResourceResponse<List<User>> traderList(){
        return HttpResourceResponse.success(userService.traderList());
    }
    @ApiOperation("获取所有用列表")
    @PostMapping("/getAllList")
    public HttpResourceResponse<List<User>> getAllList(){
        return HttpResourceResponse.success(userService.getAllList());
    }



}
