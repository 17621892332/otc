package org.orient.client.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orient.otc.client.ClientApplication;
import org.orient.otc.client.service.GrantCreditService;
import org.orient.otc.common.core.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class ServiceImplTest extends TestCase {
    @Autowired
    GrantCreditService grantCreditService;
    @Test
    public void importTest() throws Exception {
        File file = new File("C://Users//dzrh//Desktop//授信导入//测试授信导入.xlsx");
        MultipartFile multipartFile = FileUtil.file2MultipartFile(file);
        grantCreditService.importGrant(multipartFile);
    }
}
