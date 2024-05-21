package org.zavrsni.backend.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.field.dto.FieldDetailsDTO;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;
import org.zavrsni.backend.fieldAvailability.FieldAvailabilityRepository;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.image.ImageRepository;
import org.zavrsni.backend.reservation.dto.ReservationDTO;
import org.zavrsni.backend.sportCenter.SportCenter;
import org.zavrsni.backend.sportCenter.SportCenterRepository;
import org.zavrsni.backend.sportCenter.dto.SportCenterDetailsDTO;
import org.zavrsni.backend.user.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final SportCenterRepository sportCenterRepository;
    private final ImageRepository imageRepository;
    private final FieldAvailabilityRepository fieldAvailabilityRepository;

    @Override
    public List<ReservationDTO> getReservations(Long sportCenterId, String dateString, String sport) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        int week;
        int year;

        try {
            Date date = formatter.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            week = calendar.get(Calendar.WEEK_OF_YEAR);
            year = calendar.get(Calendar.YEAR);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format");
        }

        SportCenter sportCenter = sportCenterRepository.findById(sportCenterId).orElseThrow(
                () -> new IllegalArgumentException("Sport center not found"));

        List<Field> fields = sportCenter.getFields().stream().
                filter(field -> field.getSport().getSportName().equals(sport)).toList();

        List<Reservation> reservations = new ArrayList<>();
        for (Field field : fields) {
            List<Reservation> fieldReservations = reservationRepository.findAllByField(field);
            reservations.addAll(fieldReservations);
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role;
        if (principal instanceof String) {
            role = "UNREGISTERED_USER";
        }
        else {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            role = user.getRole().getRoleName();
        }

        return reservations.stream().filter(reservation -> {
            List<EntityStatus> reservationStatuses = reservation.getReservationStatuses();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reservation.getDate());
            int reservationWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            int reservationYear = calendar.get(Calendar.YEAR);
            String statusType = reservationStatuses.get(reservationStatuses.size() - 1).getStatus().getStatusType();
            return week == reservationWeek && year == reservationYear &&
                    (statusType.equals("ACTIVE") || statusType.equals("FINISHED"));
        }).map(reservation -> new ReservationDTO(reservation, role)).toList();
    }

    @Override
    public List<FieldDetailsDTO> getSportCenterFields(Long sportCenterId, String sport) {
        SportCenter sportCenter = sportCenterRepository.findById(sportCenterId).orElseThrow(
                () -> new IllegalArgumentException("Sport center not found"));

        return sportCenter.getFields().stream().filter(field -> {
            List<EntityStatus> fieldStatuses = field.getFieldStatuses();
            String statusType = fieldStatuses.get(fieldStatuses.size() - 1).getStatus().getStatusType();
            String Fieldsport = field.getSport().getSportName();
            return statusType.equals("ACTIVE") && Fieldsport.equals(sport);
        }).map(field -> {
            List<Image> images = imageRepository.findAllByField(field);
            List<FieldAvailability> fieldAvailabilities = fieldAvailabilityRepository.findAllByField(field);
            return new FieldDetailsDTO(field, images, fieldAvailabilities, new SportCenterDetailsDTO(sportCenter));
        }).toList();
    }
}
