package io.dekstroza;

import io.dekstroza.domain.entities.Alarm;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MicronautTest
public class AlarmsControllerTest {

    @Inject
    EmbeddedApplication application;

    @Inject
    @Client("/postgresnaut")
    HttpClient postgresnautClient;

    @Order(1)
    @Test
    void testItWorks() {
        assertTrue(application.isRunning());
    }

    @Order(2)
    @Test
    void testGetAllAlarmsReturnsEmptyCollectionWhenNoAlarms() {
        HttpRequest request = HttpRequest.GET("/alarms");
        Collection<Alarm> result = postgresnautClient.toBlocking().retrieve(request, Collection.class);
        assertTrue(result.isEmpty());
    }

    @Order(3)
    @Test
    void testGetAlarmByIdReturns404WhenNoAlarmWithSuchId() {
        Exception exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest request = HttpRequest.GET("/alarms/3030423");
            HttpResponse<Collection<Alarm>> result = postgresnautClient.toBlocking().exchange(request);
        });
        assertTrue(exception.getMessage().contains("Page Not Found"));

    }

    @Order(4)
    @Test
    void testGetAllAlarmsBySeverityReturnsEmptyCollectionWhenNoAlarmsFound() {
        HttpRequest request = HttpRequest.GET("/alarms/severity/MEDIUM");
        Collection<Alarm> result = postgresnautClient.toBlocking().retrieve(request, Collection.class);
        assertTrue(result.isEmpty());
    }

    @Order(5)
    @Test
    void testSaveAlarmReturnsWithValidInput() {
        Alarm newAlarm = new Alarm("Some very serious alarm", "MEDIUM");
        HttpRequest request = HttpRequest.POST("/alarms", newAlarm);
        HttpResponse<Alarm> result = postgresnautClient.toBlocking().exchange(request);
        assertEquals(201, result.status().getCode());
        Optional<String> resHeader = result.getHeaders().get("Location", String.class);
        assertTrue(resHeader.isPresent());
        assertTrue(resHeader.get().contains("/postgresnaut/alarms/"));

    }

    @Order(6)
    @Test
    void testUpdateAlarm() {
        Alarm newAlarm = new Alarm("Some very serious alarm", "LOW");
        Alarm updatedAlarm = new Alarm("Some very serious alarm", "LOW");
        HttpRequest createNewAlarmRequest = HttpRequest.POST("/alarms", newAlarm);
        newAlarm = postgresnautClient.toBlocking().retrieve(createNewAlarmRequest, Alarm.class);

        updatedAlarm.setId(newAlarm.getId());

        HttpRequest updateAlarmRequest = HttpRequest.PUT("/alarms", updatedAlarm);
        HttpResponse<Alarm> result = postgresnautClient.toBlocking().exchange(updateAlarmRequest, Alarm.class);

        assertEquals(200, result.status().getCode());
        Optional<String> resHeader = result.getHeaders().get("Location", String.class);
        assertTrue(resHeader.isPresent());
        assertTrue(resHeader.get().contains("/postgresnaut/alarms/" + newAlarm.getId()));
        assertEquals("LOW", result.getBody(Alarm.class).get().getSeverity());

    }

    @Order(7)
    @Test
    void testGetAllAlarmsBySeverityReturnsCollectionWhenAlarmsFound() {

        Alarm newAlarm = new Alarm("Some very kinda serious alarm", "MEDIUM");
        HttpRequest createNewAlarmRequest = HttpRequest.POST("/alarms", newAlarm);
        postgresnautClient.toBlocking().exchange(createNewAlarmRequest);

        HttpRequest request = HttpRequest.GET("/alarms/severity/MEDIUM");
        Collection<Alarm> result = postgresnautClient.toBlocking().retrieve(request, Collection.class);
        Assertions.assertFalse(result.isEmpty());
    }

    @Order(8)
    @Test
    void testDeleteAlarmWithValidInput() {
        Alarm newAlarm = new Alarm("Soon to be deleted alarm", "LOW");
        HttpRequest createNewAlarmRequest = HttpRequest.POST("/alarms", newAlarm);
        newAlarm = postgresnautClient.toBlocking().retrieve(createNewAlarmRequest, Alarm.class);

        HttpRequest deleteAlarmRequest = HttpRequest.DELETE("/alarms/" + newAlarm.getId());
        HttpResponse<Alarm> result = postgresnautClient.toBlocking().exchange(deleteAlarmRequest, Alarm.class);
        assertEquals(200, result.getStatus().getCode());

        Alarm finalNewAlarm = newAlarm;
        Exception exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest findDeletedAlarmRequest = HttpRequest.GET("/alarms/" + finalNewAlarm.getId());
            HttpResponse<Alarm> findDeletedAlarmResponse = postgresnautClient.toBlocking().exchange(findDeletedAlarmRequest, Alarm.class);
        });
        assertTrue(exception.getMessage().contains("Page Not Found"));

    }

    @Order(8)
    @Test
    void testDeleteAlarmWithNonExistingAlarmId() {
        HttpRequest deleteAlarmRequest = HttpRequest.DELETE("/alarms/" + 1002); //some id that should not exist
        HttpResponse<Alarm> result = postgresnautClient.toBlocking().exchange(deleteAlarmRequest, Alarm.class);
        assertEquals(200, result.getStatus().getCode());


    }
}