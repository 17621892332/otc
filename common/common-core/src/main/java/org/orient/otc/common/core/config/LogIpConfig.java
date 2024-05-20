package org.orient.otc.common.core.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 日志logback.xml 获取当前服务器IP使用
 */
@Slf4j
public class LogIpConfig extends ClassicConverter {
    private static String webIP;
    static {
        try {
            webIP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取日志Ip异常", e);
            webIP = null;
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        return webIP;
    }
}
