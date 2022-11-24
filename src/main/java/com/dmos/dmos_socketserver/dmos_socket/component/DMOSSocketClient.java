package com.dmos.dmos_socketserver.dmos_socket.component;

import com.dmos.dmos_client.DMOSClientContext;
import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.data.DMOSRequest;
import com.dmos.dmos_common.data.DMOSResponse;
import com.dmos.dmos_common.data.NodeDTO;
import com.dmos.dmos_common.data.NodeType;
import com.dmos.dmos_common.message.Message;
import com.dmos.dmos_common.message.MessageType;
import com.dmos.dmos_common.util.ConfigUtil;
import com.dmos.dmos_common.util.HttpUtil;
import com.dmos.dmos_common.util.Port;
import com.dmos.dmos_client.DMOSClient;
import com.dmos.dmos_socketserver.bean.SpringUtil;
import com.dmos.dmos_socketserver.dmos_socket.handler.DMOSSocketClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;

@Component
@Slf4j
public class DMOSSocketClient {
    private final HttpUtil httpUtil = SpringUtil.getBean(HttpUtil.class);
    private final DMOSConfig dmosConfig = SpringUtil.getBean(DMOSConfig.class);
    private final DMOSClientContext clientContext = SpringUtil.getBean(DMOSClientContext.class);
    private final RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);

    private static DMOSSocketClient single = null;
    private DMOSClient client;

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        single = this;
        // 初使化时将已静态化的testService实例化
    }

    public static DMOSSocketClient getSingle(){
        return single;
    }
    public void run(){
        new Thread(){
            @Override
            public void run(){
                String token = dmosConfig.getLocalToken();
                String url = dmosConfig.getStorage();
                HttpHeaders headers = new HttpHeaders();
                headers.add("token", token);
                DMOSResponse response = httpUtil.post(url, "/register/token", headers, new DMOSRequest(), restTemplate);
                if(response.getCode() != 0){
                    log.info("token无效，注册机器中");
                    DMOSRequest request = new DMOSRequest();
                    NodeDTO nodeDTO = new NodeDTO();
                    nodeDTO.setInterval(10000);
                    nodeDTO.setType(NodeType.SERVER);
                    request.put("info", nodeDTO);
                    DMOSResponse register = httpUtil.post(url, "/register/register", headers, request, restTemplate);
                    if(register.getCode() != 0){
                        log.error("无法获取token");
                        return;
                    }
                    token = (String) register.getData().get("token");
//                    log.info(token);
                    dmosConfig.setLocalToken(token);
                    ConfigUtil.save(dmosConfig, "config.json");
                    headers.set("token", token);
                    response = httpUtil.post(url, "/register/token", headers, new DMOSRequest(), restTemplate);
                }
                int id = (Integer) response.getData().get("id");
                clientContext.id(id);
                clientContext.token((String) response.getData().get("token"));

                DMOSClient client = new DMOSClient(new InetSocketAddress("127.0.0.1", Port.REGISTER_CHANNEL_PORT), new DMOSSocketClientHandler());
                client.connect();
            }
        }.start();
    }
    @Scheduled(fixedRate = 10000)
    public void heartbeat() {
        log.info("正在发送心跳");
        Message message = new Message();
        message.setType(MessageType.HEARTBEAT);
        clientContext.send(message);
    }
}
