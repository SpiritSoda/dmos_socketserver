package com.dmos.dmos_socketserver.dmos_common.util;

public class Port {
    // SpringBoot的Web端口遵循HTTP_PORT
    // Netty的Channel端口遵循CHANNEL_PORT
    public static final short SOCKET_CHANNEL_PORT = 8084;
    public static final short SOCKET_HTTP_PORT = 8083;
    public static final short REGISTER_CHANNEL_PORT = 8082;
    public static final short DATA_SERVER_HTTP_PORT = 8081;
    public static final short REGISTER_HTTP_PORT = 8081;
}
