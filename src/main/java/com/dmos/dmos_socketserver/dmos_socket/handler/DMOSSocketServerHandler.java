package com.dmos.dmos_socketserver.dmos_socket.handler;

import com.dmos.dmos_common.data.ConfigDTO;
import com.dmos.dmos_common.data.ReportDTO;
import com.dmos.dmos_common.message.Message;
import com.dmos.dmos_common.message.MessageType;
import com.dmos.dmos_socketserver.bean.SpringUtil;
import com.dmos.dmos_server.DMOSServerContext;
import com.dmos.dmos_socketserver.dmos_socket.util.SocketServerHttpUtil;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class DMOSSocketServerHandler extends ChannelInboundHandlerAdapter {
    private SocketServerHttpUtil httpUtil = SpringUtil.getBean(SocketServerHttpUtil.class);
    private final DMOSServerContext DMOSServerContext = SpringUtil.getBean(DMOSServerContext.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("通道已建立: {}", ctx.channel().id().asLongText());
        DMOSServerContext.saveChannel(ctx.channel());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("从通道 {} 中收到信息: {}", ctx.channel().id().asLongText(), msg.toString());
        if(!DMOSServerContext.established(ctx.channel().id().asLongText()))
            return;
        Gson gson = new Gson();
        Message message = gson.fromJson(msg.toString(), Message.class);
        if(message.getType() == MessageType.IDENTIFY){
            String verifyToken = message.getData();
            int id = httpUtil.verifyChannel(verifyToken);
            if(id == -1){
                DMOSServerContext.deleteChannel(ctx.channel().id().asLongText());
            }
            else{
                DMOSServerContext.verifyChannel(ctx.channel().id().asLongText(), id);
            }
        }
        else if(message.getType() == MessageType.HEARTBEAT){
            DMOSServerContext.heartbeat(ctx.channel().id().asLongText());
        }
        else if(message.getType() == MessageType.CONFIG){
            ConfigDTO configDTO = gson.fromJson(message.getData(), ConfigDTO.class);
            int client = configDTO.getId();
            DMOSServerContext.sendTo(client, configDTO);
        }
        else if(message.getType() == MessageType.REPORT){
            ReportDTO reportDTO = gson.fromJson(message.getData(), ReportDTO.class);
            reportDTO.setChilds(null);
            httpUtil.reportState(reportDTO);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("通道 {} 出现异常", ctx.channel().id().asLongText());
//        channelCache.deleteChannel(ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.warn("通道 {} 关闭", ctx.channel().id().asLongText());
        DMOSServerContext.deleteChannel(ctx.channel().id().asLongText());
    }

    @Scheduled(fixedRate = 60000)
    public void checkHeartbeat() throws InterruptedException {
        log.info("正在检查心跳");
        DMOSServerContext.disconnectTimeout();
        DMOSServerContext.resetHeartbeat();
    }
}
