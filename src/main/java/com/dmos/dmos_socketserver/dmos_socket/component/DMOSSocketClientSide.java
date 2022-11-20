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
import com.google.gson.Gson;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
public class DMOSSocketClientSide {
    private final HttpUtil httpUtil = SpringUtil.getBean(HttpUtil.class);
    private final DMOSConfig dmosConfig = SpringUtil.getBean(DMOSConfig.class);
    private final DMOSClientContext clientContext = SpringUtil.getBean(DMOSClientContext.class);
    private final RestTemplate restTemplate = SpringUtil.getBean(RestTemplate.class);
    @PostConstruct
    public void start(){
        String token = dmosConfig.getLocalToken();
        String url = dmosConfig.getStorage();
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);
        DMOSResponse response = httpUtil.post(url, "/api/token", headers, new DMOSRequest(), restTemplate);
        int id = Integer.parseInt(response.getData().get("id"));
        clientContext.id(id);
        clientContext.token(response.getData().get("token"));

        DMOSClient client = new DMOSClient(new InetSocketAddress("127.0.0.1", Port.REGISTER_CHANNEL_PORT), new DMOSSocketClientHandler());
        client.connect();
    }
}
