package org.moroboshidan.cache.config;

// @Configuration
public class RedisConfig {
    // @Bean
    // public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory redisConnectionFactory, RedisSerializer<Object> redisSerializer) {
    //     // 1. 构建RedisTemplate对象
    //     RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
    //     // 2. 封装RedisConnectionFactory到RedisTemplate对象中
    //     redisTemplate.setConnectionFactory(redisConnectionFactory);
    //     // 3. 设置redis key的序列化方式
    //     redisTemplate.setKeySerializer(RedisSerializer.string());
    //     redisTemplate.setHashKeySerializer(RedisSerializer.string());
    //     // 4. 设置value的序列化方式
    //     // Date日期格式的数据，需要特殊支持，因此不能直接使用redis提供的序列化类，而是自己注册一个bean
    //     redisTemplate.setValueSerializer(redisSerializer);
    //     redisTemplate.setHashValueSerializer(redisSerializer);
    //     // 5. 启动，保证生效
    //     redisTemplate.afterPropertiesSet();
    //     // 6. 返回redis template
    //     return redisTemplate;
    // }
    //
    // @Bean
    // public RedisSerializer<Object> redisSerializer() {
    //     // 1. 构建JSON的ObjectMapper
    //     ObjectMapper objectMapper = new ObjectMapper();
    //     // 2. 设置jdk8日期格式支持
    //     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //     DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //     Module timeModule = new JavaTimeModule()
    //             .addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter))
    //             .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter))
    //             .addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter))
    //             .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
    //     objectMapper.registerModule(timeModule);
    //     // 3. 返回对象
    //     Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    //     jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    //     return jackson2JsonRedisSerializer;
    // }
}
