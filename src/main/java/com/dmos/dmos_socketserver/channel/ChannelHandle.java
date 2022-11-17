package com.dmos.dmos_socketserver.channel;

import com.dmos.dmos_common.channel.ChannelState;
import io.netty.channel.Channel;
import lombok.Data;

@Data
public class ChannelHandle {
    private int client;
    private Channel channel;
    private int state;
    private boolean heartbeat;
    public ChannelHandle(Channel channel){
        this.channel = channel;
        this.client = 0;
        this.state = ChannelState.CONNECTED;
        this.heartbeat = true;
    }
    public boolean established(){
        return this.state == ChannelState.ESTABLISHED;
    }
    public void establish(int client){
        this.state = ChannelState.ESTABLISHED;
        this.client = client;
    }
    public void heartbeat(boolean v){
        this.heartbeat = heartbeat;
    }
}
