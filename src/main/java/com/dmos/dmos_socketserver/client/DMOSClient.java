package com.dmos.dmos_socketserver.client;

import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.util.Port;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DMOSClient {
    private final DMOSConfig dmosConfig;

    public DMOSClient(DMOSConfig dmosConfig) {
        this.dmosConfig = dmosConfig;

//        EventLoopGroup group = new NioEventLoopGroup();
//        Bootstrap bootstrap = new Bootstrap()
//                .group(group)
//                //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
//                .option(ChannelOption.TCP_NODELAY, true)
//                .channel(NioSocketChannel.class)
//                .handler(new DMOSClientChannelInitializer());

//        try {
//            ChannelFuture future = bootstrap.connect(dmosConfig.getSocketIP(), Port.REGISTER_CHANNEL_PORT).sync();
//            log.info("客户端成功....");
//            // 设置attr
//            future.channel().attr(AttributeKey.valueOf("key")).set("sssss");
//            //发送消息
//            //future.channel().writeAndFlush(sendMsg);
//            // 等待连接被关闭
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            log.error("无法连接父服务器");
//        }finally {
//            group.shutdownGracefully();
//        }
    }
}
