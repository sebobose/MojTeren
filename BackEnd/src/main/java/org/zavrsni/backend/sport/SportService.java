package org.zavrsni.backend.sport;

import org.zavrsni.backend.sport.dto.SportDTO;
import org.zavrsni.backend.sport.dto.SportDetailsDTO;
import java.util.List;

public interface SportService {

    void createSport(String sport);

    List<SportDTO> getAllSports();

    List<SportDetailsDTO> getAdminSports();

    void deleteSport(String sportName);
}
