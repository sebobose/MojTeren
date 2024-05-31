package org.zavrsni.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.reservation.Reservation;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReservationDTO {

    private Long reservationId;
    private String time;
    private String sportCenterName;
    private String address;
    private String fieldName;
    private String sportName;
    private String status;

    public UserReservationDTO(Reservation reservation, EntityStatus status) {
        this.reservationId = reservation.getReservationId();
        this.time = reservation.getDate() + " " + reservation.getStartTime() + " - " + reservation.getEndTime();
        this.sportCenterName = reservation.getField().getSportCenter().getSportCenterName();
        this.address = reservation.getField().getSportCenter().getAddress().getStreetAndNumber() + ", " +
                reservation.getField().getSportCenter().getAddress().getCity().getCityName();
        this.fieldName = reservation.getField().getFieldName();
        this.sportName = reservation.getField().getSport().getSportName();
        this.status = status.getStatus().getStatusType();
    }
}
