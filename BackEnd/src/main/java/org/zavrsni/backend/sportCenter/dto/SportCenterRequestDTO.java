package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportCenterRequestDTO {
        private Long sportCenterId;
        private String decision;
        private String reason;
}
