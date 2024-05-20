package org.orient.otc.openapi.service;

import org.orient.otc.api.openapi.vo.TransDetailVo;
import org.orient.otc.openapi.dto.StatusConvert;
import org.orient.otc.openapi.vo.Token;

import java.util.List;
import java.util.Map;

/**
 * @author dzrh
 */
public interface TransDetailService {
    String get(String url, Map<String, String> params);

    String post(String url, Object postData);

    Token getAccessToken(String userInfo);

    String getAccessToken();

    Token getAppTokenERP();

    String getAppToken();

    List<TransDetailVo> getTransDetailList();

    Boolean statusConvertY(StatusConvert statusConvert);

    Boolean statusConvertN(StatusConvert statusConvert);
}
