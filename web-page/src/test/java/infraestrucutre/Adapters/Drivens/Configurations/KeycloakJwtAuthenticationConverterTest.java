package infraestrucutre.Adapters.Drivens.Configurations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class KeycloakJwtAuthenticationConverterTest {

    @Mock
    private Jwt jwt;

    private KeycloakJwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakJwtAuthenticationConverter();
        ReflectionTestUtils.setField(converter, "principleAttribute", "preferred_username");
        ReflectionTestUtils.setField(converter, "resourceId", "Docker-Gym");
    }

    @Test
    void convert_mapsKeycloakClientRolesToRoleAuthorities() {
        when(jwt.getClaim("resource_access")).thenReturn(
                Map.of("Docker-Gym", Map.of("roles", List.of("Client"))));
        when(jwt.getClaim("preferred_username")).thenReturn("jdoe");

        StepVerifier.create(converter.convert(jwt))
                .assertNext(token -> {
                    assertThat(token).isInstanceOf(JwtAuthenticationToken.class);
                    assertThat(token.getName()).isEqualTo("jdoe");
                    List<String> authorities = token.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();
                    assertThat(authorities).contains("ROLE_Client");
                })
                .verifyComplete();
    }

    @Test
    void convert_returnsNoResourceRoleAuthorities_whenResourceAccessClaimIsMissing() {
        when(jwt.getClaim("resource_access")).thenReturn(null);
        when(jwt.getClaim("preferred_username")).thenReturn("jdoe");

        StepVerifier.create(converter.convert(jwt))
                .assertNext(token -> assertThat(token.getAuthorities()).isEmpty())
                .verifyComplete();
    }

    @Test
    void convert_returnsNoResourceRoleAuthorities_whenOurClientIdIsAbsentFromResourceAccess() {
        when(jwt.getClaim("resource_access")).thenReturn(Map.of("some-other-client", Map.of("roles", List.of("Client"))));
        when(jwt.getClaim("preferred_username")).thenReturn("jdoe");

        StepVerifier.create(converter.convert(jwt))
                .assertNext(token -> assertThat(token.getAuthorities()).isEmpty())
                .verifyComplete();
    }
}
