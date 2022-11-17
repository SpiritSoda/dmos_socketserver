package com.dmos.dmos_common.util;

import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.data.DMOSResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.HashMap;

@Component
public class HttpUtil {
    private final DMOSConfig dmosConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public HttpUtil(DMOSConfig dmosConfig, RestTemplate restTemplate){
        this.dmosConfig = dmosConfig;
        this.restTemplate = restTemplate;
    }
    public DMOSResponse post(String base_url, String service, HttpHeaders headers, String json){
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        if(!service.startsWith("/"))
            service = "/" + service;
        while(base_url.endsWith("/"))
            base_url = base_url.substring(0, base_url.length() - 1);
        String url = base_url + service;
        return restTemplate.postForObject(url, request, DMOSResponse.class);
    }
}
