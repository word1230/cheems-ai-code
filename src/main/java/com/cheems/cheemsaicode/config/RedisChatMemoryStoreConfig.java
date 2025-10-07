package com.cheems.cheemsaicode.config;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisChatMemoryStoreConfig {

    private  String host;

    private  int port;

    private  String password;


    private Long ttl;

    public RedisChatMemoryStoreConfig redisChatMemoryStore(String host, int port, String password, Long ttl) {
        return RedisChatMemoryStoreConfig.builder()
                .host(host)
                .port(port)
                .password(password)
                .ttl(ttl)
                .build();
    }
}
