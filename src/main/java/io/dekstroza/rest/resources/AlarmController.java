package io.dekstroza.rest.resources;

import io.dekstroza.domain.entities.Alarm;
import io.micrometer.core.annotation.Timed;
import io.micronaut.core.version.annotation.Version;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

public interface AlarmController {
    /**
     * Get all alarms from database
     *
     * @return Returns json array of all alarms in the database
     */
    @Timed(value = "method.alarms.api.getall", percentiles = {0.5, 0.95, 0.99}, description = "Read all alarms api metric")
    @Get(value = "/alarms", produces = APPLICATION_JSON)
    @Version("1")
    Collection<Alarm> getAll();

    /**
     * Save single alarm into the database
     *
     * @param alarm    Alarm to be persisted
     * @return Persisted alarm as json object
     */
    @Timed(value = "method.alarms.api.save", percentiles = {0.5, 0.95, 0.99}, description = "Insert alarm api metric")
    @Post(value = "/alarms", produces = APPLICATION_JSON, consumes = APPLICATION_JSON)
    @NewSpan("mongonaut-service")
    @Version("1")
    HttpResponse<Alarm> save(@SpanTag("alarm.id") @NotNull Alarm alarm);

    /**
     * Find alarm by id
     *
     * @param id Integer representing id of this alarm
     * @return Alarm with this id
     */
    @Get(value = "/alarms/{id}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.findById", percentiles = {0.5, 0.95, 0.99}, description = "Find alarm by id api metric")
    @NewSpan("mongonaut-service")
    @Version("1")
    Optional<Alarm> findById(@SpanTag("alarm.id") @NotNull Integer id);


    /**
     * Find alarms by severity level
     *
     * @param severity Requested severity level, can be LOW, MEDIUM or CRITICAL
     * @return All alarms with requested level of severity as json array
     */
    @Get(value = "/alarms/severity/{severity}", produces = APPLICATION_JSON)
    @Timed(value = "method.alarms.api.findBySeverity", percentiles = {0.5, 0.95, 0.99}, description = "Find alarm by severity api metric")
    @Version("1")
    Collection<Alarm> findBySeverity(@NotBlank String severity);

}
