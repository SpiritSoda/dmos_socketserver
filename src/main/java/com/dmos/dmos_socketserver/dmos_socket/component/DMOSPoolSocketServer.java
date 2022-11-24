package com.dmos.dmos_socketserver.dmos_socket.component;

import com.dmos.dmos_client.DMOSClientContext;
import com.dmos.dmos_common.data.ServerReportDTO;
import com.dmos.dmos_common.message.Message;
import com.dmos.dmos_common.message.MessageType;
import com.dmos.dmos_common.util.ParseUtil;
import com.dmos.dmos_common.util.Port;
import com.dmos.dmos_server.DMOSServer;
import com.dmos.dmos_server.DMOSServerContext;
import com.dmos.dmos_socketserver.dmos_socket.bean.SpringUtil;
import com.dmos.dmos_socketserver.dmos_socket.config.ThreadPoolTaskConfig;
import com.dmos.dmos_socketserver.dmos_socket.handler.DMOSSocketServerHandler;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DMOSPoolSocketServer {
    private final DMOSServerContext serverContext = SpringUtil.getBean(DMOSServerContext.class);
    private final DMOSClientContext clientContext = SpringUtil.getBean(DMOSClientContext.class);
    @Resource
    private ThreadPoolTaskConfig poolTaskExecutor;
    private static DMOSPoolSocketServer single = null;

    private DMOSServer server;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        single = this;
        single.poolTaskExecutor = this.poolTaskExecutor;
        // 初使化时将已静态化的testService实例化
    }

    public static DMOSPoolSocketServer getSingle(){
        return single;
    }

    public void  run(){
        poolTaskExecutor.taskExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //启动服务端
                System.out.println("DMOSPoolRegisterServer当前线程池：" + Thread.currentThread().getName());
                DMOSServer server = new DMOSServer(new InetSocketAddress("127.0.0.1", Port.SOCKET_CHANNEL_PORT), new DMOSSocketServerHandler());
                server.start();
            }
        });
    }

    public void stop(){
        if(server!= null && server.isRunning){
            server.stop();
        }
    }

    public boolean getIsRunning(){
        if(server == null){return false;}
        return  server.isRunning;
    }
    @Scheduled(fixedRate = 60000)
    public void checkHeartbeat() throws InterruptedException {
        log.info("正在检查心跳");
        serverContext.disconnectTimeout();
        serverContext.resetHeartbeat();
    }
    @Scheduled(fixedRate = 45000)
    public void reportChild(){
        log.info("正在同步子节点信息");
        ServerReportDTO reportDTO = new ServerReportDTO();
        reportDTO.setChild(serverContext.getClients().stream().collect(Collectors.toList()));
        reportDTO.setId(clientContext.getId());
        reportDTO.setTimestamp(System.currentTimeMillis() / 1000L);
        Message message = new Message();
        message.setType(MessageType.SERVER_REPORT);
        message.setData(ParseUtil.encode(reportDTO, false));
//        log.info(new Gson().toJson(message));
        clientContext.send(message);
    }
}
