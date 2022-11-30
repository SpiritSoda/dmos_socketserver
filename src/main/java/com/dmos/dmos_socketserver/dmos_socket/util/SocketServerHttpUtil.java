package com.dmos.dmos_socketserver.dmos_socket.util;

import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.data.DMOSRequest;
import com.dmos.dmos_common.data.DMOSResponse;
import com.dmos.dmos_common.data.ClientReportDTO;
import com.dmos.dmos_common.util.HttpUtil;
import com.dmos.dmos_common.util.Port;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class SocketServerHttpUtil {
    private final DMOSConfig dmosConfig;
    private final HttpUtil httpUtil;
    private final RestTemplate restTemplate;

    @Autowired
    public SocketServerHttpUtil(DMOSConfig dmosConfig, HttpUtil httpUtil, RestTemplate restTemplate) {
        this.dmosConfig = dmosConfig;
        this.httpUtil = httpUtil;
        this.restTemplate = restTemplate;
    }

    public int verifyChannel(String verifyToken){
        String localToken = dmosConfig.getLocalToken();
        String url = dmosConfig.getRegister() + ":" + Port.REGISTER_HTTP_PORT;

        HttpHeaders headers = new HttpHeaders();
        headers.add("token", localToken);

        DMOSRequest request = new DMOSRequest();
        request.put("token", verifyToken);

        DMOSResponse response = httpUtil.post(url, "/register/verify", headers, request, restTemplate);
        if(response.getCode() != 0)
            return -1;
        return (Integer) response.getData().get("id");
    }

    public int reportState(ClientReportDTO reportDTO){
        String localToken = dmosConfig.getLocalToken();
        String url = dmosConfig.getStorage() + ":" + Port.DATA_SERVER_HTTP_PORT;

        HttpHeaders headers = new HttpHeaders();
        headers.add("token", localToken);

        DMOSRequest request = new DMOSRequest();
        request.put("report", reportDTO);

        DMOSResponse response = httpUtil.post(url, "/storage/report", headers, request, restTemplate);
        return response.getCode();
    }
}
