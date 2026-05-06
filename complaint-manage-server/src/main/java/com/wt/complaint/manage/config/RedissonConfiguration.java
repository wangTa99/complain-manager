package com.wt.complaint.manage.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfiguration {
    private final RedisProperties redisProperties;

    public RedissonConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = "redissonClient")
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单例模式
        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword());

        return Redisson.create(config);
    }
}
