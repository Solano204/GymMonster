package infraestrucutre.Adapters.Drivens.ImpServices;


/*
 * 
 import org.springframework.stereotype.Service;
 
 
 import ch.qos.logback.core.util.Duration;
 import lombok.Data;
 
 import org.redisson.api.RBucket;
 import org.redisson.api.RedissonClient;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.data.redis.core.RedisTemplate;
 
 import java.util.concurrent.TimeUnit;
 
 @Service
 @Data
 public class TokenServices {
    
 @Autowired
 private RedissonClient redissonClient;
 
 long time;
 TimeUnit timeUnit = TimeUnit.SECONDS;
 private long jwtExpiration = 3600;
 
 public Token getTrackById(Long trackId) {
    // Retrieve the track entity from the repository
    RBucket<Token> bucket = redissonClient.getBucket("token:" + trackId); 
    // Try to retrieve the track
    if (bucket.get() != null) {
        System.out.println("Retrieved track from Redis: " + bucket.get());
        Token r = bucket.get();
        return r;
    }
    
    Token t = new Token();
    t.setToken("token:" + trackId);
    t.setUser("user: Josue" + trackId);
    
    bucket.set(t,java.time.Duration.ofSeconds(20000)); // Set Redis value to userId
    return t; // If track not found, return null (won't be cached)
}

public void deleteTokenById(Long trackId) {
    // Create a Redis bucket for the specified trackId
    RBucket<Token> bucket = redissonClient.getBucket("token:" + trackId);
    
    // Check if the token exists in Redis
    if (bucket.isExists()) {
        // Delete the token from Redis
        bucket.delete();
        System.out.println("Token for trackId " + trackId + " has been deleted from Redis.");
    } else {
        System.out.println("No token found for trackId " + trackId + " in Redis.");
    }
}


public void modifyTokenById(Long trackId, String newToken, String newUser) {
    // Create a Redis bucket for the specified trackId
    RBucket<Token> bucket = redissonClient.getBucket("token:" + trackId);
    
    // Check if the token exists in Redis
    if (bucket.isExists()) {
        // Retrieve the existing token
        Token existingToken = bucket.get();
        System.out.println("Current token in Redis: " + existingToken);
        
        // Modify the token with new values
        existingToken.setToken(newToken);
        existingToken.setUser(newUser);
        
        // Save the modified token back to Redis with an expiration (if required)
        bucket.set(existingToken, java.time.Duration.ofDays(10));
        
        System.out.println("Token for trackId " + trackId + " has been modified in Redis.");
    } else {
        System.out.println("No token found for trackId " + trackId + " in Redis.");
    }
}
    
    

}
*/