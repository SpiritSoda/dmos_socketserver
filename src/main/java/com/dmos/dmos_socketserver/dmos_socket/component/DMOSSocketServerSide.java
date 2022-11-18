package com.dmos.dmos_socketserver.dmos_socket.component;

import com.dmos.dmos_common.util.Port;
import com.dmos.dmos_server.DMOSServer;
import com.dmos.dmos_socketserver.dmos_socket.handler.DMOSSocketServerHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
public class DMOSSocketServerSide {
    @PostConstruct
    public void start(){
        DMOSServer server = new DMOSServer(new InetSocketAddress("127.0.0.1", Port.SOCKET_CHANNEL_PORT), new DMOSSocketServerHandler());
        server.start();
    }
}
