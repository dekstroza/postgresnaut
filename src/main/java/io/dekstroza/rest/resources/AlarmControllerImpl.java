package io.dekstroza.rest.resources;

import io.dekstroza.domain.entities.Alarm;
import io.dekstroza.domain.services.AlarmServiceImpl;
import io.micrometer.core.annotation.Timed;
import io.micronaut.core.version.annotation.Version;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/postgresnaut")
public class AlarmControllerImpl {

    @Inject
    AlarmServiceImpl alarmService;

    /**
     * Get all alarms from database
     *
     * @return Returns json array of all alarms in the database
     */
    @Timed(value = "method.alarms.api.getall", percentiles = {0.5, 0.95, 0.99}, description = "Read all alarms api metric")
    @Get(value = "/alarms", produces = APPLICATION_JSON)
    @Version("1")
    @ExecuteOn(TaskExecutors.IO)
    public Iterable<Alarm> getAll() {
        return alarmService.findAll();
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
    public HttpResponse save(@SpanTag("alarm.id") @Body @NotNull Alarm alarm) {
        return HttpResponse.created(alarmService.save(alarm)).header(HttpHeaders.LOCATION, "/postgresnaut/alarms/" + alarm.getId());
    }

    /**
     * Update alarm method
     *
     * @param alarm Alarm to be updated
     * @return Updated alarm and it's location url
     */
    @Timed(value = "method.alarms.api.update", percentiles = {0.5, 0.95, 0.99}, description = "Update alarm api metric")
    @Put(value = "/alarms", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @NewSpan("postgresnaut-service")
    @Version("1")
    @ExecuteOn(TaskExecutors.IO)
    public HttpResponse<Alarm> update(@Body @NotNull Alarm alarm) {
        alarmService.update(alarm.getId(), alarm.getName(), alarm.getSeverity());
        return HttpResponse.ok(alarm).header(HttpHeaders.LOCATION, "/postgresnaut/alarms/" + alarm.getId());
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
    @ExecuteOn(TaskExecutors.IO)
    public Optional<Alarm> findById(@PathVariable("id") Integer id) {
        return alarmService.findById(id);
    }


    /**
     * Delete alarm
     *
     * @param id Integer representing id of alarm which is to be deleted
     * @return Alarm with this id
     */
    @Delete(value = "/alarms/{id}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.delete", percentiles = {0.5, 0.95, 0.99}, description = "Delete alarm api metric")
    @Version("1")
    @ExecuteOn(TaskExecutors.IO)
    public HttpResponse delete(@PathVariable("id") Integer id) {
        alarmService.deleteById(id);
        return HttpResponse.ok();
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
    @ExecuteOn(TaskExecutors.IO)
    public Collection<Alarm> findBySeverity(@NotBlank String severity) {
        return alarmService.findBySeverity(severity);
    }

}
