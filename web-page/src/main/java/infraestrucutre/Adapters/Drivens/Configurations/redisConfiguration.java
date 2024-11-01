
 package infraestrucutre.Adapters.Drivens.Configurations;
 import org.redisson.Redisson;
 import org.redisson.api.RedissonClient;
 import org.redisson.config.Config;
 import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.Data;

import java.util.Collections;

@Configuration
@EnableCaching
@Data
public class redisConfiguration {
   
    

    public static final String CACHE_NAME = "redis";
    
    private final ServicesUrl servicesUrl;
    @Bean 
    public RedissonClient redissonClient() {
    
        
    
    var config = new Config();
    config.useSingleServer()
    .setAddress("redis://" + servicesUrl.getRedis().getHost() + ":" + servicesUrl.getRedis().getPort())
    .setPassword(servicesUrl.getRedis().getPassword());
    return Redisson.create(config);
}

// Here UI ce
    @Bean
    @Autowired
    public CacheManager cacheManager(RedissonClient redissonClient) {
    var config = Collections.singletonMap(CACHE_NAME, new CacheConfig());
        return new RedissonSpringCacheManager(redissonClient, config);
    }
    
}
