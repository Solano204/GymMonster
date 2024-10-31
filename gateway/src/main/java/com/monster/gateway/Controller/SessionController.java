package com.monster.gateway.Controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;
import com.monster.gateway.InterfaceServices.Session;
import lombok.Data;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/GymMonster/auth")
@Data
public class SessionController {

    private final Session impSession;

    @PostMapping("/login")
    public Mono<Token> login(@RequestHeader("username") String username,
            @RequestHeader("password") String password) {

        return impSession.login(username, password);
    }

    @PostMapping("/logout")
    public Mono<String> logout(@RequestHeader("refresh_token") String refresh_token, @RequestHeader("username") String username) {
        return impSession.logout(refresh_token, username);
    }
}
