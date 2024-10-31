package com.monster.gateway.InterfaceServices;



import com.monster.gateway.Entities.Token;

import reactor.core.publisher.Mono;

public interface RedisHandlerInterface {
 
    public Mono<String> login(String username, Token newTokens);
 
    public Mono<String> logout(String username);

    public Mono<Boolean> saveAccessToken(String username, String accessToken);
}
