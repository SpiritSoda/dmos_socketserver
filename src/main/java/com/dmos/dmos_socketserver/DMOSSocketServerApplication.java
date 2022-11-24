package com.dmos.dmos_socketserver;

import com.dmos.dmos_socketserver.dmos_socket.component.DMOSPoolSocketServer;
import com.dmos.dmos_socketserver.dmos_socket.component.DMOSSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@Slf4j
public class DMOSSocketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DMOSSocketServerApplication.class, args);
        DMOSPoolSocketServer.getSingle().run();
        DMOSSocketClient.getSingle().run();
    }

}
