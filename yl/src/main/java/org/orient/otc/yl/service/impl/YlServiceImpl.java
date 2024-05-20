package org.orient.otc.yl.service.impl;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.orient.otc.api.client.vo.ClientInfoDetailAllVo;
import org.orient.otc.api.client.vo.ClientInfoDetailVo;
import org.orient.otc.common.cache.adapter.RedisAdapter;
import org.orient.otc.common.rocketmq.config.RocketMqConstant;
import org.orient.otc.common.security.adapter.AuthAdapter;
import org.orient.otc.common.security.dto.SystemLogInfo;
import org.orient.otc.common.security.exception.BussinessException;
import org.orient.otc.yl.config.YlProperties;
import org.orient.otc.yl.dto.*;
import org.orient.otc.yl.enums.YlApiUrl;
import org.orient.otc.yl.service.YlService;
import org.orient.otc.yl.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dzrh
 */
@Service
@Slf4j
public class YlServiceImpl implements YlService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private YlProperties ylProperties;

    @Value("${logIsSendRocketMQ:true}")
    private Boolean logIsSendRocketMQ;
    @Resource
    RocketMQTemplate rocketMQTemplate;


    @Override
    public String post(String url, Object postData) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AuthAdapter.AuthToken, getAccessToken());
        String body = JSONObject.toJSONString(postData);
        String res = HttpUtil.createPost(ylProperties.getUrl() + url).addHeaders(headers).body(body).execute().body();
        log.info("请求镒链接口:{}\r\n参数:{}\r\n headers:{}\r\n 响应内容：{}", url, body, headers, res);
        return res;
    }

    @Override
    public YlAccessToken getAccessToken(String userInfo) {
        String url = YlApiUrl.OAUTH_TOKEN_URL.getPath();
        Map<String, String> headers = new HashMap<>();
        headers.put(AuthAdapter.AuthToken, userInfo);
        HttpRequest request = HttpUtil.createPost(ylProperties.getUrl() + url).addHeaders(headers);
        String res = request.body("{}").execute().body();
        log.info("获取镒链Token接口:{}\r\n headers:{}\r\n 响应内容：{}", url, headers, res);
        return JSONObject.parseObject(res, YlAccessToken.class);
    }

    @Override
    public String getAccessToken() {
        String accessTokenString = stringRedisTemplate.opsForValue().get(RedisAdapter.YL_ACCESS_TOKEN_DIRECTORY + ylProperties.getAccount());
        if (StringUtils.isNotBlank(accessTokenString)) {
            return accessTokenString;
        }
        YlAccessToken token = this.getAccessToken("Basic  " + Base64Encoder.encode(ylProperties.getAccount() + ":" + ylProperties.getPassword()));
        if (token != null && token.getExpiresIn() != 0) {
            stringRedisTemplate.opsForValue().set(RedisAdapter.YL_ACCESS_TOKEN_DIRECTORY + ylProperties.getAccount(), token.getTokenType() + " " + token.getAccessToken(), token.getExpiresIn(), TimeUnit.SECONDS);
            return token.getTokenType() + " " + token.getAccessToken();
        }
        return null;
    }

    @Override
    public String postNeedLog(String url, Object postData) {
        Map<String, String> headers = new HashMap<>();
        long startTime = System.currentTimeMillis();
        String ip;
        try {
            InetAddress address = InetAddress.getLocalHost();
            ip=address.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        headers.put(AuthAdapter.AuthToken, getAccessToken());
        String body = JSONObject.toJSONString(postData);
        String res = HttpUtil.createPost(ylProperties.getUrl() + url).addHeaders(headers).body(body).execute().body();

        SystemLogInfo systemLogInfo = SystemLogInfo.builder()
                .path(url)
                .requestInfo(JSONObject.toJSONString(postData))
                .serverName("ylApi")
                .ip(ip)
                .spendTimes(System.currentTimeMillis()-startTime)
                .requestTime(LocalDateTime.now())
                .responseData(res)
                .build();
        if (logIsSendRocketMQ) {
            rocketMQTemplate.syncSend(RocketMqConstant.SYSTEM_LOG+":"+RocketMqConstant.SYSTEM_LOG,systemLogInfo);
        }
        log.info("请求镒链接口:{}\r\n参数:{}\r\n headers:{}\r\n 响应内容：{}", url, body, headers, res);
        return res;
    }

    @Override
    public List<ClientInfoVo> getClientInfoList() {
        String res = this.post(YlApiUrl.CLIENT_INFO_LIST.getPath(), null);
        BaseResult<List<ClientInfoVo>> listBaseResultlistBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<List<ClientInfoVo>>>() {
        });
        return listBaseResultlistBaseResult.getData();
    }

    @Override
    public ClientInfoDetailVo getClientInfo(ClientInfoDetailDto dto) {
        String res = this.post(YlApiUrl.GET_CLIENT_INFO.getPath(), dto);
        BaseResult<ClientInfoDetailVo> clientInfoVoBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<ClientInfoDetailVo>>() {
        });
        return clientInfoVoBaseResult.getData();
    }

    @Override
    public ClientInfoDetailAllVo getClientInfo2(ClientInfoDetailDto dto) {
        String res = this.post(YlApiUrl.GET_CLIENT_INFO2.getPath(), dto);
        BaseResult<ClientInfoDetailAllVo> clientInfoVoBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<ClientInfoDetailAllVo>>() {
        });
        return clientInfoVoBaseResult.getData();
    }

    @Override
    public PageInfo<ClientPositionVo> getClientPositionListV1(ClientPositionDto dto) {
        String res = this.post(YlApiUrl.CLIENT_POSITION_LIST_V1.getPath(), dto);
        BaseResult<PageInfo<ClientPositionVo>> listBaseResultlistBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<PageInfo<ClientPositionVo>>>() {
        });
        return listBaseResultlistBaseResult.getData();
    }


    @Override
    public PageInfo<ClientPositionVo> getClientPositionListV3(ClientPositionDto dto) {
        String res = this.post(YlApiUrl.CLIENT_POSITION_LIST_V3.getPath(), dto);
        BaseResult<PageInfo<ClientPositionVo>> listBaseResultlistBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<PageInfo<ClientPositionVo>>>() {
        });
        return listBaseResultlistBaseResult.getData();
    }

    @Override
    public PageInfo<ClientPositionVo> getClientPositionListV2(ClientPositionDto dto) {
        String res = this.post(YlApiUrl.CLIENT_POSITION_LIST_V2.getPath(), dto);
        BaseResult<PageInfo<ClientPositionVo>> listBaseResultlistBaseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<PageInfo<ClientPositionVo>>>() {
        });
        return listBaseResultlistBaseResult.getData();
    }


    @Override
    public List<UnderlyingVo> getUnderlyingList() {
        String res = this.post(YlApiUrl.GET_UNDERLYING_LIST.getPath(), null);
        return JSONObject.parseArray(res, UnderlyingVo.class);
    }


    @Override
    public List<UnderlyingVolVo> getUnderlyingVolLis(UnderlyingVolSurfaceDto dto) {
        String res = this.post(YlApiUrl.GET_UNDERLYING_VOL_SURFACE.getPath(), dto);
        BaseResult<List<UnderlyingVolVo>> baseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<List<UnderlyingVolVo>>>() {
        });
        return baseResult.getData();
    }

    @Override
    public void saveVolatility(SaveVolatilityDto saveVolatilityDto) {
        this.postNeedLog(YlApiUrl.SAVE_VOLATILITY.getPath(), saveVolatilityDto);
    }

    @Override
    public List<EodPricesVo> getEodPricesList(EodPricesDto eodPricesDto) {
        String res = this.post(YlApiUrl.GET_UNDERLYING_EOD_PRICES.getPath(), eodPricesDto);
        BaseResult<List<EodPricesVo>> baseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<List<EodPricesVo>>>() {
        });
        return baseResult.getData();
    }

    @Override
    public ClientCashInoCashOutVO addClientCashInCashOut(ClientCashInCashOutDTO clientCashInCashOutDTO) {
        String res = this.postNeedLog(YlApiUrl.CLIENT_CASH_IN_CASH_OUT.getPath(), clientCashInCashOutDTO);
        BaseResult<ClientCashInoCashOutVO> baseResult = JSONObject.parseObject(res, new TypeReference<BaseResult<ClientCashInoCashOutVO>>() {
        });
        BussinessException.E_900102.assertTrue(baseResult.getErrcode()==0,"同步资金记录异常");
        return baseResult.getData();
    }

    @Override
    public void clientCashInCashOutConfirmed(Integer ylId) {
        this.postNeedLog(YlApiUrl.CLIENT_CASH_IN_CASH_OUT_CONFIRM.getPath()+"?id="+ylId, new JSONObject());
    }

    @Override
    public void clientCashInCashOutRefuse(Integer ylId) {
        this.postNeedLog(YlApiUrl.CLIENT_CASH_IN_CASH_OUT_REJECT.getPath()+"?id="+ylId, new JSONObject());
    }

    @Override
    public BaseResult<String>  updateCustomTradeRisk(UpdateCustomTradeRiskDTO customTradeRiskDTO) {
        String res = this.postNeedLog(YlApiUrl.UPDATE_CUSTOM_TRADE_RISK.getPath(), customTradeRiskDTO);
        return  JSONObject.parseObject(res, new TypeReference<BaseResult<String>>() {
        });
    }

    @Override
    public BaseResult<String>  updateTradePositionMargin(UpdateTradePositionMarginDTO marginDTO) {
        String res = this.postNeedLog(YlApiUrl.UPDATE_TRADE_POSITION_MARGIN.getPath(), marginDTO);
        return  JSONObject.parseObject(res, new TypeReference<BaseResult<String>>() {
        });
    }
}
