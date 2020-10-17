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
        Assertions.assertTrue(application.isRunning());
    }


    @Order(2)
    @Test
    void testGetAllAlarmsReturnsEmptyCollectionWhenNoAlarms() {
        HttpRequest request = HttpRequest.GET("/alarms");
        Collection<Alarm> result = postgresnautClient.toBlocking().retrieve(request, Collection.class);
        Assertions.assertTrue(result.isEmpty());
    }

    @Order(3)
    @Test
    void testGetAlarmByIdReturns404WhenNoAlarmWithSuchId() {
        Exception exception = Assertions.assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest request = HttpRequest.GET("/alarms/3030423");
            HttpResponse<Collection<Alarm>> result = postgresnautClient.toBlocking().exchange(request);
        });
        Assertions.assertTrue(exception.getMessage().contains("Page Not Found"));

    }

    @Order(4)
    @Test
    void testGetAllAlarmsBySeverityReturnsEmptyCollectionWhenNoAlarmsFound() {
        HttpRequest request = HttpRequest.GET("/alarms/severity/MEDIUM");
        Collection<Alarm> result = postgresnautClient.toBlocking().retrieve(request, Collection.class);
        Assertions.assertTrue(result.isEmpty());
    }

    @Order(5)
    @Test
    void testSaveAlarmReturnsWithValidInputWithNoId() {
        Alarm newAlarm = new Alarm("Some very serious alarm","MEDIUM");
        HttpRequest request = HttpRequest.POST("/alarms", newAlarm);
        HttpResponse<Alarm> result = postgresnautClient.toBlocking().exchange(request);
        Assertions.assertEquals(201, result.status().getCode());
        Optional<String> resHeader = result.getHeaders().get("Location", String.class);
        Assertions.assertTrue(resHeader.isPresent());
        Assertions.assertTrue(resHeader.get().contains("/postgresnaut/alarms/"));

    }
    @Order(6)
    @Test
    void testGetAllAlarmsBySeverityReturnsCollectionWhenAlarmsFound() {
        HttpRequest request = HttpRequest.GET("/alarms/severity/MEDIUM");
        Collection<Alarm> result = postgresnautClient.toBlocking().retrieve(request, Collection.class);
        Assertions.assertFalse(result.isEmpty());
    }





}
