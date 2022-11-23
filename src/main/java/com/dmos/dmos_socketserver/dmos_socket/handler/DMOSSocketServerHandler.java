package com.dmos.dmos_socketserver.dmos_socket.handler;

import com.dmos.dmos_client.DMOSClientContext;
import com.dmos.dmos_common.data.ConfigDTO;
import com.dmos.dmos_common.data.ClientReportDTO;
import com.dmos.dmos_common.data.ServerReportDTO;
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

import java.util.stream.Collectors;

@Slf4j
public class DMOSSocketServerHandler extends ChannelInboundHandlerAdapter {
    private final SocketServerHttpUtil httpUtil = SpringUtil.getBean(SocketServerHttpUtil.class);
    private final DMOSServerContext serverContext = SpringUtil.getBean(DMOSServerContext.class);
    private final DMOSClientContext clientContext = SpringUtil.getBean(DMOSClientContext.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("子节点通道已建立: {}", ctx.channel().id().asLongText());
        serverContext.saveChannel(ctx.channel());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("从子节点通道 {} 中收到信息: {}", ctx.channel().id().asLongText(), msg.toString());
        if(!serverContext.established(ctx.channel().id().asLongText()))
            return;
        Gson gson = new Gson();
        Message message = gson.fromJson(msg.toString(), Message.class);
        if(message.getType() == MessageType.IDENTIFY){
            String verifyToken = message.getData();
            int id = httpUtil.verifyChannel(verifyToken);
            if(id == -1){
                serverContext.deleteChannel(ctx.channel().id().asLongText());
            }
            else{
                serverContext.verifyChannel(ctx.channel().id().asLongText(), id);
            }
        }
        else if(message.getType() == MessageType.HEARTBEAT){
            serverContext.heartbeat(ctx.channel().id().asLongText());
        }
        else if(message.getType() == MessageType.CLIENT_REPORT){
            ClientReportDTO reportDTO = gson.fromJson(message.getData(), ClientReportDTO.class);
            reportDTO.setId(serverContext.getChannelHandle(ctx.channel().id().asLongText()).getId());
            httpUtil.reportState(reportDTO);
        }
        else if(message.getType() == MessageType.SERVER_REPORT){
            ServerReportDTO reportDTO = gson.fromJson(message.getData(), ServerReportDTO.class);
            serverContext.report(reportDTO);
            clientContext.send(message);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("子节点通道 {} 出现异常", ctx.channel().id().asLongText());
//        channelCache.deleteChannel(ctx.channel().id().asLongText());
        cause.printStackTrace();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.warn("子节点通道 {} 关闭", ctx.channel().id().asLongText());
        serverContext.deleteChannel(ctx.channel().id().asLongText());
    }

    @Scheduled(fixedRate = 60000)
    public void checkHeartbeat() throws InterruptedException {
        log.info("正在检查心跳");
        serverContext.disconnectTimeout();
        serverContext.resetHeartbeat();
    }
    @Scheduled(fixedRate = 10000)
    public void reportChild(){
        log.info("正在同步子节点信息");
        ServerReportDTO reportDTO = new ServerReportDTO();
        reportDTO.setChild(serverContext.getClients().stream().collect(Collectors.toList()));
        reportDTO.setId(clientContext.getId());
        clientContext.send(reportDTO);
    }
}
