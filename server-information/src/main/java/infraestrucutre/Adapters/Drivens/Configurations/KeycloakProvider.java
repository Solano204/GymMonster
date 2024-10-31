package infraestrucutre.Adapters.Drivens.Configurations;
import java.util.List;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// In this class I have to specify the url of the keycloak server, the name of the realm, the name of the client, and the client secret. to connect to the realm of the keycloak server and I can handle it with this class.

@Component
public class KeycloakProvider {
    public Mono<RealmResource> getRealmResource() {
        
        // Create a Keycloak instance
        return Mono.fromCallable(() -> {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl("http://localhost:8181")
                    .realm("master")
                    .username("admin")
                    .clientId("admin-cli")
                    .password("admin")
                    .clientSecret("ckdP5Vf6X5laZN16wm1xGIXGLZXrDC8p")
                    .resteasyClient(new ResteasyClientBuilderImpl()
                            .connectionPoolSize(10)
                            .build())
                    .build();
            return keycloak.realm("docker-real");
        }).subscribeOn(Schedulers.boundedElastic()); // Use boundedElastic scheduler for blocking calls
    }

    // RealmResource: Provides access to realm-specific operations, such as managing
    // users    
    public  Mono<UsersResource> getUserResource() {
        return getRealmResource()
                .map(RealmResource::users);
    }

    
}