package com.dmos.dmos_common.message;

public class MessageType {
    // client2server: 心跳
    public static final int HEARTBEAT = 0;
    // client2server: 状态报告
    public static final int REPORT = 1;
    // server2client: 配置变更
    public static final int CONFIG = 2;
    // client2server: 通道身份验证信息
    public static final int IDENTIFY = 3;
}
