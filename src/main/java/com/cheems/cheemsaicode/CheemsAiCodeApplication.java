package com.cheems.cheemsaicode;

import com.cheems.cheemsaicode.config.RedisChatMemoryStoreConfig;
import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.cheems.cheemsaicode.mapper")
@EnableAspectJAutoProxy
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@EnableConfigurationProperties(RedisChatMemoryStoreConfig.class)
public class CheemsAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheemsAiCodeApplication.class, args);
    }

}
