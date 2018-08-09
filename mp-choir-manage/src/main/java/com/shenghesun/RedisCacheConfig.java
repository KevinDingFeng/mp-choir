package com.shenghesun;

import java.text.SimpleDateFormat;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class RedisCacheConfig {

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		return RedisCacheManager.create(connectionFactory);
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
		template.setConnectionFactory(factory);
		ObjectMapper om = new ObjectMapper();
		om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
		GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(
				om);
		template.setDefaultSerializer(genericJackson2JsonRedisSerializer);
		return template;
	}
}
