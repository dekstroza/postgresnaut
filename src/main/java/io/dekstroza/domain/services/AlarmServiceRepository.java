package io.dekstroza.domain.services;

import io.dekstroza.domain.entities.Alarm;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.TransactionalAdvice;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.stream.Collectors;

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public abstract class AlarmServiceRepository implements CrudRepository<Alarm, Integer> {

    @Inject
    JdbcOperations jdbcOperations;

    @TransactionalAdvice
    public Alarm updateAlarm(final Alarm alarm) {
        String sql = "UPDATE alarms SET name = ?, severity = ? WHERE alarms.id = ?";
        return jdbcOperations.prepareStatement(sql, statement -> {
            statement.setString(1, alarm.getName());
            statement.setString(2, alarm.getSeverity());
            statement.setInt(3, alarm.getId());
            statement.executeUpdate();
            return alarm;
        });
    }

    @ReadOnly
    public Collection<Alarm> findBySeverity(String severity) {
        String sql = "SELECT * FROM alarms WHERE alarms.severity = ?";
        return jdbcOperations.prepareStatement(sql, statement -> {
            statement.setString(1, severity);
            ResultSet resultSet = statement.executeQuery();
            return jdbcOperations.entityStream(resultSet, Alarm.class).collect(Collectors.toList());
        });
    }
}
