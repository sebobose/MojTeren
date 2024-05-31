package org.zavrsni.backend.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.user.User;

import java.sql.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.field IN :fields")
    Long countReservationsByFields(List<Field> fields);

    List<Reservation> findAllByField(Field field);

    List<Reservation> findAllByFieldAndDate(Field field, Date date);

    List<Reservation> findAllByUser(User user);
}
