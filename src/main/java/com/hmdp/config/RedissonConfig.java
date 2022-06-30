package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author icatw
 * @date 2022/6/29
 * @email 762188827@qq.com
 * @apiNote
 */
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.17.130:6379").setPassword("123321");

        return Redisson.create(config);
    }
}
