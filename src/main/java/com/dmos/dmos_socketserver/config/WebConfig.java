package com.dmos.dmos_socketserver.config;

import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.util.HttpUtil;
import com.dmos.dmos_server.channel.ChannelCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class WebConfig {
    @Bean
    public HttpUtil httpUtil(){
        return new HttpUtil(dmosConfig(), restTemplate());
    }
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    @Bean
    public DMOSConfig dmosConfig(){
        return new DMOSConfig();
    }
    @Bean
    public ChannelCache channelCache() { return new ChannelCache(); }
}
