package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.sportCenter.SportCenter;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportCenterDetailsDTO {

    private Long sportCenterId;
    private String sportCenterName;
    private String owner;
    private String address;
    private List<byte[]> images;

    public SportCenterDetailsDTO(SportCenter sportCenter ) {
        this.sportCenterId = sportCenter.getSportCenterId();
        this.sportCenterName = sportCenter.getSportCenterName();
        this.owner = sportCenter.getOwner().getEmail();
        this.address = sportCenter.getAddress();
    }

    public SportCenterDetailsDTO(SportCenter sportCenter, List<Image> images) {
        this.sportCenterId = sportCenter.getSportCenterId();
        this.sportCenterName = sportCenter.getSportCenterName();
        this.owner = sportCenter.getOwner().getEmail();
        this.address = sportCenter.getAddress();
        this.images = images.stream().map(Image::getImage).toList();
    }
}
