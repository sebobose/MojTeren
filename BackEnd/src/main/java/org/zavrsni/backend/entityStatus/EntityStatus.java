package org.zavrsni.backend.entityStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.reservation.Reservation;
import org.zavrsni.backend.sportCenter.SportCenter;
import org.zavrsni.backend.status.Status;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EntityStatus {

    @Id
    @GeneratedValue
    private Long entityStatusId = 0L;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp statusChangeTS;

    private String statusComment;

    @ManyToOne
    private Status status;

    @ManyToOne
    private SportCenter sportCenter;

    @ManyToOne
    private Field field;

    @ManyToOne
    private Reservation reservation;

}
