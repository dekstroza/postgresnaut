package io.dekstroza.domain.services;

import io.dekstroza.domain.entities.Alarm;
import io.dekstroza.domain.services.api.AlarmService;
import io.micronaut.data.annotation.Repository;
import io.micronaut.transaction.annotation.TransactionalAdvice;
import io.reactivex.Maybe;

@Repository
public abstract class AlarmServiceImpl implements AlarmService {

    @Override
    @TransactionalAdvice
    public Maybe<Alarm> update(Integer id, Alarm alarm) {
        return findById(id).defaultIfEmpty(new Alarm(id, alarm.getName(), alarm.getSeverity())).flatMapSingle(alarm1 -> {
            alarm1.setSeverity(alarm.getSeverity());
            alarm1.setName(alarm.getName());
            return update(alarm1);
        }).toMaybe();
    }
}
