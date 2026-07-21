package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class ScheduleInformationClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private static final String BASE_URL = "http://server-informations:8082";

    private ScheduleInformationClient repository;

    @BeforeEach
    void setUp() {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.InfoProperties info = new ServicesUrl.InfoProperties();
        info.setUrl(BASE_URL);
        servicesUrl.setInfo(info);

        repository = new ScheduleInformationClient(webClientBuilder, servicesUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    private Schedule sample() {
        return new Schedule(1, "MONDAY", "08:00", "09:00");
    }

    @Test
    void createSchedule_postsToFixedEndpoint_withBody() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        Schedule schedule = sample();
        when(requestBodySpec.bodyValue(schedule)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Schedule.class)).thenReturn(Mono.just(schedule));

        StepVerifier.create(repository.createSchedule(schedule)).expectNext(schedule).verifyComplete();
    }

    @Test
    void getAllSchedules_getsFixedEndpoint_asAFlux() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Schedule schedule = sample();
        when(responseSpec.bodyToFlux(Schedule.class)).thenReturn(Flux.just(schedule));

        StepVerifier.create(repository.getAllSchedules()).expectNext(schedule).verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture());
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/schedules");
    }

    @Test
    void getScheduleByStartTime_getsWithStartTimePathVariable() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("08:00"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Schedule schedule = sample();
        when(responseSpec.bodyToFlux(Schedule.class)).thenReturn(Flux.just(schedule));

        StepVerifier.create(repository.getScheduleByStartTime("08:00")).expectNext(schedule).verifyComplete();
    }

    @Test
    void getScheduleByDay_getsWithDayPathVariable() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("MONDAY"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Schedule schedule = sample();
        when(responseSpec.bodyToFlux(Schedule.class)).thenReturn(Flux.just(schedule));

        StepVerifier.create(repository.getScheduleByDay("MONDAY")).expectNext(schedule).verifyComplete();
    }

    @Test
    void getScheduleByDayGym_getsWithDayPathVariable_onADistinctGymEndpoint() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq("MONDAY"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Schedule schedule = sample();
        when(responseSpec.bodyToFlux(Schedule.class)).thenReturn(Flux.just(schedule));

        StepVerifier.create(repository.getScheduleByDayGym("MONDAY")).expectNext(schedule).verifyComplete();

        ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestHeadersUriSpec).uri(uriCaptor.capture(), eq("MONDAY"));
        assertThat(uriCaptor.getValue()).isEqualTo(BASE_URL + "/api/schedules/day/gym/{day}");
    }

    @Test
    void updateSchedule_putsWithIdPathVariable_andBody() {
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), eq(1))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        Schedule schedule = sample();
        when(requestBodySpec.bodyValue(schedule)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Schedule.class)).thenReturn(Mono.just(schedule));

        StepVerifier.create(repository.updateSchedule(1, schedule)).expectNext(schedule).verifyComplete();
    }

    @Test
    void deleteSchedule_deletesWithIdPathVariable() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(1))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(repository.deleteSchedule(1)).expectNext("Deleted").verifyComplete();
    }
}
