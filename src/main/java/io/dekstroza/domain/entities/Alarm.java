package io.dekstroza.domain.entities;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.util.Objects;

@Schema(name = "Alarm", description = "Alarm representation")
@Introspected
@Entity
@Table(name = "alarms")
public class Alarm {

    @Id
    @SequenceGenerator(name = "alarms_id_seq",
            sequenceName = "alarms_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "alarms_id_seq")
    @Column(name = "id", updatable = false)
    private  Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private  String name;

    @Column(name = "severity", nullable = false, length = 20)
    private  String severity;

    public Alarm() {
    }

    public Alarm(String name, String severity) {
        this.name = name;
        this.severity = severity;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Alarm alarm = (Alarm) o;
        return getId().equals(alarm.getId()) && getName().equals(alarm.getName()) && getSeverity().equals(alarm.getSeverity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSeverity());

    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Alarm{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", severity='").append(severity).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
