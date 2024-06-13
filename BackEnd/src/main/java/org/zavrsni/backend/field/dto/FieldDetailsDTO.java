package org.zavrsni.backend.field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;
import org.zavrsni.backend.fieldAvailability.dto.FieldAvailabilityDTO;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.sportCenter.dto.SportCenterDetailsDTO;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDetailsDTO {

    private Long fieldId;
    private String fieldName;
    private String sport;
    private Long minResTime;
    private Long timeSlot;
    private String description;
    private Double price;
    private List<byte[]> images;
    private List<FieldAvailabilityDTO> fieldAvailabilities;
    private SportCenterDetailsDTO sportCenter;
    private Boolean hasActiveReservations;

    public FieldDetailsDTO(Field field, List<Image> images, List<FieldAvailability> fieldAvailabilities) {
        this.fieldId = field.getFieldId();
        this.fieldName = field.getFieldName();
        this.sport = field.getSport().getSportName();
        this.minResTime = field.getMinResTime();
        this.timeSlot = field.getTimeSlot();
        this.description = field.getDescription();
        this.price = field.getPrice();
        this.images = images.stream().map(Image::getImage).toList();
        this.fieldAvailabilities = fieldAvailabilities.stream().map(FieldAvailabilityDTO::new).toList();
        this.hasActiveReservations = field.getReservations().stream().anyMatch(reservation -> {
            EntityStatus lastStatus = reservation.getReservationStatuses().get(reservation.getReservationStatuses().size() - 1);
            return lastStatus.getStatus().getStatusType().equals("ACTIVE");
        });
    }

    public FieldDetailsDTO(Field field) {
        this.fieldId = field.getFieldId();
        this.fieldName = field.getFieldName();
        this.sport = field.getSport().getSportName();
        this.minResTime = field.getMinResTime();
        this.timeSlot = field.getTimeSlot();
        this.description = field.getDescription();
        this.price = field.getPrice();
    }
}
