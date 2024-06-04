package org.zavrsni.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.reservation.Reservation;
import org.zavrsni.backend.user.dto.UserDTO;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long reservationId;
    private String date;
    private String startTime;
    private String endTime;
    private String field;
    private String statusMessage;
    private UserDTO user;
    private String sportCenterName;

    public ReservationDTO(Reservation reservation, String role) {
        this.reservationId = reservation.getReservationId();
        this.date = reservation.getDate().toString();
        this.startTime = reservation.getStartTime().toString();
        this.endTime = reservation.getEndTime().toString();
        if (role.equals("FIELD_OWNER") || role.equals("ADMIN")) {
            this.user = new UserDTO(reservation.getUser());
            List<EntityStatus> reservationStatuses = reservation.getReservationStatuses();
            this.statusMessage = reservationStatuses.get(reservationStatuses.size() - 1).getStatusComment();
        }
        this.field = reservation.getField().getFieldName();
    }
}
