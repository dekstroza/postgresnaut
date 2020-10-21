package io.dekstroza

import io.dekstroza.domain.entities.Alarm
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

import static io.micronaut.http.HttpHeaders.LOCATION
import static io.micronaut.http.HttpRequest.*
import static io.micronaut.http.HttpStatus.CREATED
import static io.micronaut.http.HttpStatus.OK

@MicronautTest
class TestAlarmsControllerSpec extends Specification {

    @Inject
    EmbeddedApplication application

    @Inject
    @Client("/postgresnaut")
    HttpClient postgresnautClient

    void "application should start successfully"() {
        expect:
        application.running
    }

    void "post with non-existing id returns 201"() {

        given:
        Alarm alarm = new Alarm("Test alarm", "LOW")

        when:
        HttpResponse<Alarm> response = postgresnautClient.toBlocking().exchange(POST('/alarms/', alarm), Alarm.class)

        then:
        response.getStatus() == CREATED
        response.getHeaders().get(LOCATION) == "/postgresnaut/alarms/"+response.getBody().get().id
        !response.getBody(Alarm.class).isEmpty()

    }

    void "put with non-existing id returns 200"() {

        given:
        Alarm alarm = new Alarm("Put alarm", "PUT")

        when:
        HttpResponse<Alarm> response = postgresnautClient.toBlocking().exchange(PUT('/alarms/445', alarm), Alarm.class)

        then:
        response.getStatus() == OK
        !response.getBody(Alarm.class).isEmpty()

    }

    void "put with existing id returns 200"() {

        given:
        Alarm alarm = new Alarm("Put alarm Updated", "LOW")

        when:
        HttpResponse<Alarm> response = postgresnautClient.toBlocking().exchange(PUT('/alarms/202', alarm), Alarm.class)

        then:
        response.getStatus() == OK
        response.getHeaders().get(LOCATION) == "/postgresnaut/alarms/202"
        !response.getBody(Alarm.class).isEmpty()

    }


    void "get with non-existing id returns 404"() {
        when:
        postgresnautClient.toBlocking().exchange(GET('/alarms/42'), Alarm.class)

        then:
        def res = thrown(HttpClientResponseException.class)
        res.message == "Not Found"
    }

    void "get with valid existing id returns alarm with that id"() {
        when:
        HttpResponse<Alarm> response = postgresnautClient.toBlocking().exchange(GET('/alarms/101'), Alarm.class)

        then:
        response.getStatus() == OK
        !response.getBody(Alarm.class).isEmpty()
        response.getBody(Alarm.class).get().id == 101
    }

    void "Get alarms by severity returns alarms when valid severity value exists"() {
        when:
        def response = postgresnautClient.toBlocking().exchange(GET('/alarms/severity/desa'), List.class)

        then:
        response.getStatus() == OK
    }

    void "Get alarms by severity returns empty list when valid severity value does not exist"() {
        when:
        def response = postgresnautClient.toBlocking().exchange(GET('/alarms/severity/bigbang'), List.class)

        then:
        response.getStatus() == OK
        response.getBody(List<Alarm>).isPresent()
        response.getBody(List<Alarm>).get().isEmpty()
    }

    void "delete with valid existing id returns http code 200"() {
        when:
        HttpResponse<Alarm> response = postgresnautClient.toBlocking().exchange(DELETE('/alarms/101'), Alarm.class)

        then:
        response.getStatus() == OK
    }

    void "delete with valid non-existing id returns http code 200"() {
        when:
        HttpResponse<Alarm> response = postgresnautClient.toBlocking().exchange(DELETE('/alarms/404'), Alarm.class)

        then:
        response.getStatus() == OK
    }

}
