package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.IServices.IEmailService;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.DetailClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.InscriptionRepository;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import com.monster.server_register.ServerRegisterApplication;
import reactor.core.publisher.Mono;

/**
 * This is the actual cross-service contract point: web-page publishes an AllClient to
 * the "flow" topic exactly like this, and RegisterService's @KafkaListener consumes it.
 * No Docker needed - @EmbeddedKafka is an in-process broker, so this runs the same way
 * `mvn test` always does. Repositories/Keycloak/email are @MockBean'd (not the concern
 * here); what this proves is that a message published the way web-page really publishes
 * it survives serialization and is correctly deserialized and routed into RegisterService.
 */
@SpringBootTest(classes = ServerRegisterApplication.class)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "flow")
@TestPropertySource(properties = "svc.kafka.bootstrapAddress=${spring.embedded.kafka.brokers}")
class RegisterServiceKafkaListenerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry listenerRegistry;

    @MockBean private ClientRepository clientRepository;
    @MockBean private MembershipRepository membershipRepository;
    @MockBean private DetailClientRepository detailClientRepository;
    @MockBean private PerTrainerRepository perTrainerRepository;
    @MockBean private InscriptionRepository inscriptionRepository;
    @MockBean private KeycloakServiceImpl keycloakService;
    @MockBean private IEmailService emailService;

    private KafkaTemplate<String, AllClient> testProducer;

    @BeforeEach
    void setUp() {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        testProducer = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));

        // Let the consumer chain start; existsByUsername is the very first call
        // createClient makes, which is enough to prove the message was received.
        when(clientRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(clientRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));

        // Without this, the test can send before the @KafkaListener container has
        // finished joining its consumer group; with the default auto.offset.reset=latest
        // that message is then silently skipped as "history".
        for (MessageListenerContainer container : listenerRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    void consumesARegistrationEventPublishedToTheFlowTopicJustLikeWebPageWould() {
        AllClient client = new AllClient(
                null, "kafka-flow-test-user", "Str0ngPassword!", "kafka-flow-test@example.com",
                "trainer1", "Carlos", "Josue", "Lopez", "Solano", "30", "180", "75",
                "premium", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 50.0);

        testProducer.send("flow", client);

        verify(clientRepository, timeout(10_000)).existsByUsername("kafka-flow-test-user");
        verify(clientRepository, timeout(10_000)).existsByEmail("kafka-flow-test@example.com");
    }
}
