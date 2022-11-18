package com.dmos.dmos_socketserver;

import com.dmos.dmos_common.util.Port;
import com.dmos.dmos_server.server.DMOSServer;
import com.dmos.dmos_socketserver.dmos_socket.handler.DMOSSocketServerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;


@SpringBootApplication
@Slf4j
public class DMOSSocketServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(DMOSSocketServerApplication.class, args);
        DMOSServer server = new DMOSServer();
        server.start(new InetSocketAddress("127.0.0.1", Port.SOCKET_CHANNEL_PORT), new DMOSSocketServerHandler());

    }

}
