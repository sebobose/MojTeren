package org.zavrsni.backend.status;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.fieldStatus.FieldStatus;
import org.zavrsni.backend.reservationStatus.ReservationStatus;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @GeneratedValue
    private Long statusId;

    @NotNull
    private String statusType;

    @OneToMany(mappedBy = "status")
    private List<FieldStatus> fieldStatuses;

    @OneToMany(mappedBy = "status")
    private List<ReservationStatus> reservationStatuses;
}
