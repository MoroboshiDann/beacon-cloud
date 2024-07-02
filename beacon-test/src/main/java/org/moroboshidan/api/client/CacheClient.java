package org.moroboshidan.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "beacon-cache")
public interface CacheClient {
    @GetMapping("/cache/test")
    void test();

    @PostMapping(value = "/cache/hmset/{key}")
    void hmset(@PathVariable(value = "key") String key, @RequestBody Map<String, Object> map);

    @PostMapping(value = "/cache/set/{key}")
    void set(@PathVariable(value = "key") String key, @RequestParam(value = "value") Object value);

    @PostMapping(value = "/cache/sadd/{key}")
    void sadd(@PathVariable(value = "key") String key, @RequestBody Map<String, Object>... maps);

    @PostMapping("/cache/pipeline/string")
    void pipelineString(@RequestBody Map<String, String> map);

    @PostMapping(value = "/cache/saddstr/{key}")
    void saddStr(@PathVariable(value = "key") String key, @RequestBody String... value);
}
