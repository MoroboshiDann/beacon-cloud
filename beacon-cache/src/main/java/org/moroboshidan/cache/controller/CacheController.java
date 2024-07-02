package org.moroboshidan.cache.controller;

import com.msb.framework.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
public class CacheController {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/cache/test")
    public void test() {
        log.info("test");
    }

    @PostMapping("/cache/hmset/{key}")
    public void hmset(@PathVariable("key") String key, @RequestBody Map<String, Object> map) {
        log.info("beacon-cache, hmset() called with key={}, stored {}", key, map);
        redisClient.hSet(key, map);
    }

    @PostMapping(value = "/cache/set/{key}")
    public void set(@PathVariable(value = "key") String key, @RequestParam(value = "value") Object value) {
        log.info("【缓存模块】 set方法，存储key = {}，存储value = {}", key, value);
        redisClient.set(key, value);
    }

    @PostMapping(value = "/cache/sadd/{key}")
    public void sadd(@PathVariable(value = "key")String key, @RequestBody Map<String,Object>... value){
        log.info("【缓存模块】 sadd方法，存储key = {}，存储value = {}", key, value);
        redisClient.sAdd(key,value);
    }

    @PostMapping(value = "/cache/saddstr/{key}")
    public void saddStr(@PathVariable(value = "key")String key, @RequestBody String... value){
        log.info("【缓存模块】 saddStr方法，存储key = {}，存储value = {}", key, value);
        redisClient.sAdd(key,value);
    }

    @PostMapping(value = "/cache/sinterstr/{key}/{sinterKey}")
    public Set<Object> sinterStr(@PathVariable(value = "key")String key, @PathVariable String sinterKey,@RequestBody String... value){
        log.info("【缓存模块】 sinterStr的交集方法，存储key = {}，sinterKey = {}，存储value = {}", key, sinterKey,value);
        //1、 存储数据到set集合
        redisClient.sAdd(key,value);
        //2、 需要将key和sinterKey做交集操作，并拿到返回的set
        Set<Object> result = redisTemplate.opsForSet().intersect(key, sinterKey);
        //3、 将key删除
        redisClient.delete(key);
        //4、 返回交集结果
        return result;
    }


    @GetMapping("/cache/hgetall/{key}")
    public Map hGetAll(@PathVariable(value = "key")String key){
        log.info("【缓存模块】 hGetAll方法，获取key ={} 的数据", key);
//        因为飞马框架更新，这里的方法名称更改了。
//        Map<String, Object> value = redisClient.getMap(key);
        Map<String, Object> value = redisClient.hGetAll(key);
        log.info("【缓存模块】 hGetAll方法，获取key ={} 的数据 value = {}", key,value);
        return value;
    }

    @GetMapping("/cache/hget/{key}/{field}")
    public Object hget(@PathVariable(value = "key")String key,@PathVariable(value = "field")String field){
        log.info("【缓存模块】 hget方法，获取key ={}，field = {}的数据", key,field);
//        因为飞马框架更新，这里的方法名称更改了。
//        Object value = redisClient.getMapItem(key, field);
        Object value = redisClient.hGet(key, field);
        log.info("【缓存模块】 hget方法，获取key ={}，field = {} 的数据 value = {}", key,field,value);
        return value;
    }

    @GetMapping("/cache/smember/{key}")
    public Set smember(@PathVariable(value = "key")String key){
        log.info("【缓存模块】 smember方法，获取key ={}的数据", key);
//        因为飞马框架更新，这里的方法名称更改了。
//        Set<Object> values = redisClient.sGet(key);
        Set<Object> values = redisClient.sMembers(key);
        log.info("【缓存模块】 smember方法，获取key ={} 的数据 value = {}", key,values);
        return values;
    }

    @PostMapping("/cache/pipeline/string")
    public void pipelineString(@RequestBody Map<String,String> map){
        log.info("【缓存模块】 pipelineString，获取到存储的数据，map的长度 ={}的数据", map.size());
        redisClient.pipelined(operations -> {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                operations.opsForValue().set(entry.getKey(),entry.getValue());
            }
        });
    }

    @GetMapping(value = "/cache/get/{key}")
    public Object get(@PathVariable(value = "key") String key) {
        log.info("【缓存模块】 get方法，查询key = {}", key);
        Object value = redisClient.get(key);
        log.info("【缓存模块】 get方法，查询key = {}对应的value = {}", key,value);
        return value;
    }

    @PostMapping(value = "/cache/zadd/{key}/{score}/{member}")
    public boolean zadd(@PathVariable(value = "key")String key,
                        @PathVariable(value = "score")Long score,
                        @PathVariable(value = "member")Object member){
        log.info("【缓存模块】 zaddLong方法，存储key = {}，存储score = {}，存储value = {}", key,score, member);
        boolean result = redisClient.zAdd(key, member, score);
        return result;
    }

    // @GetMapping(value = "/cache/zrangebyscorecount/{key}/{start}/{end}")
    // public int zRangeByScoreCount(@PathVariable(value = "key") String key,
    //                               @PathVariable(value = "start") Double start,
    //                               @PathVariable(value = "end") Double end) {
    //     log.info("【缓存模块】 zRangeByScoreCount方法，查询key = {},start = {},end = {}", key,start,end);
    //     Set<ZSetOperations.TypedTuple<Object>> values = redisTemplate.opsForZSet().rangeByScoreWithScores(key, start, end);
    //     if(values != null){
    //         return values.size();
    //     }
    //     return 0;
    // }

    @DeleteMapping(value = "/cache/zremove/{key}/{member}")
    public void zRemove(@PathVariable(value = "key") String key,@PathVariable(value = "member") String member) {
        log.info("【缓存模块】 zRemove方法，删除key = {},member = {}", key,member);
        redisClient.zRemove(key,member);
    }

    @PostMapping(value = "/cache/hincrby/{key}/{field}/{delta}")
    public Long hIncrBy(@PathVariable(value = "key") String key,
                        @PathVariable(value = "field") String field,
                        @PathVariable(value = "delta") Long delta){
        log.info("【缓存模块】 hIncrBy方法，自增   key = {},field = {}，number = {}", key,field,delta);
//        因为飞马框架更新，这里的方法名称更改了。
//        Long result = redisClient.incrementMap(key, field, delta);
        Long result = redisClient.hIncrementBy(key, field, delta);
        log.info("【缓存模块】 hIncrBy方法，自增   key = {},field = {}，number = {},剩余数值为 = {}", key,field,delta,result);
        return result;
    }

    @PostMapping(value = "/cache/keys/{pattern}")
    public Set<String> keys(@PathVariable String pattern){
        log.info("【缓存模块】 keys方法，根据pattern查询key的信息  pattern = {}" ,pattern);
        Set<String> keys = redisTemplate.keys(pattern);
        log.info("【缓存模块】 keys方法，根据pattern查询key的信息  pattern = {},查询出全部的key信息 keys = {}" ,pattern,keys);
        return keys;
    }


}
