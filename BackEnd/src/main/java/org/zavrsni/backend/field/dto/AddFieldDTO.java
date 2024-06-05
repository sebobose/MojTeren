package org.zavrsni.backend.field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.zavrsni.backend.fieldAvailability.dto.FieldAvailabilityDTO;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFieldDTO {

        private String fieldName;
        private String sportName;
        private Long minResTime;
        private Long timeSlot;
        private String description;
        private Long sportCenterId;
        private Double price;
        private List<MultipartFile> images;
        private List<FieldAvailabilityDTO> fieldAvailabilities;
}
