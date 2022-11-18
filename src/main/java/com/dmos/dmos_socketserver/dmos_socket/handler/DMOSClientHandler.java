package com.dmos.dmos_socketserver.dmos_socket.handler;

import com.dmos.dmos_common.util.HttpUtil;
import com.dmos.dmos_socketserver.dmos_client.DMOSClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class DMOSClientHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private DMOSClient dmosClient;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端通道已建立: {}", ctx.channel().id().asLongText());
        // send verify token
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端通道 {} 出现异常", ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.warn("客户端通道 {} 关闭", ctx.channel().id().asLongText());

    }

    @Scheduled(fixedRate = 60000)
    public void checkHeartbeat() throws InterruptedException {

    }
}
