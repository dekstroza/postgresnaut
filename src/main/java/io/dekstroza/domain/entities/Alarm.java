package io.dekstroza.domain.entities;

import io.micronaut.core.annotation.Creator;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name="Alarm", description="Alarm description")
@MappedEntity("alarms")
public class Alarm {

    @Id
    @GeneratedValue(value = GeneratedValue.Type.SEQUENCE, ref = "alarms_id_seq")
    private Integer id;

    private final String name;
    private final String severity;

    @Creator
    public Alarm(String name, String severity) {
        this.name = name;
        this.severity = severity;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Schema(description="Alarm id, not required when creating alarms", maximum="50", nullable = false, type = "Integer")
    public Integer getId() {
        return id;
    }

    @Schema(description="Alarm name", maximum="50", nullable = false)
    public String getName() {
        return name;
    }

    @Schema(description="Alarm severity", maximum="20", nullable = false)
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
