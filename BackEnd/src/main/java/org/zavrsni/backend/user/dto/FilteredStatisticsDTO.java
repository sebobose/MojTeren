package org.zavrsni.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilteredStatisticsDTO {
    private String chosenField;
    private String bestField;
    private String sport;
    private Long activeReservations;
    private Long canceledReservations;
    private Long finishedReservations;
    private Date creationDate;
    private Double averageReservationTime;
    private Double income;
}
