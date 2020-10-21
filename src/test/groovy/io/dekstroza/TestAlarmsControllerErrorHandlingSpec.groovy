package io.dekstroza

import io.dekstroza.domain.entities.Alarm
import io.dekstroza.domain.services.api.AlarmService
import io.dekstroza.domain.services.AlarmServiceImpl
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
class TestAlarmsControllerErrorHandlingSpec extends Specification {


    @Inject
    @Client("/postgresnaut")
    HttpClient postgresnautClient

    @Inject
    AlarmService alarmService


    void "get returns 500 when exception is thrown"() {

        given:
        alarmService.findById(42) >> { throw new Exception("Something bad happened") }

        when:
        postgresnautClient.toBlocking().retrieve(HttpRequest.GET('/alarms/42'))

        then:
        def exception = thrown(HttpClientResponseException.class)
        exception.status.code == 500
    }

    void "post returns 500 when exception is thrown"() {

        given:
        def alarm = new Alarm(1, "test alarm", "MEDIUM")

        and:
        alarmService.save(alarm) >> { throw new Exception("Something bad happened") }

        when:
        postgresnautClient.toBlocking().retrieve(HttpRequest.POST('/alarms/', alarm))

        then:
        def exception = thrown(HttpClientResponseException.class)
        exception.status.code == 500
        exception.message == "Internal Server Error: Something bad happened"
    }

    void "put returns 500 when exception is thrown"() {

        given:
        def alarm = new Alarm(1, "test alarm", "MEDIUM")

        and:
        alarmService.update(1, alarm) >> { throw new Exception("Something bad happened") }

        when:
        postgresnautClient.toBlocking().retrieve(HttpRequest.PUT('/alarms/1', alarm))

        then:
        def exception = thrown(HttpClientResponseException.class)
        exception.status.code == 500
        exception.message == "Internal Server Error: Something bad happened"
    }


    @MockBean(AlarmServiceImpl)
    AlarmService alarmService() {
        Mock(AlarmService)
        Mock(AlarmServiceImpl)
    }

}
