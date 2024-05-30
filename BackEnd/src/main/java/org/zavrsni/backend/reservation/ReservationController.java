package org.zavrsni.backend.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.reservation.dto.AddReservationDTO;
import org.zavrsni.backend.reservation.dto.ReservationDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterReservationsDTO;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/by-week/{date}/{fieldId}")
    public ResponseEntity<List<ReservationDTO>> getReservations(@PathVariable String date,
                                                                @PathVariable Long fieldId) {
        return ResponseEntity.ok(reservationService.getReservations(date, fieldId));
    }

    @GetMapping("/sport-center/{sport}/{sportCenterId}")
    public ResponseEntity<SportCenterReservationsDTO> getSportCenterFields(@PathVariable Long sportCenterId,
                                                                           @PathVariable String sport) {
        return ResponseEntity.ok(reservationService.getSportCenterFields(sportCenterId, sport));
    }

    @PostMapping("/add")
    public ResponseEntity<AddReservationDTO> addReservation(@RequestBody AddReservationDTO addReservationDTO) {
        return ResponseEntity.ok(reservationService.addReservation(addReservationDTO));
    }

}
