package io.dekstroza.rest.resources;

import io.dekstroza.domain.entities.Alarm;
import io.dekstroza.domain.services.api.AlarmService;
import io.micrometer.core.annotation.Timed;
import io.micronaut.core.version.annotation.Version;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static io.micronaut.http.HttpHeaders.LOCATION;
import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/postgresnaut")
public class AlarmResourceController {

    @Inject
    AlarmService alarmService;

    /**
     * Get all alarms from database
     *
     * @return Returns json array of all alarms in the database
     */
    @Timed(value = "method.alarms.api.getall", percentiles = { 0.5, 0.95, 0.99 }, description = "Read all alarms api metric")
    @Get(value = "/alarms", produces = APPLICATION_JSON)
    @Version("1")
    @ExecuteOn(TaskExecutors.IO)
    public Flowable<Alarm> getAll() {
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
    public Single<HttpResponse> save(@SpanTag("alarm.id") @Body @NotNull Alarm alarm) {
        return alarmService.save(alarm).map(alarm1 -> (HttpResponse) HttpResponse.created(alarm1).header(LOCATION, location(alarm1.getId())))
                   .onErrorReturn(throwable -> HttpResponse.serverError(throwable.getMessage()));
    }

    /**
     * Update alarm method
     *
     * @param alarm
     *            Alarm to be updated
     * @return Updated alarm and it's location url
     */
    @Timed(value = "method.alarms.api.update", percentiles = { 0.5, 0.95, 0.99 }, description = "Update alarm api metric")
    @Put(value = "/alarms/{id}", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @NewSpan("postgresnaut-service")
    @Version("1")
    @ExecuteOn(TaskExecutors.IO)
    public Single<HttpResponse> update(@PathVariable("id") Integer id, @Body @NotNull Alarm alarm) {
        return alarmService.update(id, alarm).map(alarm1 -> (HttpResponse) HttpResponse.ok(alarm1).header(LOCATION, location(alarm1.getId())))
                   .onErrorReturn(throwable -> HttpResponse.serverError(throwable.getMessage())).toSingle();
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
    @ExecuteOn(TaskExecutors.IO)
    public Single<HttpResponse> findById(@PathVariable("id") Integer id) {
        Maybe<Alarm> maybe = alarmService.findById(id);
        return maybe.isEmpty().flatMap(
                   empty -> empty ? Single.just(HttpResponse.notFound()) : maybe.flatMapSingle(alarm -> Single.just(HttpResponse.ok(alarm))));
    }

    /**
     * Delete alarm
     *
     * @param id
     *            Integer representing id of alarm which is to be deleted
     * @return Alarm with this id
     */
    @Delete(value = "/alarms/{id}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.delete", percentiles = { 0.5, 0.95, 0.99 }, description = "Delete alarm api metric")
    @Version("1")
    @ExecuteOn(TaskExecutors.IO)
    public Completable delete(@PathVariable("id") Integer id) {
        return alarmService.deleteById(id);
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
    @ExecuteOn(TaskExecutors.IO)
    public Flowable<Alarm> findBySeverity(@NotBlank String severity) {
        return alarmService.findBySeverity(severity);
    }

    String location(Integer id) {
        return "/postgresnaut/alarms/" + id;
    }

}
