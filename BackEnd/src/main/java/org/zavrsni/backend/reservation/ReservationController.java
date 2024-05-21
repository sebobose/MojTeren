package org.zavrsni.backend.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.field.dto.FieldDetailsDTO;
import org.zavrsni.backend.reservation.dto.ReservationDTO;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/sport-center/{sport}/{sportCenterId}/{date}")
    public ResponseEntity<List<ReservationDTO>> getReservations(@PathVariable String sport,
            @PathVariable Long sportCenterId, @PathVariable String date) {
        return ResponseEntity.ok(reservationService.getReservations(sportCenterId, date, sport));
    }

    @GetMapping("/sport-center/{sport}/{sportCenterId}")
    public ResponseEntity<List<FieldDetailsDTO>> getSportCenterFields(@PathVariable Long sportCenterId,
                                                                      @PathVariable String sport) {
        return ResponseEntity.ok(reservationService.getSportCenterFields(sportCenterId, sport));
    }
}
