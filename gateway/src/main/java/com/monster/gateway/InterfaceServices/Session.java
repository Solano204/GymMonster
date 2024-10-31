package com.monster.gateway.InterfaceServices;



import com.monster.gateway.Entities.Token;

import reactor.core.publisher.Mono;

public interface Session {
    


    public Mono<Token> login(String username, String password);
    public Mono<String> logout(String refreshToken,String username);

}
