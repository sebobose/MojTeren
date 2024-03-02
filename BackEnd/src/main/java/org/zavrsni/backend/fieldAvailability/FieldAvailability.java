package org.zavrsni.backend.fieldAvailability;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;

import java.sql.Time;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FieldAvailability {

    @Id
    @GeneratedValue
    private Long fieldAvailabilityId = 0L;

    @NotNull
    private String dayOfWeek;

    @NotNull
    private Time startTime;

    @NotNull
    private Time endTime;

    @ManyToOne
    private Field field;
}
