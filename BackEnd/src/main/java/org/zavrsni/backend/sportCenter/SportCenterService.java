package org.zavrsni.backend.sportCenter;

import org.zavrsni.backend.field.dto.FieldsMetadataDTO;
import org.zavrsni.backend.sportCenter.dto.*;

import java.util.List;

public interface SportCenterService {

    Void addSportCenter(AddSportCenterDTO addSportCenterDTO);

    List<SportCenterDetailsDTO> getAllOwnerSportCenters();

    SportCenterDetailsDTO getSportCenterById(Long sportCenterId);

    Void updateSportCenter(Long sportCenterId, AddSportCenterDTO addSportCenterDTO);

    Void deactivateSportCenter(Long sportCenterId, String reason);

    List<FieldsMetadataDTO> getSportCenterFields(Long sportCenterId);

    List<SportCenterDetailsDTO> getAllSportCenters(FilteredSportCenterDTO filteredSportCenterDTO);

    List<SportCenterDetailsDTO> getSportCenterRequests();

    Void resolveSportCenterRequest(SportCenterRequestDTO sportCenterRequestDTO);

    List<SportCenterDetailsDTO> searchSportCenters(SportCenterSearchDTO sportCenterSearchDTO);
}
