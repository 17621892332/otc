package org.orient.otc.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.user.dto.*;
import org.orient.otc.user.service.PermissionService;
import org.orient.otc.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PermissionServiceImplTest extends TestCase {

    @Autowired
    PermissionService permissionService;

    @Test
    public void testDeletePermission() {
        permissionService.deletePermission(35);
    }

    @Test
    public void testTreePermission() {
        System.out.println("----权限树结构---"+JSON.toJSON(permissionService.treePermission()));
    }

    @Test
    public void testUpdatePermission() {
        PermissionSaveDto dto = new PermissionSaveDto();
        dto.setId(38);
        dto.setName("38name");
        permissionService.updatePermission(dto);
    }

}
