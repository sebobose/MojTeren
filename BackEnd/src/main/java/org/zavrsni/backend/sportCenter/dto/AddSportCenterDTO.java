package org.zavrsni.backend.sportCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSportCenterDTO {

    private String email;
    private String sportCenterName;
    private String streetAndNumber;
    private String cityName;
    private Long zipCode;
    private String longitude;
    private String latitude;
    private List<MultipartFile> images;
}
