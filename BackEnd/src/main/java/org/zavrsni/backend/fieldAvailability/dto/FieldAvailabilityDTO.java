package org.zavrsni.backend.fieldAvailability.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldAvailabilityDTO {

    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
