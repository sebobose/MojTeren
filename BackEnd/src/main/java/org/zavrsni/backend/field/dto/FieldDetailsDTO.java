package org.zavrsni.backend.field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;
import org.zavrsni.backend.fieldAvailability.dto.FieldAvailabilityDTO;
import org.zavrsni.backend.image.Image;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDetailsDTO {

    private String fieldName;
    private String sport;
    private Long minResTime;
    private Long timeSlot;
    private String description;
    private List<byte[]> images;
    private List<FieldAvailabilityDTO> fieldAvailabilities;

    public FieldDetailsDTO(Field field, List<Image> images, List<FieldAvailability> fieldAvailabilities) {
        this.fieldName = field.getFieldName();
        this.sport = field.getSport().getSportName();
        this.minResTime = field.getMinResTime();
        this.timeSlot = field.getTimeSlot();
        this.description = field.getDescription();
        this.images = images.stream().map(Image::getImage).toList();
        this.fieldAvailabilities = fieldAvailabilities.stream().map(FieldAvailabilityDTO::new).toList();
    }

    public FieldDetailsDTO(Field field) {
        this.fieldName = field.getFieldName();
        this.sport = field.getSport().getSportName();
        this.minResTime = field.getMinResTime();
        this.timeSlot = field.getTimeSlot();
        this.description = field.getDescription();
    }
}
