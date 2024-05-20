package org.orient.otc.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplTest extends TestCase {

    @Autowired
    UserService userService;


    @Test
    public void testRegister() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setName("程强_2");
        dto.setAccount("chengqiang_2");
        dto.setPassword("$1$64md74iV$ywa8i5ZtrOFN9YEUsZFdZ");
        dto.setJobNumber("24");
        dto.setSex(2);
        String result = userService.register(dto);
        System.out.println(result);
    }


    @Test
    public void testGetListBypage() throws ParseException {
        UserPageListDto dto = new UserPageListDto();
        dto.setPageNo(1);
        dto.setPageSize(10);
       dto.setName("");
//        dto.setJobNumber("1");
//        dto.setPhone("13100000000");
//        dto.setStatus(1);
        String dateStr = "2023-08-01 08:00:00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = format.parse(dateStr);
//        dto.setBirthday(date);
//        dto.setStatus(1);
//        dto.setEmail("111@qq.com");
        /*List<Integer> roleIds = new ArrayList<>();
        roleIds.add(1);
        dto.setRoleIdList(roleIds);*/
        System.out.println(JSONObject.toJSONString(userService.getListBypage(dto)));
    }

    @Test
    public void testDeleteUser() {
        UserDeleteDto dto = new UserDeleteDto();
        dto.setId(198);
        String result = userService.deleteUser(dto);
        System.out.println(result);
    }
    @Test
    public void testGetUserDetails() {
        org.orient.otc.user.vo.UserVo userVo = userService.getUserDetails(198);
        System.out.println(JSON.toJSON(userVo));
    }
    @Test
    public void testSaveUser() {
        UserSaveDto userSaveDto = new UserSaveDto();
        userSaveDto.setId(201);
        userSaveDto.setPassword("123456");
        userSaveDto.setAccount("chengqiang");
        userSaveDto.setJobNumber("1");
        userService.saveUser(userSaveDto);
    }
    @Test
    public void modifyPassword() {
        UserUpdatePasswordDto dto = new UserUpdatePasswordDto();
        dto.setPassword("1234567");
        dto.setId(201);
        System.out.println("----------"+userService.modifyPassword(dto));
    }

    @Test
    public void traderList() {
        System.out.println("----------"+userService.traderList());
    }

}
