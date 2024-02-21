package org.zavrsni.backend.sport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SportServiceImpl implements SportService{

    private final SportRepository sportRepository;

    @Override
    public void createSport(String sportName) {
        Sport sport = new Sport();
        sport.setSportName(sportName);
        sportRepository.save(sport);
    }

    @Override
    public List<SportDTO> getAllSports() {
        return sportRepository.findAll().stream().map(SportDTO::new).toList();
    }
}
