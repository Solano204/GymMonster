package infraestrucutre.Adapters.Drivens.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http, KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter) {
        return http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        // Public browsing endpoints - anonymous site visitors read these
                        .pathMatchers(HttpMethod.GET,
                                "/api/page/allMemberships",
                                "/api/page/allPools",
                                "/api/page/promotions/**",
                                "/api/page/allSpecialties",
                                "/api/page/workclasses/**").permitAll()
                        // Registration itself has to be reachable before you have a token
                        .pathMatchers(HttpMethod.POST, "/api/page/registerClient").permitAll()
                        // Everything else, notably /api/page/clients/{username}/** (returns a
                        // client's full profile including their password hash), requires a
                        // valid Keycloak-issued JWT.
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)))
                .build();
    }

    @Bean
    public HaveIBeenPwnedRestApiReactivePasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiReactivePasswordChecker();
    }
}
