package com.dmos.dmos_socketserver.client;

import com.dmos.dmos_common.util.HttpUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DMOSClientHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private DMOSClient dmosClient;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // send verify token
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Scheduled(fixedRate = 60000)
    public void checkHeartbeat() throws InterruptedException {

    }
}
