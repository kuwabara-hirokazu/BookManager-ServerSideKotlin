package com.book.manager.presentation.config

import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

// spring-session-data-redis での Redis接続時に使用される
@EnableRedisHttpSession  // SpringとRedisのセッション管理を有効にする
class HttpSessionConfig {
    @Bean  // インスタンスをDIコンテナに登録する
    fun connectionFactory(): JedisConnectionFactory {
        // カスタム設定
//        val redisStandaloneConfiguration = RedisStandaloneConfiguration().also {
//            it.hostName = "kotlin-redis"
//            it.port = 16379
//        }
//        return JedisConnectionFactory(redisStandaloneConfiguration)
        return JedisConnectionFactory()  // デフォルト値はlocalhostと6379(Redisのデフォルトのポート)で接続される
    }
}