package org.orient.otc.yl.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.lang.StringUtils;
import org.orient.otc.api.user.feign.AssetUnitClient;
import org.orient.otc.api.user.feign.UserClient;
import org.orient.otc.api.user.vo.AssetunitVo;
import org.orient.otc.api.user.vo.UserVo;
import org.orient.otc.yl.dto.SyncMarketCloseDataDto;
import org.orient.otc.yl.service.SyncServe;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤： 1、任务开发：在Spring Bean实例中，开发Job方法； 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法",
 * destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class YlXxlJob {

    @Resource
    private SyncServe syncServe;

    @Resource
    private AssetUnitClient assetUnitClient;
    @Resource
    private UserClient userClient;

    /**
     * 1、同步客户信息
     */
    @XxlJob("syncClient")
    public void syncClient() {
        if (syncServe.syncClient()) {
            XxlJobHelper.handleSuccess();
        } else {
            XxlJobHelper.handleFail();
        }
    }

    /**
     * 从镒链拉取标的合约信息
     */
    @XxlJob("syncUnderlying")
    public void syncUnderlying() {
        if (syncServe.syncUnderlying()) {
            XxlJobHelper.handleSuccess();
        } else {
            XxlJobHelper.handleFail();
        }
    }

    @XxlJob("syncMarketCloseData")
    public void syncMarketCloseData() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isNotBlank(jobParam)) {
            SyncMarketCloseDataDto   dto = JSONObject.parseObject(jobParam, SyncMarketCloseDataDto.class);
            syncServe.syncMarketCloseData(dto.getStartDate(), dto.getIsOnlyToday());
        }else {
            XxlJobHelper.handleFail("消息不能为空");
        }

    }

//    /**
//     * 同步镒链波动率
//     * @return
//     */
//    @XxlJob("syncUnderlyingVol")
//    public void syncUnderlyingVol() {
//        String jobParam = XxlJobHelper.getJobParam();
//        SyncUnderlyingVolDto dto = new SyncUnderlyingVolDto();
//        if (StringUtils.isNotBlank(jobParam)) {
//            dto = JSONObject.parseObject(jobParam, SyncUnderlyingVolDto.class);
//        }
//        String res = syncServe.syncUnderlyingVol(dto.getSystemDate());
//        XxlJobHelper.handleSuccess(res);
//    }

    /**
     * 从镒链获取客户持仓数据
     * @return
     */
    @XxlJob("syncClientPosition")
    public void syncClientPosition() {
        String res = syncServe.syncClientPosition();
        XxlJobHelper.handleSuccess(res);
    }

    /**
     * 根据交易编号手动同步交易记录
     */
    @XxlJob("syncTradeInfoByTradeCode")
    public void syncTradeInfoByTradeCode() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.isNotBlank(jobParam)) {
            List<String>  tradeCodeList = JSONObject.parseArray(jobParam, String.class);
            Map<String, Integer> assetMap = assetUnitClient.getAssetUnitList(new HashSet<>()).stream()
                    .collect(Collectors.toMap(AssetunitVo::getName, AssetunitVo::getId));
            Map<String, Integer> traderMap = userClient.getUserList().stream()
                    .collect(Collectors.toMap(UserVo::getName, UserVo::getId));
            for (String tradeCode : tradeCodeList) {
                syncServe.syncTradeInfoByTradeCode(assetMap, traderMap, tradeCode);
            }
        }


    }

    /**
     * 同步交易至镒链
     */
    @XxlJob("syncTradeToYl")
    public void syncTradeToYl() {
        String res = syncServe.syncTradeToYl();
        XxlJobHelper.handleSuccess(res);
    }

    /**
     * 同步平仓至镒链
     */
    @XxlJob("syncTradeCloseToYl")
    public void syncTradeCloseToYl() {
        String res = syncServe.syncTradeCloseToYl();
        XxlJobHelper.handleSuccess(res);
    }
}
