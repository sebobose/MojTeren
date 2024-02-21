package org.zavrsni.backend.sport;

import lombok.Data;

@Data
public class SportDTO {
    private Long id;
    private String name;

    public SportDTO(Sport sport) {
        this.id = sport.getSportId();
        this.name = sport.getSportName();
    }
}
