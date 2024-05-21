package org.zavrsni.backend.reservation;

import org.zavrsni.backend.field.dto.FieldDetailsDTO;
import org.zavrsni.backend.reservation.dto.ReservationDTO;

import java.util.List;

public interface ReservationService {
    List<ReservationDTO> getReservations(Long sportCenterId, String date, String sport);

    List<FieldDetailsDTO> getSportCenterFields(Long sportCenterId, String sport);
}
