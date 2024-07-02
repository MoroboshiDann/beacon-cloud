package org.morosboshidan.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class BeaconSearchApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(BeaconSearchApplication.class, args);
    }
}
