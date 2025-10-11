package com.cheems.cheemsaicode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.region.Region;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    private String host;
    private String secretId;
    private String secretKey;
    private String bucket;
    private String region;

    public COSClient cosClient(){
         BasicCOSCredentials basicCOSCredentials = new BasicCOSCredentials(secretId,secretKey);
         ClientConfig clientConfig = new ClientConfig(new Region(region));
         return new COSClient(basicCOSCredentials,clientConfig);
    }
}
