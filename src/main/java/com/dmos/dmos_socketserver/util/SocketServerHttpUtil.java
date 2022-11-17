package com.dmos.dmos_socketserver.util;

import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.data.DMOSResponse;
import com.dmos.dmos_common.data.ReportDTO;
import com.dmos.dmos_common.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

@Component
public class SocketServerHttpUtil {
    @Autowired
    private final DMOSConfig dmosConfig;
    @Autowired
    private final HttpUtil httpUtil;

    public SocketServerHttpUtil(DMOSConfig dmosConfig, HttpUtil httpUtil) {
        this.dmosConfig = dmosConfig;
        this.httpUtil = httpUtil;
    }

    public int verifyChannel(String verifyToken){
        String localToken = dmosConfig.getLocalToken();
        String url = dmosConfig.getRegister();

        HttpHeaders headers = new HttpHeaders();
        headers.add("token", localToken);

        HashMap<String, String> body = new HashMap<>();
        body.put("token", verifyToken);

        DMOSResponse response = httpUtil.post(url, "/verify", headers, new Gson().toJson(body));
        if(response.getCode() != 0)
            return -1;
        return Integer.parseInt(response.getData().get("id"));
    }

    public int reportState(ReportDTO reportDTO){
        String localToken = dmosConfig.getLocalToken();
        String url = dmosConfig.getStorage();

        HttpHeaders headers = new HttpHeaders();
        headers.add("token", localToken);

        Gson gson = new Gson();
        DMOSResponse response = httpUtil.post(url, "/report", headers, gson.toJson(reportDTO));
        return response.getCode();
    }
}
