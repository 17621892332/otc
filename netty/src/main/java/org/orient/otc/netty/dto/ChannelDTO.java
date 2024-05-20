package org.orient.otc.netty.dto;

import io.netty.channel.Channel;
import lombok.Data;
import org.orient.otc.netty.enums.ChannelType;

/**
 * 通道信息
 */
@Data
public class ChannelDTO {
    private Channel channel;

    private ChannelType channelType;
}
