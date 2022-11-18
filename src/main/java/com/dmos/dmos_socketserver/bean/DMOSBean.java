package com.dmos.dmos_socketserver.bean;

import com.dmos.dmos_common.config.DMOSConfig;
import com.dmos.dmos_common.util.HttpUtil;
import com.dmos.dmos_server.channel.ChannelCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DMOSBean {
    @Bean
    public static ChannelCache channelCache(){
        return new ChannelCache();
    }
    @Bean
    public static DMOSConfig dmosConfig(){
        return new DMOSConfig();
    }
    @ConditionalOnMissingBean(RestTemplate.class)
    @Bean
    public static RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
