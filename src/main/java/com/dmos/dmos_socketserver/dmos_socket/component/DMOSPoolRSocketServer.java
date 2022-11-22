package com.dmos.dmos_socketserver.dmos_socket.component;

import com.dmos.dmos_common.util.Port;
import com.dmos.dmos_server.DMOSServer;
import com.dmos.dmos_socketserver.dmos_socket.config.ThreadPoolTaskConfig;
import com.dmos.dmos_socketserver.dmos_socket.handler.DMOSSocketServerHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

@Component
public class DMOSPoolRSocketServer {
    @Resource
    private ThreadPoolTaskConfig poolTaskExecutor;
    private static DMOSPoolRSocketServer single = null;

    private DMOSServer server;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        single = this;
        single.poolTaskExecutor = this.poolTaskExecutor;
        // 初使化时将已静态化的testService实例化
    }

    public static DMOSPoolRSocketServer getSingle(){
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
}
