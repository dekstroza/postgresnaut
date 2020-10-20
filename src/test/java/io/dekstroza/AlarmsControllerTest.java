package io.dekstroza;

import io.dekstroza.domain.entities.Alarm;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;


import static io.micronaut.http.HttpHeaders.*;
import static org.junit.jupiter.api.Assertions.*;


@MicronautTest
public class AlarmsControllerTest {

    @Inject
    EmbeddedApplication application;

    @Inject
    @Client("/postgresnaut")
    HttpClient postgresnautClient;


    public Alarm persistAlarm(Alarm alarm) {
        MutableHttpRequest<Alarm> request = HttpRequest.POST("/alarms", alarm);
        HttpResponse<Alarm> exchange = postgresnautClient.toBlocking().exchange(request, Alarm.class);
        return exchange.getBody().get();
    }


    @Test
    void testServiceStarts() {
        assertTrue(application.isRunning());
    }


    @Test
    void testGetAlarmByIdReturns404WhenNoAlarmWithSuchId() {
        Exception exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest request = HttpRequest.GET("/alarms/3030423");
            HttpResponse<Collection<Alarm>> result = postgresnautClient.toBlocking().exchange(request);
        });
        assertTrue(exception.getMessage().contains("Page Not Found"));
    }


    @Test
    void testSaveAlarmWithValidInput() {
        Alarm newAlarm = new Alarm("Some very serious alarm", "MEDIUM");
        MutableHttpRequest<Alarm> request = HttpRequest.POST("/alarms", newAlarm);
        HttpResponse<Alarm> exchange = postgresnautClient.toBlocking().exchange(request, Alarm.class);
        Optional<Alarm> result = exchange.getBody();
        assertTrue(result.isPresent());
        assertEquals("/postgresnaut/alarms/" + result.get().getId(), exchange.header(LOCATION));
    }

    @Test
    void testUpdateAlarmWithValidInput() {

        Alarm newAlarm = new Alarm("Some very serious alarm", "MEDIUM");
        Alarm persisted = persistAlarm(newAlarm);
        newAlarm.setId(persisted.getId());
        newAlarm.setSeverity("LOW");
        newAlarm.setName("Some not so serious alarm");
        MutableHttpRequest<Alarm> request = HttpRequest.PUT("/alarms", newAlarm);
        HttpResponse<Alarm> updateResult = postgresnautClient.toBlocking().exchange(request, Alarm.class);
        assertTrue(updateResult.getBody().isPresent());
        assertEquals("Some not so serious alarm", updateResult.getBody().get().getName());
        assertEquals("LOW", updateResult.getBody().get().getSeverity());
        assertEquals("/postgresnaut/alarms/" + newAlarm.getId(), updateResult.header(LOCATION));


    }


    @Test
    void testDeleteAlarmWithValidInput() {
        Alarm newAlarm = new Alarm("Some very serious alarm", "MEDIUM");
        Alarm persisted = persistAlarm(newAlarm);

        MutableHttpRequest<Alarm> deleteRequest = HttpRequest.DELETE("/alarms/" + persisted.getId(), newAlarm);

        HttpResponse<Alarm> deleteResponse = postgresnautClient.toBlocking().exchange(deleteRequest, Alarm.class);
        assertEquals(200, deleteResponse.getStatus().getCode());


    }


}
