package org.orient.otc.yl.vo;

import lombok.Data;

/**
 * @author dzrh
 */
@Data
public class YlAccessToken  {
    /**
     * 凭证类型,目前仅支持'Bearer'令牌
     */
    String tokenType;
    /**
     * 获取到的凭证
     */
    String accessToken;
    /**
     * 凭证有效时间，单位：秒
     */
    Integer expiresIn;
}
