package com.cheems.cheemsaicode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class CheemsAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheemsAiCodeApplication.class, args);
    }

}
