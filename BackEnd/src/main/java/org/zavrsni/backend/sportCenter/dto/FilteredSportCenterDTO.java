package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilteredSportCenterDTO {

    private String longitude;
    private String latitude;
    private Float distance;
    private Date date;
    private String timeLow;
    private String timeHigh;
    private String sport;
    private Long reservationTime;
}
