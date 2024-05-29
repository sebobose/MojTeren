package org.zavrsni.backend.reservation;

import org.zavrsni.backend.reservation.dto.ReservationDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterReservationsDTO;

import java.util.List;

public interface ReservationService {
    List<ReservationDTO> getReservations(Long sportCenterId, String date, String sport);

    SportCenterReservationsDTO getSportCenterFields(Long sportCenterId, String sport);
}
