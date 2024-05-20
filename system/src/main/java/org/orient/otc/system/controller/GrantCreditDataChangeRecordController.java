package org.orient.otc.system.controller;

import io.swagger.annotations.Api;
import org.orient.otc.system.service.GrantCreditDataChangeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grantCreditDataChangeRecord")
@Api(tags = "授信数据变更记录")
public class GrantCreditDataChangeRecordController {
    @Autowired
    GrantCreditDataChangeRecordService grantCreditDataChangeRecordService;
}
