package com.dmos.dmos_socketserver.dmos_common.config;

import com.dmos.dmos_socketserver.dmos_common.util.ConfigUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Data
@Component
public class DMOSConfig {
    private String localToken;
    // 父服务器 IP
    private String socketIP;
    // 注册服务器 域名 或 IP + 端口
    private String register;
    // 数据服务器 域名 或 IP + 端口
    private String storage;
    public DMOSConfig() {
        try{
            HashMap<String, String> config = ConfigUtil.load("config.json");
            localToken = config.get("token");
            socketIP = config.get("localhost");
            register = config.get("register");
            storage = register;
            socketIP = register;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
