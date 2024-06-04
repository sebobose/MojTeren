package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.field.dto.FieldDetailsDTO;
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
    private String streetAndNumber;
    private String cityName;
    private Long zipCode;
    private String longitude;
    private String latitude;
    private List<byte[]> images;
    private List<FieldDetailsDTO> fields;
    private Double distance;
    private String status;
    private String statusReason;
    private String sport;
    private FieldDetailsDTO filteredField;

    public SportCenterDetailsDTO(SportCenter sportCenter, String sport ) {
        setBaseDetails(sportCenter);
        this.sport = sport;
    }

    public SportCenterDetailsDTO(SportCenter sportCenter, List<Image> images) {
        setBaseDetails(sportCenter);
        this.images = images.stream().map(Image::getImage).toList();
    }

    public SportCenterDetailsDTO(SportCenter sportCenter, List<Image> images, List<Field> fields, Double distance) {
        setBaseDetails(sportCenter);
        this.images = images.stream().map(Image::getImage).toList();
        this.fields = fields.stream().map(FieldDetailsDTO::new).toList();
        this.distance = distance;
    }

    public SportCenterDetailsDTO(SportCenter sportCenter, List<Image> images, List<Field> fields, Field field, Double distance) {
        setBaseDetails(sportCenter);
        this.images = images.stream().map(Image::getImage).toList();
        this.fields = fields.stream().map(FieldDetailsDTO::new).toList();
        this.distance = distance;
        this.filteredField = new FieldDetailsDTO(field);
    }

    private void setBaseDetails(SportCenter sportCenter) {
        this.sportCenterId = sportCenter.getSportCenterId();
        this.sportCenterName = sportCenter.getSportCenterName();
        this.owner = sportCenter.getOwner().getEmail();
        this.cityName = sportCenter.getAddress().getCity().getCityName();
        this.zipCode = sportCenter.getAddress().getCity().getZipCode();
        this.streetAndNumber = sportCenter.getAddress().getStreetAndNumber();
        this.longitude = sportCenter.getAddress().getLongitude();
        this.latitude = sportCenter.getAddress().getLatitude();
        int numStatuses = sportCenter.getSportCenterStatuses().size();
        this.statusReason = sportCenter.getSportCenterStatuses().get(numStatuses - 1).getStatusComment();
        this.status = sportCenter.getSportCenterStatuses().get(numStatuses - 1).getStatus().getStatusType();
    }
}
