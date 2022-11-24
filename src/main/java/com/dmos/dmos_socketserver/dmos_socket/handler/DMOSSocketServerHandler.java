package com.dmos.dmos_socketserver.dmos_socket.handler;

import com.dmos.dmos_client.DMOSClientContext;
import com.dmos.dmos_common.data.ClientReportDTO;
import com.dmos.dmos_common.data.ServerReportDTO;
import com.dmos.dmos_common.message.Message;
import com.dmos.dmos_common.message.MessageType;
import com.dmos.dmos_common.util.ChannelUtil;
import com.dmos.dmos_common.util.ParseUtil;
import com.dmos.dmos_socketserver.dmos_socket.bean.SpringUtil;
import com.dmos.dmos_server.DMOSServerContext;
import com.dmos.dmos_socketserver.dmos_socket.util.SocketServerHttpUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

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
        // Todo：奇怪的问题，可能是线程冲突导致的消息叠加
        String[] msgs = msg.toString().split("\r");
        for(String s: msgs){
            log.info("从子节点通道 {} 中收到信息: {}", ctx.channel().id().asLongText(), s);
            Message message = ParseUtil.decode(s, Message.class);
            if(message.getType() == MessageType.IDENTIFY){
                String verifyToken = message.getData();
                int id = httpUtil.verifyChannel(verifyToken);
                if(id == -1){
                    serverContext.deleteChannel(ctx.channel().id().asLongText());
                    ctx.channel().close().sync();
                }
                else{
                    serverContext.verifyChannel(ctx.channel().id().asLongText(), id);

                    reportChild();
                }
            }
            else if(message.getType() == MessageType.HEARTBEAT){
                serverContext.heartbeat(ctx.channel().id().asLongText());
                ChannelUtil.heartbeat(ctx.channel());
            }
            else if(message.getType() == MessageType.CLIENT_REPORT){
                if(!serverContext.established(ctx.channel().id().asLongText()))
                    return;
                ClientReportDTO reportDTO = ParseUtil.decode(message.getData(), ClientReportDTO.class);
                reportDTO.setId(serverContext.getChannelHandle(ctx.channel().id().asLongText()).getId());
                httpUtil.reportState(reportDTO);
            }
            else if(message.getType() == MessageType.SERVER_REPORT){
                if(!serverContext.established(ctx.channel().id().asLongText()))
                    return;
                ServerReportDTO reportDTO = ParseUtil.decode(message.getData(), ServerReportDTO.class);
                serverContext.report(reportDTO);
                clientContext.send(message);
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("子节点通道 {} 出现异常", ctx.channel().id().asLongText());
//        channelCache.deleteChannel(ctx.channel().id().asLongText());
//        cause.printStackTrace();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.warn("子节点通道 {} 关闭", ctx.channel().id().asLongText());
        serverContext.deleteChannel(ctx.channel().id().asLongText());

        reportChild();
    }
    public void reportChild(){
//        log.info("正在同步子节点信息");
        ServerReportDTO reportDTO = new ServerReportDTO();
        reportDTO.setChild(serverContext.getClients().stream().collect(Collectors.toList()));
        reportDTO.setId(clientContext.getId());
        reportDTO.setTimestamp(System.currentTimeMillis() / 1000L);
        serverContext.report(reportDTO);
        clientContext.send(reportDTO);
    }
}
