package com.dmos.dmos_socketserver.dmos_common.channel;

// 只有SocketServer才需要维护与客户机的通道状态
public class ChannelState {
    // 未连接
    public static final int DISCONNECTED = 0;
    // channel已连接但身份未验证，此时拒绝除心跳的一切数据操作
    public static final int CONNECTED = 1;
    // channel已连接且身份已验证，通道正常工作
    public static final int ESTABLISHED = 2;
}
