package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportCenterSearchDTO {

    private String search;
    private String sport;
    private String longitude;
    private String latitude;
}
