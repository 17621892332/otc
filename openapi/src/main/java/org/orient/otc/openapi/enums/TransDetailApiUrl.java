package org.orient.otc.openapi.enums;

import lombok.Getter;

/**
 * @author dzrh
 */
@Getter
public enum TransDetailApiUrl {


    /**
     * 交易明细查询
     */
    TRANS_DETAIL_API_URL("/ierp/kapi/v2/jnbu/bei/bei_transdetail/getTransdetail"),
    /**
     * 场外状态确认
     */
    STATUS_CONVERT_Y("/ierp/kapi/v2/jnbu/bei/bei_transdetail/statusconvert_y"),
    /**
     * 场外状态反确认
     */
    STATUS_CONVERT_N("/ierp/kapi/v2/jnbu/bei/bei_transdetail/statusconvert_n"),
    /**
     * 访问令牌(app_token)
     */
    APP_TOKEN_URL("/ierp/api/getAppToken.do"),
    /**
     * 访问令牌(access_token)
     */
    OAUTH_TOKEN_URL("/ierp/api/login.do"),
    ;
    TransDetailApiUrl(String path) {
        this.path = path;
    }

    private final String path;

}
