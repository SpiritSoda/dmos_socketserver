package com.dmos.dmos_socketserver.server;

import com.dmos.dmos_common.data.ClientConfigDTO;
import com.dmos.dmos_common.data.ReportDTO;
import com.dmos.dmos_common.message.Message;
import com.dmos.dmos_common.message.MessageType;
import com.dmos.dmos_socketserver.channel.ChannelCache;
import com.dmos.dmos_socketserver.util.SocketServerHttpUtil;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DMOSServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private SocketServerHttpUtil httpUtil;
    @Autowired
    private ChannelCache channelCache;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("通道已建立: {}", ctx.channel().id().asLongText());
        channelCache.saveChannel(ctx.channel());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("从通道 {} 中收到信息: {}", ctx.channel().id().asLongText(), msg.toString());
        if(!channelCache.established(ctx.channel().id().asLongText()))
            return;
        Gson gson = new Gson();
        Message message = gson.fromJson(msg.toString(), Message.class);
        if(message.getType() == MessageType.IDENTIFY){
            String verifyToken = message.getData();
            int id = httpUtil.verifyChannel(verifyToken);
            if(id == -1){
                channelCache.deleteChannel(ctx.channel().id().asLongText());
            }
            else{
                channelCache.verifyChannel(ctx.channel().id().asLongText(), id);
            }
        }
        else if(message.getType() == MessageType.HEARTBEAT){
            channelCache.heartbeat(ctx.channel().id().asLongText());
        }
        else if(message.getType() == MessageType.CONFIG){
            ClientConfigDTO configDTO = gson.fromJson(message.getData(), ClientConfigDTO.class);
            int client = configDTO.getId();
            Channel channel = channelCache.getChannel(client);
            channel.writeAndFlush(message);
        }
        else if(message.getType() == MessageType.REPORT){
            ReportDTO reportDTO = gson.fromJson(message.getData(), ReportDTO.class);
            // 或许要与注册服务器同步？
            List<Integer> childs = reportDTO.getChilds();
            reportDTO.setChilds(null);
            httpUtil.reportState(reportDTO);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("通道 {} 出现异常", ctx.channel().id().asLongText());
        channelCache.deleteChannel(ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.warn("通道 {} 关闭", ctx.channel().id().asLongText());
        channelCache.deleteChannel(ctx.channel().id().asLongText());
    }

    @Scheduled(fixedRate = 60000)
    public void checkHeartbeat() throws InterruptedException {
        log.info("正在检查心跳");
        channelCache.disconnectTimeout();
        channelCache.resetHeartbeat();
    }
}
