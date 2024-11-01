package infraestrucutre.Adapters.Drivens.Security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
/*
 * 
 
 private final Converter jwtAuthenticationConverter = new JwtAuthenticationConverter();
 
 @Bean
 public SecurityWebFilterChain filter(ServerHttpSecurity http) throws Exception {
    
 http.csrf(c -> c.disable())
 .authorizeExchange(auth -> auth.anyExchange().authenticated())
 .oauth2ResourceServer(oauth2 -> oauth2
 .jwt(jwt -> jwt
 .jwtAuthenticationConverter(jwtAuthenticationConverter)));
 
 return http.build();
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
*/
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless APIs
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/public/**","*/*,","/**").permitAll()  // Allow public access
                        .anyExchange().authenticated()  // Protect all other paths
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("1234"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder().encode("1234"))
                .roles("USER")
                .build();

        return new org.springframework.security.core.userdetails.MapReactiveUserDetailsService(admin, user);
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = 
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }
    @Bean
        public HaveIBeenPwnedRestApiReactivePasswordChecker compromisedPasswordChecker() {
            return new HaveIBeenPwnedRestApiReactivePasswordChecker();
        }
}

