package com.monster.gateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import lombok.AllArgsConstructor;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
@AllArgsConstructor
public class SecurityConfig {


    private final ReactiveJwtDecoder jwtDecoder;
    private final ReactiveRedisTemplate<String, String> redisTemplate; // Assuming you need this for Redis in your filter
    private final WebClient webClient; // Assuming you need this for token refresh
    private final ObjectMapper objectMapper;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeExchange(auth -> auth
                // The CorsWebFilter sets CORS response headers on a preflight, but doesn't
                // by itself exempt OPTIONS from .anyExchange().authenticated() below - an
                // unauthenticated preflight would otherwise 403 before the browser ever
                // gets to see the Access-Control-Allow-Origin header.
                .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                // Can't authenticate before you have a token; logout only needs the refresh
                // token/username headers it already checks itself (see SessionController).
                .pathMatchers("/GymMonster/auth/login", "/GymMonster/auth/logout").permitAll()
                // Kubernetes liveness/readiness probes hit this unauthenticated.
                .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                // Mirrors web-page's own SecurityConfig public-path list. Without these
                // exceptions here, this chain's blanket .anyExchange().authenticated() would
                // 401 anonymous site visitors and new-client registration before the request
                // ever reaches web-page - which is the only place that was previously deciding
                // these routes are public.
                .pathMatchers(org.springframework.http.HttpMethod.GET,
                        "/api/page/allMemberships",
                        "/api/page/allPools",
                        "/api/page/promotions/**",
                        "/api/page/allSpecialties",
                        "/api/page/workclasses/**").permitAll()
                .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/page/registerClient").permitAll()
                // Everything else, notably /actuator/auditevents (login/logout history) and
                // /actuator/info, requires a valid Keycloak-issued JWT. /api/admin/** and
                // /api/page/** already get their own token check from RedisTokenValidationFilter
                // (a GlobalFilter, independent of this chain) - this is defense in depth for
                // those, and the only real gate for gateway's own local endpoints.
                .anyExchange().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder)));
        return http.build();
    }

    // Allows the standalone browser panel (frontend/) to call this gateway cross-origin.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
