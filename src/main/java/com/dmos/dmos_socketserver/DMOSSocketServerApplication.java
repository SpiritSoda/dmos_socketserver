package com.dmos.dmos_socketserver;

import com.dmos.dmos_socketserver.dmos_socket.component.DMOSPoolRSocketServer;
import com.dmos.dmos_socketserver.dmos_socket.component.DMOSSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Slf4j
public class DMOSSocketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DMOSSocketServerApplication.class, args);
        DMOSPoolRSocketServer.getSingle().run();
        DMOSSocketClient.getSingle().run();
    }

}
