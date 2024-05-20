package org.orient.otc.openapi.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.openapi.vo.TransDetailVo;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.security.adapter.AuthAdapter;
import org.orient.otc.openapi.config.TransDetailProperties;
import org.orient.otc.openapi.dto.StatusConvert;
import org.orient.otc.openapi.enums.TransDetailApiUrl;
import org.orient.otc.openapi.service.TransDetailService;
import org.orient.otc.openapi.vo.AccessToken;
import org.orient.otc.openapi.vo.AppToken;
import org.orient.otc.openapi.vo.BaseResult;
import org.orient.otc.openapi.vo.Token;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author dzrh
 */
@Service
@Slf4j
public class TransDetailServiceImpl implements TransDetailService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TransDetailProperties transDetailProperties;
    @Override
    public String get(String url, Map<String, String> params) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AuthAdapter.AccessToken, getAccessToken());
        String res = HttpUtil.createGet(url).addHeaders(headers).execute().body();
        log.info("请求ERP接口:{}\r\n headers:{}\r\n 响应内容：{}", url, headers, res);
        return res;
    }
    @Override
    public String post(String url, Object postData) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AuthAdapter.AccessToken, getAccessToken());
        String body = JSONObject.toJSONString(postData);
        String res = HttpUtil.createPost(url).addHeaders(headers).body(body).execute().body();
        log.info("请求ERP接口:{}\r\n参数:{}\r\n headers:{}\r\n 响应内容：{}", url, body, headers, res);
        return res;
    }

    @Override
    public Token getAccessToken(String appToken) {
        String url = TransDetailApiUrl.OAUTH_TOKEN_URL.getPath();
        AccessToken accessToken = new AccessToken();
        accessToken.setUser(transDetailProperties.getUser());
        accessToken.setApptoken(appToken);
        accessToken.setTenantid(transDetailProperties.getTenantid());
        accessToken.setAccountId(transDetailProperties.getAccountId());
        accessToken.setUsertype(transDetailProperties.getUsertype());
        accessToken.setLanguage(transDetailProperties.getLanguage());
        String body = JSONObject.toJSONString(accessToken);
        HttpRequest request = HttpUtil.createPost(transDetailProperties.getUrl() + url).body(body);
        String res = request.execute().body();
        log.info("获取ERP AccessToken接口:{}\r\n 响应内容：{}", url, res);
        return JSONObject.parseObject(res, Token.class);
    }

    @Override
    public String getAccessToken() {
        String appToken = getAppToken();
        String accessTokenString = stringRedisTemplate.opsForValue().get(RedisAdapter.TD_ACCESS_TOKEN + transDetailProperties.getUser());
        if (StringUtils.isNotBlank(accessTokenString)) {
            return accessTokenString;
        }
        Token token = this.getAccessToken(appToken);
        if (token != null && LocalDateTime.now().isBefore(token.getData().getExpire_time())) {
            stringRedisTemplate.opsForValue().set(RedisAdapter.TD_ACCESS_TOKEN + transDetailProperties.getUser(), token.getData().getAccess_token(),1, TimeUnit.HOURS);
            return token.getData().getAccess_token();
        }
        return null;
    }
    @Override
    public Token getAppTokenERP() {
        String url = TransDetailApiUrl.APP_TOKEN_URL.getPath();
        AppToken appToken = new AppToken();
        appToken.setAppId(transDetailProperties.getAppId());
        appToken.setAppSecret(transDetailProperties.getAppSecret());
        appToken.setTenantid(transDetailProperties.getTenantid());
        appToken.setAccountId(transDetailProperties.getAccountId());
        String body = JSONObject.toJSONString(appToken);
        HttpRequest request = HttpUtil.createPost(transDetailProperties.getUrl() + url).body(body);
        String res = request.execute().body();
        log.info("获取ERP AppToken接口:{}\r\n 响应内容：{}", url, res);
        return JSONObject.parseObject(res, Token.class);
    }

    @Override
    public String getAppToken() {
        String accessTokenString = stringRedisTemplate.opsForValue().get(RedisAdapter.TD_APP_TOKEN + transDetailProperties.getAppId());
        if (StringUtils.isNotBlank(accessTokenString)) {
            return accessTokenString;
        }
        Token token = this.getAppTokenERP();
        if (token != null && LocalDateTime.now().isBefore(token.getData().getExpire_time())) {
            stringRedisTemplate.opsForValue().set(RedisAdapter.TD_APP_TOKEN + transDetailProperties.getAppId(), token.getData().getApp_token(),1, TimeUnit.HOURS);
            return token.getData().getApp_token();
        }
        return null;
    }
    @Override
    public List<TransDetailVo> getTransDetailList() {
        String baseUrl = transDetailProperties.getUrl();
        String apiUrl = TransDetailApiUrl.TRANS_DETAIL_API_URL.getPath();
        int pageSize = transDetailProperties.getPagesize();
        String url = String.format("%s%s?pageSize=%d", baseUrl, apiUrl, pageSize);
        try {
            List<TransDetailVo> allTransDetails = new ArrayList<>();
            int pageNo = 1;
            boolean hasNextPage = true;
            while (hasNextPage) {
                String pageUrl = String.format("%s&pageNo=%d", url, pageNo);
                String res = this.get(pageUrl, null);
                BaseResult<List<TransDetailVo>> listBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<List<TransDetailVo>>>(){});
                if (listBaseResult != null && listBaseResult.getData() != null) {
                    List<TransDetailVo> transDetails = listBaseResult.getData().getRows();
                    if (!transDetails.isEmpty()) {
                        allTransDetails.addAll(transDetails);
                        pageNo++;
                    } else {
                        hasNextPage = false;
                    }
                } else {
                    hasNextPage = false;
                }
            }
            return allTransDetails;
        } catch (Exception e) {
            log.error("获取資金列表出错:", e);
            throw new RuntimeException("获取資金列表出错", e);
        }
    }

    @Override
    public Boolean statusConvertY(StatusConvert statusConvert) {
        String apiUrl = TransDetailApiUrl.STATUS_CONVERT_Y.getPath();
        String url = transDetailProperties.getUrl() + apiUrl;
        try {
            String res = this.post(url, statusConvert);
            JSONObject jsonRes = JSONObject.parseObject(res);
            boolean status = jsonRes.getBoolean("status");
            if (status) {
                log.info("调用statusConvertY接口成功");
                return true;
            } else {
                log.error("调用statusConvertY接口失败，错误码：{}，错误信息：{}", jsonRes.getString("errorCode"), jsonRes.getString("message"));
                return false;
            }
        } catch (Exception e) {
            log.error("调用statusConvertY接口出错:", e);
            return false;
        }
    }

    @Override
    public Boolean statusConvertN(StatusConvert statusConvert) {
        String apiUrl = TransDetailApiUrl.STATUS_CONVERT_N.getPath();
        String url = transDetailProperties.getUrl() + apiUrl;
        try {
            String res = this.post(url, statusConvert);
            JSONObject jsonRes = JSONObject.parseObject(res);
            boolean status = jsonRes.getBoolean("status");
            if (status) {
                log.info("调用statusConvertN接口成功");
                return true;
            } else {
                log.error("调用statusConvertN接口失败，错误码：{}，错误信息：{}", jsonRes.getString("errorCode"), jsonRes.getString("message"));
                return false;
            }
        } catch (Exception e) {
            log.error("调用statusConvertN接口出错:", e);
            return false;
        }
    }
}
