package com.monster.gateway.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import com.monster.gateway.PropertiesUrl.ServicesUrl;

import lombok.Data;

@Configuration
@Data
public class coder {

    
    private final ServicesUrl  servicesUrl;
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder
                .withJwkSetUri(""+servicesUrl.getKeycloak().getUrl()+"/realms/docker-real/protocol/openid-connect/certs").build();
    }

}