package com.monster.gateway.PropertiesUrl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class ServicesUrlTest {

    @Test
    void keycloakSubObject_gettersAndSettersRoundTrip() {
        ServicesUrl.Keycloak keycloak = new ServicesUrl.Keycloak();

        keycloak.setClientId("Docker-Gym");
        keycloak.setClientSecret("s3cr3t");
        keycloak.setUrl("http://keycloak:8181");

        assertThat(keycloak.getClientId()).isEqualTo("Docker-Gym");
        assertThat(keycloak.getClientSecret()).isEqualTo("s3cr3t");
        assertThat(keycloak.getUrl()).isEqualTo("http://keycloak:8181");
    }

    @Test
    void redisSubObject_gettersAndSettersRoundTrip() {
        ServicesUrl.RedisProperties redis = new ServicesUrl.RedisProperties();

        redis.setHost("redis-host");
        redis.setPort(6380);
        redis.setPassword("pw");

        assertThat(redis.getHost()).isEqualTo("redis-host");
        assertThat(redis.getPort()).isEqualTo(6380);
        assertThat(redis.getPassword()).isEqualTo("pw");
    }

    @Test
    void topLevelObject_holdsBothSubObjectsIndependently() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.Keycloak keycloak = new ServicesUrl.Keycloak();
        ServicesUrl.RedisProperties redis = new ServicesUrl.RedisProperties();

        servicesUrl.setKeycloak(keycloak);
        servicesUrl.setRedis(redis);

        assertThat(servicesUrl.getKeycloak()).isSameAs(keycloak);
        assertThat(servicesUrl.getRedis()).isSameAs(redis);
    }

    @EnableConfigurationProperties(ServicesUrl.class)
    static class BindingTestConfig {
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(BindingTestConfig.class);

    @Test
    void bindsFromSvcPrefixedProperties_matchingTheApplicationPropertiesLayout() {
        contextRunner.withPropertyValues(
                "svc.keycloak.client-id=Docker-Gym",
                "svc.keycloak.client-secret=test-client-secret",
                "svc.keycloak.url=http://keycloak:8181",
                "svc.redis.host=redis-test",
                "svc.redis.port=6379",
                "svc.redis.password=test-redis-password"
        ).run(context -> {
            ServicesUrl servicesUrl = context.getBean(ServicesUrl.class);

            assertThat(servicesUrl.getKeycloak().getClientId()).isEqualTo("Docker-Gym");
            assertThat(servicesUrl.getKeycloak().getClientSecret()).isEqualTo("test-client-secret");
            assertThat(servicesUrl.getKeycloak().getUrl()).isEqualTo("http://keycloak:8181");
            assertThat(servicesUrl.getRedis().getHost()).isEqualTo("redis-test");
            assertThat(servicesUrl.getRedis().getPort()).isEqualTo(6379);
            assertThat(servicesUrl.getRedis().getPassword()).isEqualTo("test-redis-password");
        });
    }

    @Test
    void bindsSuccessfully_evenWhenOnlyPartialPropertiesAreProvided() {
        // Documents the current (risky) behavior: a missing svc.redis.* block does NOT
        // fail startup, it just leaves ServicesUrl.getRedis() == null - anything that
        // dereferences it (RedisConfig, RedisTokenValidationFilter) will NPE instead of
        // failing fast with a clear "missing property" message.
        contextRunner.withPropertyValues(
                "svc.keycloak.client-id=Docker-Gym",
                "svc.keycloak.client-secret=test-client-secret",
                "svc.keycloak.url=http://keycloak:8181"
        ).run(context -> {
            ServicesUrl servicesUrl = context.getBean(ServicesUrl.class);

            assertThat(servicesUrl.getKeycloak()).isNotNull();
            assertThat(servicesUrl.getRedis()).isNull();
        });
    }
}
