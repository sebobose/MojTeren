package org.zavrsni.backend.fieldAvailability.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldAvailabilityDTO {

    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public FieldAvailabilityDTO(FieldAvailability fieldAvailability) {
        List<String> startTime = List.of(fieldAvailability.getStartTime().toString().split(":"));
        List<String> endTime = List.of(fieldAvailability.getEndTime().toString().split(":"));
        this.dayOfWeek = fieldAvailability.getDayOfWeek();
        this.startTime = startTime.get(0) + ":" + startTime.get(1);
        this.endTime = endTime.get(0) + ":" + endTime.get(1);
    }
}
