package io.dekstroza.domain.services;

import io.dekstroza.domain.entities.Alarm;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@Repository
public abstract class AlarmServiceImpl implements CrudRepository<Alarm, Integer> {

    public abstract List<Alarm> findBySeverity(String severity);
}
