package io.dekstroza.domain.services;

import io.dekstroza.domain.entities.Alarm;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.inject.Inject;
import java.util.Collection;

@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public abstract class AlarmServiceRepository implements CrudRepository<Alarm, Integer> {

    @Inject
    JdbcOperations jdbcOperations;

    public abstract void update(@Id Integer id, String name, String severity);

    public abstract Collection<Alarm> findAllBySeverity(String severity);

}
