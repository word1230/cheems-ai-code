package com.cheems.cheemsaicode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;

@MapperScan("com.cheems.cheemsaicode.mapper")
@EnableAspectJAutoProxy
@SpringBootApplication(exclude = { RedisEmbeddingStoreAutoConfiguration.class })
public class CheemsAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheemsAiCodeApplication.class, args);
    }
}
