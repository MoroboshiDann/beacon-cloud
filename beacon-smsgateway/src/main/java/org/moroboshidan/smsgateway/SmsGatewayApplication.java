package org.moroboshidan.smsgateway;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableDynamicThreadPool
public class SmsGatewayApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(SmsGatewayApplication.class, args);
    }
}
