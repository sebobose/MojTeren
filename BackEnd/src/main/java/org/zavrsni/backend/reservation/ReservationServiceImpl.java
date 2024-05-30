package org.zavrsni.backend.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.entityStatus.EntityStatusRepository;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.field.FieldRepository;
import org.zavrsni.backend.field.dto.FieldDetailsDTO;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;
import org.zavrsni.backend.fieldAvailability.FieldAvailabilityRepository;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.image.ImageRepository;
import org.zavrsni.backend.reservation.dto.AddReservationDTO;
import org.zavrsni.backend.reservation.dto.ReservationDTO;
import org.zavrsni.backend.sportCenter.SportCenter;
import org.zavrsni.backend.sportCenter.SportCenterRepository;
import org.zavrsni.backend.sportCenter.dto.SportCenterReservationsDTO;
import org.zavrsni.backend.status.Status;
import org.zavrsni.backend.status.StatusRepository;
import org.zavrsni.backend.user.User;

import java.sql.Time;
import java.text.SimpleDateFormat;
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
    private final FieldRepository fieldRepository;
    private final StatusRepository statusRepository;
    private final EntityStatusRepository entityStatusRepository;

    @Override
    public List<ReservationDTO> getReservations(String dateString, Long fieldId) {
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

        Field field = fieldRepository.findById(fieldId).orElseThrow(
                () -> new IllegalArgumentException("Field not found"));

        List<Reservation> reservations = reservationRepository.findAllByField(field);

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
    public SportCenterReservationsDTO getSportCenterFields(Long sportCenterId, String sport) {
        SportCenter sportCenter = sportCenterRepository.findById(sportCenterId).orElseThrow(
                () -> new IllegalArgumentException("Sport center not found"));

        List<FieldDetailsDTO> fields = sportCenter.getFields().stream().filter(field -> {
            List<EntityStatus> fieldStatuses = field.getFieldStatuses();
            String statusType = fieldStatuses.get(fieldStatuses.size() - 1).getStatus().getStatusType();
            String Fieldsport = field.getSport().getSportName();
            return statusType.equals("ACTIVE") && Fieldsport.equals(sport);
        }).map(field -> {
            List<Image> images = imageRepository.findAllByField(field);
            List<FieldAvailability> fieldAvailabilities = fieldAvailabilityRepository.findAllByField(field);
            return new FieldDetailsDTO(field, images, fieldAvailabilities);
        }).toList();

        return new SportCenterReservationsDTO(sportCenter, sport, fields);
    }

    @Override
    public AddReservationDTO addReservation(AddReservationDTO addReservationDTO) {
        Field field = fieldRepository.findById(addReservationDTO.getFieldId()).orElseThrow(
                () -> new IllegalArgumentException("Field not found"));
        Time startTime = Time.valueOf(addReservationDTO.getStartTime() + ":00");
        Time endTime = Time.valueOf(addReservationDTO.getEndTime() + ":00");

        Reservation reservation = Reservation.builder()
                .date(addReservationDTO.getDate())
                .startTime(startTime)
                .endTime(endTime)
                .field(field)
                .user((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .build();
        reservationRepository.save(reservation);

        Status status = statusRepository.findByStatusType("ACTIVE").orElseThrow();
        EntityStatus entityStatus = EntityStatus.builder()
                .status(status)
                .statusComment(addReservationDTO.getMessage())
                .reservation(reservation)
                .build();

        entityStatusRepository.save(entityStatus);
        return addReservationDTO;
    }
}
