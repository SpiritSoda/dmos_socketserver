package com.dmos.dmos_socketserver.dmos_socket.component;

import com.dmos.dmos_common.util.Port;
import com.dmos.dmos_client.DMOSClient;
import com.dmos.dmos_socketserver.dmos_socket.handler.DMOSSocketClientHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
public class DMOSSocketClientSide {
    @PostConstruct
    public void start(){
        DMOSClient client = new DMOSClient(new InetSocketAddress("127.0.0.1", Port.REGISTER_CHANNEL_PORT), new DMOSSocketClientHandler());
        client.connect();
    }
}
