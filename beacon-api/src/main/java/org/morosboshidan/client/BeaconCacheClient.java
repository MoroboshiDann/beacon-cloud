package org.morosboshidan.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "beacon-cache")
public interface BeaconCacheClient {

}
