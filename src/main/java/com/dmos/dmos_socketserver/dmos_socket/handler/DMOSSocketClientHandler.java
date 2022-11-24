package com.dmos.dmos_socketserver.dmos_socket.handler;

import com.dmos.dmos_client.DMOSClientContext;
import com.dmos.dmos_common.data.ClientReportDTO;
import com.dmos.dmos_common.data.ConfigDTO;
import com.dmos.dmos_common.data.ServerReportDTO;
import com.dmos.dmos_common.message.Message;
import com.dmos.dmos_common.message.MessageType;
import com.dmos.dmos_server.DMOSServerContext;
import com.dmos.dmos_socketserver.bean.SpringUtil;
import com.dmos.dmos_socketserver.dmos_socket.util.SocketServerHttpUtil;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class DMOSSocketClientHandler extends ChannelInboundHandlerAdapter {

    private final DMOSServerContext serverContext = SpringUtil.getBean(DMOSServerContext.class);
    private final DMOSClientContext clientContext = SpringUtil.getBean(DMOSClientContext.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("父节点通道已建立: {}", ctx.channel().id().asLongText());
        // send verify token
        clientContext.channel(ctx.channel());
        Message verifyMessage = new Message();
        verifyMessage.setType(MessageType.IDENTIFY);
        verifyMessage.setData(clientContext.getToken());
        clientContext.send(verifyMessage);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("从父节点通道 {} 中收到信息: {}", ctx.channel().id().asLongText(), msg.toString());
        Gson gson = new Gson();
        Message message = gson.fromJson(msg.toString(), Message.class);
        if(message.getType() == MessageType.CONFIG){
            ConfigDTO configDTO = gson.fromJson(message.getData(), ConfigDTO.class);
            int client = configDTO.getId();
            serverContext.sendTo(client, message);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("父节点通道 {} 出现异常", ctx.channel().id().asLongText());
        cause.printStackTrace();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.warn("父节点通道 {} 关闭", ctx.channel().id().asLongText());
        clientContext.channel(null);
    }

}
