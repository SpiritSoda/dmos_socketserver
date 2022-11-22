package com.dmos.dmos_socketserver.dmos_socket.component;

import com.dmos.dmos_client.DMOSClientContext;
import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.data.DMOSRequest;
import com.dmos.dmos_common.data.DMOSResponse;
import com.dmos.dmos_common.util.HttpUtil;
import com.dmos.dmos_common.util.Port;
import com.dmos.dmos_client.DMOSClient;
import com.dmos.dmos_socketserver.bean.SpringUtil;
import com.dmos.dmos_socketserver.dmos_socket.handler.DMOSSocketClientHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
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
                DMOSResponse response = httpUtil.post(url, "/api/register/token", headers, new DMOSRequest(), restTemplate);
                int id = (Integer) response.getData().get("id");
                clientContext.id(id);
                clientContext.token((String) response.getData().get("token"));

                DMOSClient client = new DMOSClient(new InetSocketAddress("127.0.0.1", Port.REGISTER_CHANNEL_PORT), new DMOSSocketClientHandler());
                client.connect();
            }
        }.start();
    }
}