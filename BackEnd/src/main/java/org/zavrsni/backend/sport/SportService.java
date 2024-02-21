package org.zavrsni.backend.sport;

import java.util.List;

public interface SportService {

    void createSport(String sport);

    List<SportDTO> getAllSports();
}
