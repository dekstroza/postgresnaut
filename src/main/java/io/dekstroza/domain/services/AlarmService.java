package io.dekstroza.domain.services;


import io.dekstroza.domain.entities.Alarm;

import java.util.Collection;
import java.util.Optional;

public interface AlarmService {

    Collection<Alarm> getAll();

    Alarm save(Alarm alarm);

    Optional<Alarm> findById(Integer id);

    Collection<Alarm> findAlarmsBySeverity(String severity);
}
