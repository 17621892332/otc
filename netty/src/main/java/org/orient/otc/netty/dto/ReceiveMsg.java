package org.orient.otc.netty.dto;

import io.netty.channel.Channel;
import lombok.Data;
import org.orient.otc.netty.enums.ChannelType;

/**
 * 与客户端通讯统一对象
 * @author dzrh
 * @param <T> 消息内容
 */
@Data
public class ReceiveMsg<T> {
    /**
     * 消息类型
     */
    private ChannelType channelType;

    private Channel channel;
    /**
     * 消息内容
     */
    private  T  msgInfo;
}
