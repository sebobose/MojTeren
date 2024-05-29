package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.dto.FieldDetailsDTO;
import org.zavrsni.backend.sportCenter.SportCenter;

import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportCenterReservationsDTO {
    private String sportCenterName;
    private String streetAndNumber;
    private String cityName;
    private Long zipCode;
    private String owner;
    private List<FieldDetailsDTO> fields;
    private String currentSport;
    private Set<String> sports;

    public SportCenterReservationsDTO(SportCenter sportCenter, String sport, List<FieldDetailsDTO> fields) {
        this.sportCenterName = sportCenter.getSportCenterName();
        this.streetAndNumber = sportCenter.getAddress().getStreetAndNumber();
        this.cityName = sportCenter.getAddress().getCity().getCityName();
        this.zipCode = sportCenter.getAddress().getCity().getZipCode();
        this.owner = sportCenter.getOwner().getFirstName() + " " + sportCenter.getOwner().getLastName();
        this.fields = fields;
        this.currentSport = sport;
        List<String> sports = sportCenter.getFields().stream().map(field -> field.getSport().getSportName()).toList();
        this.sports = Set.copyOf(sports);
    }
}
