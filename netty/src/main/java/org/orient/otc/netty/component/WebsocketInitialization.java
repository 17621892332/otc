package org.orient.otc.netty.component;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.Properties;


@Slf4j
@Component
public class WebsocketInitialization {
    @Resource
    private WebsocketChannelInitializer websocketChannelInitializer;

    @Value("${websocket.port}")
    private Integer port;
//
//    @Value("${spring.profiles.active}")
//    private String env;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    public void init() {

            //bossGroup连接线程组，主要负责接受客户端连接，一般一个线程足矣
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            //workerGroup工作线程组，主要负责网络IO读写
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                //注册到Nacos里
                registerNamingService(port);
                //启动辅助类
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                //bootstrap绑定两个线程组
                serverBootstrap.group(bossGroup, workerGroup);
                //设置通道为NioChannel
                serverBootstrap.channel(NioServerSocketChannel.class);
                //可以对入站\出站事件进行日志记录，从而方便我们进行问题排查。
                serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
                //设置自定义的通道初始化器，用于入站操作
                serverBootstrap.childHandler(websocketChannelInitializer);
                //启动服务器,本质是Java程序发起系统调用，然后内核底层起了一个处于监听状态的服务，生成一个文件描述符FD
                ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
                //异步
                channelFuture.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage());
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
    }
    /**
     * 注册到 nacos 服务中
     * @param nettyPort netty服务端口
     */
    private void registerNamingService(Integer nettyPort) {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr",nacosDiscoveryProperties.getServerAddr());
            properties.setProperty("namespace",nacosDiscoveryProperties.getNamespace());
            NamingService namingService = NamingFactory.createNamingService(properties);
            InetAddress address = InetAddress.getLocalHost();
            namingService.registerInstance("nettyWebSocket", address.getHostAddress(), nettyPort);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
