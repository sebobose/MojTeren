package org.zavrsni.backend.fieldStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.status.Status;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FieldStatus {

    @Id
    @GeneratedValue
    private Long fieldStatusId = 0L;

    @NotNull
    private Timestamp statusChangeTS;

    private String statusComment;

    @ManyToOne
    private Status status;

    @ManyToOne
    private Field field;
}
