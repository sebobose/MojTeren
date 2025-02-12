package org.zavrsni.backend.user;

import org.springframework.http.ResponseEntity;
import org.zavrsni.backend.user.dto.FilteredStatisticsDTO;
import org.zavrsni.backend.user.dto.GetStatisticDTO;
import org.zavrsni.backend.user.dto.StatisticDTO;
import org.zavrsni.backend.user.dto.UserDTO;
import java.util.List;
import java.util.Map;

public interface UserService {
    void createAdmin();

    List<UserDTO> getAllUsers();

    ResponseEntity<Map<String, String>> checkToken();

    ResponseEntity<UserDTO> getProfile();

    ResponseEntity<Void> editProfile(UserDTO userDTO);

    List<StatisticDTO> getSportCenterAndFields();

    FilteredStatisticsDTO getFilteredStatistics(GetStatisticDTO getStatisticDTO);
}
