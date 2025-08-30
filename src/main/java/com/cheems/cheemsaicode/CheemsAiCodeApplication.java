package com.cheems.cheemsaicode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.cheems.cheemsaicode.mapper")
@EnableAspectJAutoProxy
@SpringBootApplication
public class CheemsAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheemsAiCodeApplication.class, args);
    }

}
