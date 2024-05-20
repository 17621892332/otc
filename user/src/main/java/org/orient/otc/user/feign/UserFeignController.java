package org.orient.otc.user.feign;

import org.orient.otc.api.user.dto.UserDto;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.user.entity.User;
import org.orient.otc.user.service.AssetunitService;
import org.orient.otc.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserFeignController implements UserClient {
    @Autowired
    UserService userService;

    @Autowired
    AssetunitService assetunitService;
    @Override
    public List<UserVo> getUserList() {
        return userService.getList();
    }

    @Override
    public int insert(UserDto userDto) {
        return userService.insertDto(userDto, User.class);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public UserVo getUserById(Integer id) {
        return userService.getVoById(id, UserVo.class);
    }

    /**
     * @param idSet
     * @return
     */
    @Override
    public Map<Integer, String> getUserMapByIds(Set<Integer> idSet) {
        List<User> list = userService.queryByIds(idSet);
        return list.stream().collect(Collectors.toMap(User::getId,User::getName));
    }
}
