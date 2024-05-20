package org.orient.otc.common.rocketmq.config;

/**
 * nameserver用;分割
 * 同步消息，如果两次
 * @author FrozenWatermelon
 * @date 2021/3/25
 */
public class RocketMqConstant {

    /**
     * 系统日志topic
     */
    public static final String SYSTEM_LOG = "systemLog";
    /**
     * 同步消息
     */
    public static final String SYNC_TOPIC ="syncTopic";

    /**
     * 交易新增
     */
    public static final  String SYNC_TOPIC_TRADE_MNG_ADD = "tradeMngAdd";

    /**
     * 交易修改
     */
    public static final  String SYNC_TOPIC_TRADE_MNG_UPDATE = "tradeMngUpdate";
    /**
     * 交易删除
     */

    public static final  String SYNC_TOPIC_TRADE_MNG_DEL = "tradeMngDel";

    /**
     * 交易平仓
     */

    public static final  String SYNC_TOPIC_TRADE_CLOSE_MNG_CLOSE = "tradeCloseMngClose";

    /**
     * 交易到期
     */
    public static final  String SYNC_TOPIC_TRADE_CLOSE_MNG_END = "tradeCloseMngEnd";

    /**
     * 波动率信息
     */
    public static final  String SYNC_TOPIC_VOL = "vol";



    /**
     * 资金记录新增
     */
    public static final  String CAPITAL_ADD = "capitalAdd";

    /**
     * 资金记录确认
     */
    public static final  String CAPITAL_CONFIRMED = "capitalConfirmed";

    /**
     * 资金记录新增及确认
     */
    public static final  String CAPITAL_ADD_CONFIRMED = "capitalAddConfirmed";
    /**
     * 资金记录确认
     */
    public static final  String CAPITAL_REFUSE = "capitalRefuse";
    /**
     * 波动率信息同步繁微
     */
    public static final  String VOL_TO_FINOVIEW = "volToFinoview";
}
