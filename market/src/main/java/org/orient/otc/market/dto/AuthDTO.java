package org.orient.otc.market.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class AuthDTO {

    @JSONField(name = "user_name")
    private String userName;

    private String password;
}
