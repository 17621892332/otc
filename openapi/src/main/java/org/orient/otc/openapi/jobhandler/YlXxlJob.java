package org.orient.otc.openapi.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.orient.otc.api.quote.feign.TransDetailClient;
import org.orient.otc.openapi.service.FinoviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Autowired
    FinoviewService finoviewService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    private TransDetailClient transDetailClient;

    /**
     * 1、同步客户信息
     */
    @XxlJob("sendAllVolToFinoview")
    public void sendAllVolToFinoview() {
        Integer count= finoviewService.sendAllVolToFinoview();
        if (count>0) {
            XxlJobHelper.handleSuccess("成功发送:"+count+"条波动率");
        } else {
            XxlJobHelper.handleFail();
        }
    }

    /**
     *
     */
    @XxlJob("getTransDetail")
    public void getTransDetail() {
        if (transDetailClient.getTransDetail()) {
            XxlJobHelper.handleSuccess();
        } else {
            XxlJobHelper.handleFail();
        }
    }
}
