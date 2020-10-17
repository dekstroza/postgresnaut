package io.dekstroza.rest.controller;

import io.dekstroza.domain.entities.Alarm;
import io.dekstroza.domain.services.AlarmServiceRepository;
import io.dekstroza.rest.controller.api.AlarmController;
import io.micrometer.core.annotation.Timed;
import io.micronaut.core.version.annotation.Version;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/postgresnaut")
public class AlarmControllerImpl implements AlarmController {

    @Inject
    AlarmServiceRepository alarmService;

    /**
     * Get all alarms from database
     *
     * @return Returns json array of all alarms in the database
     */
    @Timed(value = "method.alarms.api.getall", percentiles = { 0.5, 0.95, 0.99 }, description = "Read all alarms api metric")
    @Get(value = "/alarms", produces = APPLICATION_JSON)
    @Version("1")
    public Iterable<Alarm> getAll() {
        return alarmService.findAll();
    }

    /**
     * Save alarm to the database
     *
     * @param alarm
     *            Alarm to be saved
     * @return Persisted alarm and it's location url
     */
    @Timed(value = "method.alarms.api.save", percentiles = { 0.5, 0.95, 0.99 }, description = "Insert alarm api metric")
    @Post(value = "/alarms", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @NewSpan("postgresnaut-service")
    @Version("1")
    public HttpResponse<Alarm> save(@SpanTag("alarm.id") @Body @NotNull Alarm alarm) {
        return HttpResponse.created(alarmService.save(alarm), URI.create("/postgresnaut/alarms/" + alarm.getId()));
    }

    /**
     * Update alarm to the database
     *
     * @param alarm
     *            Alarm to be updated, note that id can not be updated
     * @return Persisted alarm and it's location url
     */
    @Timed(value = "method.alarms.api.update", percentiles = { 0.5, 0.95, 0.99 }, description = "Update alarm api metric")
    @Put(value = "/alarms", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @NewSpan("postgresnaut-service")
    @Version("1")
    public HttpResponse<Alarm> update(@SpanTag("alarm.id") @Body @NotNull Alarm alarm) {
        return HttpResponse.ok(alarmService.updateAlarm(alarm)).header(HttpHeaders.LOCATION, "/postgresnaut/alarms/" + alarm.getId());
    }

    /**
     * Delete alarm to the database
     *
     * @param id
     *            Id of the Alarm
     * @return    Http response code 200 if the alarm has been deleted or if the alarm doesnt exist
     */
    @Timed(value = "method.alarms.api.delete", percentiles = { 0.5, 0.95, 0.99 }, description = "Delete alarm api metric")
    @Delete(value = "/alarms/{id}", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @NewSpan("postgresnaut-service")
    @Version("1")
    public HttpResponse<Alarm> delete(@PathVariable("id") @NotNull Integer id) {
        alarmService.deleteById(id);
        return HttpResponse.ok();
    }

    /**
     * Find alarm by id
     *
     * @param id
     *            Integer representing id of this alarm
     * @return Alarm with this id
     */
    @Get(value = "/alarms/{id}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.findById", percentiles = { 0.5, 0.95, 0.99 }, description = "Find alarm by id api metric")
    @Version("1")
    public Optional<Alarm> findById(@PathVariable("id") Integer id) {
        return alarmService.findById(id);
    }

    /**
     * Find alarms by severity level
     *
     * @param severity
     *            Requested severity level, can be LOW, MEDIUM or CRITICAL
     * @return All alarms with requested level of severity as json array
     */
    @Get(value = "/alarms/severity/{severity}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.findBySeverity", percentiles = { 0.5, 0.95, 0.99 }, description = "Find alarm by severity api metric")
    @Version("1")
    public Collection<Alarm> findBySeverity(@NotBlank String severity) {
        return alarmService.findBySeverity(severity);
    }

}
