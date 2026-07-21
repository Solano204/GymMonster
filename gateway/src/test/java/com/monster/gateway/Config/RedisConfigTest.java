package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import com.monster.gateway.PropertiesUrl.ServicesUrl;

class RedisConfigTest {

    private ServicesUrl servicesUrlFor(String host, int port, String password) {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.RedisProperties redis = new ServicesUrl.RedisProperties();
        redis.setHost(host);
        redis.setPort(port);
        redis.setPassword(password);
        servicesUrl.setRedis(redis);
        return servicesUrl;
    }

    @Test
    void reactiveRedisConnectionFactory_usesHostAndPortFromServicesUrl() {
        RedisConfig config = new RedisConfig(servicesUrlFor("redis-host", 6380, "s3cr3t"));

        ReactiveRedisConnectionFactory factory = config.reactiveRedisConnectionFactory();

        assertThat(factory).isInstanceOf(LettuceConnectionFactory.class);
        LettuceConnectionFactory lettuceFactory = (LettuceConnectionFactory) factory;
        assertThat(lettuceFactory.getHostName()).isEqualTo("redis-host");
        assertThat(lettuceFactory.getPort()).isEqualTo(6380);
    }

    @Test
    void reactiveRedisConnectionFactory_propagatesPasswordIntoStandaloneConfiguration() {
        RedisConfig config = new RedisConfig(servicesUrlFor("redis-host", 6379, "s3cr3t"));

        LettuceConnectionFactory factory = (LettuceConnectionFactory) config.reactiveRedisConnectionFactory();
        RedisStandaloneConfiguration standaloneConfig = factory.getStandaloneConfiguration();

        assertThat(standaloneConfig.getPassword().map(String::valueOf).orElse(null)).isEqualTo("s3cr3t");
    }

    @Test
    void reactiveRedisTemplate_isBackedByTheGivenConnectionFactory() {
        RedisConfig config = new RedisConfig(servicesUrlFor("redis-host", 6379, "pw"));
        ReactiveRedisConnectionFactory factory = config.reactiveRedisConnectionFactory();

        ReactiveRedisTemplate<String, String> template = config.reactiveRedisTemplate(factory);

        assertThat(template).isNotNull();
        assertThat(template.getConnectionFactory()).isSameAs(factory);
    }

    @Test
    void reactiveRedisTemplate_usesStringSerializationForKeysAndValues() {
        RedisConfig config = new RedisConfig(servicesUrlFor("redis-host", 6379, "pw"));
        ReactiveRedisConnectionFactory factory = config.reactiveRedisConnectionFactory();

        ReactiveRedisTemplate<String, String> template = config.reactiveRedisTemplate(factory);

        assertThat(template.getSerializationContext().getKeySerializationPair().getWriter()).isNotNull();
        assertThat(template.getSerializationContext().getValueSerializationPair().getWriter()).isNotNull();
    }
}
