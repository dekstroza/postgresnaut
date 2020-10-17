package io.dekstroza.rest.resources;

import io.dekstroza.domain.entities.Alarm;
import io.dekstroza.domain.services.AlarmService;
import io.micrometer.core.annotation.Timed;
import io.micronaut.core.version.annotation.Version;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import static io.micronaut.http.MediaType.APPLICATION_JSON;


@Singleton
@Controller("/postgresnaut")
public class AlarmControllerImpl implements AlarmController {

    @Inject
    AlarmService alarmService;

    private static final Logger log = LoggerFactory.getLogger(AlarmControllerImpl.class);

    public AlarmControllerImpl(final AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    /**
     * Get all alarms from database
     *
     * @return Returns json array of all alarms in the database
     */
    @Timed(value = "method.alarms.api.getall", percentiles = {0.5, 0.95, 0.99}, description = "Read all alarms api metric")
    @Get(value = "/alarms", produces = APPLICATION_JSON)
    @Version("1")
    public Collection<Alarm> getAll() {
        return alarmService.getAll();
    }

    /**
     * Save alarm to the database
     *
     * @param alarm Alarm to be saved
     * @return Persisted alarm and it's location url
     */
    @Timed(value = "method.alarms.api.save", percentiles = {0.5, 0.95, 0.99}, description = "Insert alarm api metric")
    @Post(value = "/alarms", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @NewSpan("postgresnaut-service")
    @Version("1")
    public HttpResponse<Alarm> save(@SpanTag("alarm.id") @Body @NotNull Alarm alarm) {
        return HttpResponse.created(alarmService.save(alarm), URI.create("/postgresnaut/alarms/"+alarm.getId()));
    }

    /**
     * Find alarm by id
     *
     * @param id Integer representing id of this alarm
     * @return Alarm with this id
     */
    @Get(value = "/alarms/{id}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.findById", percentiles = {0.5, 0.95, 0.99}, description = "Find alarm by id api metric")
    @Version("1")
    public Optional<Alarm> findById(@PathVariable("id") Integer id) {
        return alarmService.findById(id);
    }

    /**
     * Find alarms by severity level
     *
     * @param severity Requested severity level, can be LOW, MEDIUM or CRITICAL
     * @return All alarms with requested level of severity as json array
     */
    @Get(value = "/alarms/severity/{severity}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.findBySeverity", percentiles = {0.5, 0.95, 0.99}, description = "Find alarm by severity api metric")
    @Version("1")
    public Collection<Alarm> findBySeverity(@NotBlank String severity) {
        return alarmService.findAlarmsBySeverity(severity);
    }

}
