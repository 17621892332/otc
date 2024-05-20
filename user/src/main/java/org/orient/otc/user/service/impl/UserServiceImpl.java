package org.orient.otc.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.database.enums.IsDeletedEnum;
import org.orient.otc.common.security.adapter.AuthAdapter;
import org.orient.otc.common.security.dto.AuthorizeInfo;
import org.orient.otc.common.security.dto.Md5PasswordDto;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.entity.*;
import org.orient.otc.user.exception.BussinessException;
import org.orient.otc.user.mapper.RolePermissionMapper;
import org.orient.otc.user.mapper.UserMapper;
import org.orient.otc.user.service.PermissionService;
import org.orient.otc.user.service.RoleService;
import org.orient.otc.user.service.UserRoleService;
import org.orient.otc.user.service.UserService;
import org.orient.otc.user.vo.LoginVo;
import org.orient.otc.user.vo.UserPermissionVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleService roleService;

    @Override
    public LoginVo login(UserLoginDTO userLoginDto) {
        //获取用户账号信息
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccount, userLoginDto.getAccount())
                .eq(User :: getIsDeleted, 0).eq(User::getStatus,1));
        BussinessException.E_100102.assertTrue(Objects.nonNull(user));
        //校验密码是否正确
        BussinessException.E_100103.assertTrue(Md5Crypt.md5Crypt(userLoginDto.getPassword().getBytes()
                , user.getSalt()).equals(user.getPassword()));
        //设置用户token
        AuthorizeInfo authorizeInfo = new AuthorizeInfo();
        BeanUtils.copyProperties(user,authorizeInfo);
        //设置登录来源
        authorizeInfo.setLoginForm(userLoginDto.getLoginFrom());
        //生成token存在redis中
        String token = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        stringRedisTemplate.opsForValue().set(RedisAdapter.SESSION_DIRECTORY + token, JSONObject.toJSONString(authorizeInfo)
                , AuthAdapter.TokenExpireTime, TimeUnit.MINUTES);
        LoginVo loginVo = new LoginVo();
        loginVo.setPermission(getUserRole(user.getId(),userLoginDto.getLoginFrom()));
        loginVo.setToken(token);
        loginVo.setName(user.getName());
        loginVo.setId(user.getId());
        return loginVo;
    }

    @Override
    public String logout(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(AuthAdapter.AuthToken);
        if (Objects.nonNull(token)) {
            stringRedisTemplate.delete(RedisAdapter.SESSION_DIRECTORY + token);
        }
        return "logout success";
    }
    @Transactional
    @Override
    public String register(UserRegisterDto userRegisterDto) {
        RLock lock = redissonClient.getLock("lock:registerUser");
        lock.lock();
        try {
            BussinessException.E_100104.assertTrue(Objects.isNull(userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccount, userRegisterDto.getAccount()).eq(User::getIsDeleted, 0))));
            User user = new User();
            BeanUtils.copyProperties(userRegisterDto, user);
            Md5PasswordDto md5Password = this.getMd5Password(userRegisterDto.getPassword());
            user.setPassword(md5Password.getMd5Password());
            user.setSalt(md5Password.getSalt());
            userMapper.insert(user);
           List<Integer> roleList=  userRegisterDto.getRoleIdList();
            if (roleList!=null && !roleList.isEmpty()){
                userRoleService.savaUserRole(roleList,user.getId());
            }
        } finally {
            lock.unlock();
        }
        return "register success";
    }

    @Transactional
    @Override
    public String saveUser(UserSaveDto userSaveDto) {
        RLock lock = redissonClient.getLock("lock:saveUserInfo");
        lock.lock();
        try{
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, userSaveDto.getId()).eq(User::getIsDeleted, 0));
            BussinessException.E_100102.assertTrue(!Objects.isNull(user));
            BeanUtils.copyProperties(userSaveDto, user);
        /*Md5PasswordDto md5Password = securityUtils.getMd5Password(userSaveDto.getPassword());
        user.setPassword(md5Password.getMd5Password());
        user.setSalt(md5Password.getSalt());*/
            userMapper.updateById(user);
            // 删除原有权限
            LambdaUpdateWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaUpdateWrapper<>();
            userRoleLambdaQueryWrapper.eq(UserRole::getUserId,user.getId())
                            .set(UserRole::getIsDeleted,IsDeletedEnum.YES);
            userRoleService.update(userRoleLambdaQueryWrapper);
            //更新权限信息
            List<Integer> roleList=  userSaveDto.getRoleIdList();
            if (roleList!=null && !roleList.isEmpty()){
                userRoleService.savaUserRole(roleList,user.getId());
            }
        } catch (Exception e) {
            log.error("保存用户异常:",e);
        } finally {
            lock.unlock();
        }
        return "save success";
    }

    @Override
    public List<UserVo> getList() {
        return this.listVo(UserVo.class);
    }

    @Override
    public List<UserPermissionVo> getUserRole(Integer userId,Integer loginFrom) {

        //获取用户对应的角色列表
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserRole::getRoleId);
        queryWrapper.eq(UserRole::getUserId, userId);
        queryWrapper.eq(UserRole::getIsDeleted, 0);
        List<Integer> roleList = userRoleService.getBaseMapper().selectList(queryWrapper)
                .stream().map(UserRole::getRoleId).collect(Collectors.toList());
        //获取角色对应的权限ID
        if(roleList.isEmpty()){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<RolePermission> rolePermissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionLambdaQueryWrapper.select(RolePermission::getPermissionId)
                .in(RolePermission::getRoleId,roleList)
                .eq(RolePermission::getIsDeleted,0);
        List<Integer> permissionIdList= rolePermissionMapper.selectList(rolePermissionLambdaQueryWrapper)
                .stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        //获取对应的权限代码
        LambdaQueryWrapper<Permission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        permissionLambdaQueryWrapper.in(Permission::getId,permissionIdList)
                .eq(Permission::getIsDeleted,0);
        return permissionService.listVo(permissionLambdaQueryWrapper, UserPermissionVo.class);
    }

    @Override
    public List<User> queryByIds(Set<Integer> ids) {
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDeleted, IsDeletedEnum.NO);
        queryWrapper.in(ids!=null && !ids.isEmpty(),User::getId,ids);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<org.orient.otc.user.vo.UserVo> getListBypage(UserPageListDto userDto) {
        List<Integer> userIds = null;
        if (null!=userDto.getRoleIdList() && !userDto.getRoleIdList().isEmpty()) {
            LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userRoleLambdaQueryWrapper.in(UserRole::getRoleId,userDto.getRoleIdList());
            userRoleLambdaQueryWrapper.eq(UserRole::getIsDeleted,IsDeletedEnum.NO);
            // 查询用户和角色关联信息
            List<UserRole> userRoleList = userRoleService.list(userRoleLambdaQueryWrapper);
            userIds = userRoleList.stream().map(UserRole::getUserId).distinct().collect(Collectors.toList());
        }
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(userDto.getName()),User::getName, userDto.getName())
                .eq(StringUtils.isNotEmpty(userDto.getAccount()),User::getAccount,userDto.getAccount())
                .eq(StringUtils.isNotEmpty(userDto.getJobNumber()),User::getJobNumber,userDto.getJobNumber())
                .eq(StringUtils.isNotEmpty(userDto.getPhone()),User::getPhone,userDto.getPhone())
                .eq(null!=userDto.getStatus(),User::getStatus,userDto.getStatus())
                .eq(null!=userDto.getBirthday(),User::getBirthday,userDto.getBirthday())
                .eq(null!=userDto.getSex(),User::getSex,userDto.getSex())
                .eq(StringUtils.isNotEmpty(userDto.getEmail()),User::getEmail,userDto.getEmail())
                .in(null!=userIds && !userIds.isEmpty(),User::getId,userIds)
                .eq(User::getIsDeleted,0);

        Page<User> dbPage= this.page(new Page<>(userDto.getPageNo(),userDto.getPageSize()),queryWrapper);
        // 返回值类型转换
        IPage<org.orient.otc.user.vo.UserVo> returnPage = dbPage.convert(user->{
            org.orient.otc.user.vo.UserVo userVo = new org.orient.otc.user.vo.UserVo();
            BeanUtils.copyProperties(user,userVo);
            return userVo;
        });
        // 查询当前页所有用户的角色
        if (null != returnPage && null != returnPage.getRecords()) {
            returnPage.getRecords().forEach(item->{
                LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userRoleLambdaQueryWrapper.eq(UserRole::getUserId,item.getId());
                userRoleLambdaQueryWrapper.eq(UserRole::getIsDeleted,IsDeletedEnum.NO);
                // 获取用户角色关联表信息
                List<UserRole> userRoleList = userRoleService.list(userRoleLambdaQueryWrapper);
                List<Integer> rileIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());
                if (!rileIds.isEmpty()) {
                    LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    roleLambdaQueryWrapper.in(Role::getId,rileIds);
                    roleLambdaQueryWrapper.eq(Role::getIsDeleted,IsDeletedEnum.NO);
                    // 获取用户角色信息
                    List<Role> roles = roleService.list(roleLambdaQueryWrapper);
                    item.setRoles(roles);
                } else {
                    item.setRoles(new ArrayList<>());
                }
            });
        }
        return returnPage;
    }

    @Override
    public String deleteUser(UserDeleteDto userDeleteDto) {
        RLock lock = redissonClient.getLock("lock:deleteUser");
        lock.lock();
        try {
            LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userLambdaUpdateWrapper.eq(User::getId,userDeleteDto.getId())
                    .set(User::getIsDeleted,1);
            userMapper.update(null,userLambdaUpdateWrapper);
        } finally {
            lock.unlock();
            return "delete success";
        }
    }

    @Override
    public org.orient.otc.user.vo.UserVo getUserDetails(Integer id) {
        org.orient.otc.user.vo.UserVo userVo = new org.orient.otc.user.vo.UserVo();
        // 获取用户信息
        User user = userMapper.selectById(id);
        BeanUtils.copyProperties(user,userVo);
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(UserRole::getUserId,id);
        userRoleLambdaQueryWrapper.eq(UserRole::getIsDeleted,IsDeletedEnum.NO);
        // 获取用户角色关联表信息
        List<UserRole> userRoleList = userRoleService.list(userRoleLambdaQueryWrapper);
        List<Integer> rileIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (!rileIds.isEmpty()) {
            LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roleLambdaQueryWrapper.in(Role::getId,rileIds);
            // 获取用户角色信息
            List<Role> roles = roleService.list(roleLambdaQueryWrapper);
            userVo.setRoles(roles);
        } else {
            userVo.setRoles(new ArrayList<>());
        }
        return userVo;
    }

    @Override
    public Boolean checkPassword(UserUpdatePasswordDto dto) {
        User user = userMapper.selectById(dto.getId());
        String inputPassword = dto.getPassword();
        String dbPassword = user.getPassword();
        String dbSlat = user.getSalt();
        return checkPassword(inputPassword,dbSlat,dbPassword);
    }

    @Override
    public String modifyPassword(UserUpdatePasswordDto dto) {
        Md5PasswordDto md5PasswordDto= getMd5Password(dto.getPassword());
        User user = new User();
        user.setId(dto.getId());
        user.setPassword(md5PasswordDto.getMd5Password());
        user.setSalt(md5PasswordDto.getSalt());
        /*LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId,dto.getId())
                .set(User::getPassword,md5PasswordDto.getMd5Password())
                .set(User::getSalt,md5PasswordDto.getSalt());
        int count = userMapper.update(null,userLambdaUpdateWrapper);*/
        int count = userMapper.updateById(user);
        if (count != 1) {
            return "修改密码失败";
        }
        return "修改密码成功";
    }

    @Override
    public List<User> traderList() {
        List<User> returnList = new ArrayList<>();
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(UserRole::getRoleId,2); // 交易员角色
        userRoleLambdaQueryWrapper.eq(UserRole::getIsDeleted,IsDeletedEnum.NO);
        List<UserRole> userRoleList = userRoleService.list(userRoleLambdaQueryWrapper);
        if(userRoleList != null && !userRoleList.isEmpty()) {
            Set<Integer> userIds = userRoleList.stream().map(UserRole::getUserId).collect(Collectors.toSet());
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getIsDeleted,IsDeletedEnum.NO);
            userLambdaQueryWrapper.in(User::getId,userIds);
            userLambdaQueryWrapper.eq(User::getStatus,1);
            userLambdaQueryWrapper.orderByDesc(User::getCreateTime);
            returnList = userMapper.selectList(userLambdaQueryWrapper);
        }
        return returnList;
    }

    @Override
    public List<User> getAllList() {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getIsDeleted,IsDeletedEnum.NO);
        userLambdaQueryWrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectList(userLambdaQueryWrapper);
    }

    /**
     * 生成密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public Md5PasswordDto getMd5Password(String password) {
        String salt = "$1$" + Base64.encodeBase64String(RandomStringUtils.random(5).getBytes());
        String md5Password = Md5Crypt.md5Crypt(password.getBytes(), salt);
        Md5PasswordDto md5PasswordDto = new Md5PasswordDto();
        md5PasswordDto.setMd5Password(md5Password);
        md5PasswordDto.setSalt(salt);
        return md5PasswordDto;
    }
    /**
     * 校验输入的密码是否正确
     * 用输入的密码和加密盐,在计算加密之后的密码,和数据库中的比较是否一致
     * @param inputPassword  页面输入的密码
     * @param dbSalt         数据库中存储的加密盐
     * @param dbPassword    数据库中存储的密码
     * @return 密码是否正确
     */
    public boolean checkPassword(String inputPassword,String dbSalt,String dbPassword) {
        String md5Password = Md5Crypt.md5Crypt(inputPassword.getBytes(), dbSalt);
        return md5Password.equals(dbPassword);
    }
}
