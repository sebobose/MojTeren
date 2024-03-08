package org.zavrsni.backend.sport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.field.FieldRepository;
import org.zavrsni.backend.reservation.ReservationRepository;
import org.zavrsni.backend.sport.dto.SportDTO;
import org.zavrsni.backend.sport.dto.SportDetailsDTO;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SportServiceImpl implements SportService{

    private final SportRepository sportRepository;
    private final FieldRepository fieldRepository;
    private final ReservationRepository reservationRepository;

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

    @Override
    public List<SportDetailsDTO> getAdminSports() {
        List<Sport> sports = sportRepository.findAll();
        List<Field> fields = fieldRepository.findAll();
        return sports.stream().map(sport -> {
            SportDetailsDTO sportDetailsDTO = new SportDetailsDTO();
            sportDetailsDTO.setSportName(sport.getSportName());
            List<Field> fieldsBySport = fields.stream().filter(field -> field.getSport().equals(sport)).toList();
            sportDetailsDTO.setReservations(reservationRepository.countReservationsByFields(fieldsBySport));
            sportDetailsDTO.setFields((long) fieldsBySport.size());
            return sportDetailsDTO;
        }).toList();
    }

    @Override
    public void deleteSport(String sportName) {
        Sport sport = sportRepository.findBySportName(sportName);
        sportRepository.delete(sport);
    }
}
