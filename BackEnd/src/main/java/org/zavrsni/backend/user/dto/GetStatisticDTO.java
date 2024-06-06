package org.zavrsni.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetStatisticDTO {
    private List<String> sportCenters;
    private List<String> fields;
    private String period;
    private Calendar date;
}
