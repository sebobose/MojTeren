package org.zavrsni.backend.reservation;

import org.zavrsni.backend.reservation.dto.AddReservationDTO;
import org.zavrsni.backend.reservation.dto.ReservationDTO;
import org.zavrsni.backend.reservation.dto.UserReservationDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterReservationsDTO;

import java.util.List;

public interface ReservationService {
    List<ReservationDTO> getReservations(String date, Long fieldId);

    SportCenterReservationsDTO getSportCenterFields(Long sportCenterId, String sport);

    AddReservationDTO addReservation(AddReservationDTO addReservationDTO);

    List<UserReservationDTO> getUserReservations();

    void cancelReservation(Long reservationId, String reason);
}
