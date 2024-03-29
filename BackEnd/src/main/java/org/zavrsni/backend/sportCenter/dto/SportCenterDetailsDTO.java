package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.sportCenter.SportCenter;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportCenterDetailsDTO {

    private String sportCenterName;
    private String owner;
    private String address;

    public SportCenterDetailsDTO(SportCenter sportCenter) {
        this.sportCenterName = sportCenter.getSportCenterName();
        this.owner = sportCenter.getOwner().getEmail();
        this.address = sportCenter.getAddress();
    }
}
