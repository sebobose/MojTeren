package org.zavrsni.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddReservationDTO {

    private Long fieldId;
    private Date date;
    private String startTime;
    private String endTime;
    private String message;
    private String email;
}
