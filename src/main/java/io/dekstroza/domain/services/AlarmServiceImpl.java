package io.dekstroza.domain.services;


import io.dekstroza.domain.entities.Alarm;
import io.micrometer.core.annotation.Timed;
import io.micronaut.data.annotation.Repository;
import io.micronaut.transaction.annotation.TransactionalAdvice;


import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Optional;

@Singleton
public class AlarmServiceImpl implements AlarmService {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    @TransactionalAdvice
    @Timed(value = "method.alarms.service.getall", percentiles = {0.5, 0.95, 0.99}, description = "Read all service call metric")
    public Collection<Alarm> getAll() {
        return entityManager.createQuery("FROM Alarm", Alarm.class).getResultList();
    }

    @Override
    @TransactionalAdvice
    @Timed(value = "method.alarms.service.save", percentiles = {0.5, 0.95, 0.99}, description = "Save alarm service call metric")
    public Alarm save(Alarm alarm) {
        entityManager.persist(alarm);
        return alarm;
    }


    @Override
    @TransactionalAdvice
    @Timed(value = "method.alarms.service.findById", percentiles = {0.5, 0.95, 0.99}, description = "Find alarm by id service call metric")
    public Optional<Alarm> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Alarm.class, id));
    }

    @Override
    @TransactionalAdvice
    @Timed(value = "method.alarms.service.findBySeverity", percentiles = {0.5, 0.95, 0.99}, description = "Find alarms by severity service call metric")
    public Collection<Alarm> findAlarmsBySeverity(String severity) {
        return entityManager.createQuery("FROM Alarm a WHERE a.severity=:parSeverity", Alarm.class)
                .setParameter("parSeverity", severity)
                .getResultList();
    }

}
