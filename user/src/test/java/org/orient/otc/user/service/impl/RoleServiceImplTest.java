package org.orient.otc.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.user.dto.RoleDeletDto;
import org.orient.otc.user.dto.RoleDetailDto;
import org.orient.otc.user.dto.RoleSaveDto;
import org.orient.otc.user.dto.RolePageListDto;
import org.orient.otc.user.service.RoleService;
import org.orient.otc.user.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoleServiceImplTest extends TestCase {

    @Autowired
    RoleService roleService;
    @Test
    public void testGetRoleList() {
      List<RoleVo> list= roleService.getRoleList();
        System.out.println(JSONObject.toJSONString(list));
    }
    @Test
    public void testSaveRole() {
        RoleSaveDto roleSaveDto = new RoleSaveDto();
        roleSaveDto.setId(11);
        roleSaveDto.setRoleName("测试角色-1");
        roleSaveDto.setNotes("这是一个测试备注-1");
        roleSaveDto.setPermissionIds(Arrays.asList(1,2,3));
        roleService.saveRole(roleSaveDto);
    }
    @Test
    public void testGetListByPage() {
        RolePageListDto dto = new RolePageListDto();
        dto.setPageNo(1);
        dto.setPageSize(10);
        dto.setRoleName("");
        System.out.println(JSONObject.toJSONString(roleService.getListByPage(dto)));
    }

    @Test
    public void testDeleteRole() {
        RoleDeletDto dto = new RoleDeletDto();
        dto.setId(1);
        System.out.println(JSONObject.toJSONString(roleService.deleteRole(dto)));
    }
    @Test
    public void testGetDetail() {
        RoleDetailDto dto = new RoleDetailDto();
        dto.setId(11);
        System.out.println(JSONObject.toJSONString(roleService.getRoleDetail(dto)));
    }
}
