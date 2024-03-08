package org.zavrsni.backend.sport.dto;

import lombok.Data;
import org.zavrsni.backend.sport.Sport;

@Data
public class SportDTO {
    private Long id;
    private String name;

    public SportDTO(Sport sport) {
        this.id = sport.getSportId();
        this.name = sport.getSportName();
    }
}
