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
import org.zavrsni.backend.reservation.dto.UserReservationDTO;
import org.zavrsni.backend.sportCenter.SportCenter;
import org.zavrsni.backend.sportCenter.SportCenterRepository;
import org.zavrsni.backend.sportCenter.dto.SportCenterReservationsDTO;
import org.zavrsni.backend.status.Status;
import org.zavrsni.backend.status.StatusRepository;
import org.zavrsni.backend.user.User;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
            String FieldSport = field.getSport().getSportName();
            return statusType.equals("ACTIVE") && FieldSport.equals(sport);
        }).map(field -> {
            List<Image> images = imageRepository.findAllByField(field);
            List<FieldAvailability> fieldAvailabilities = fieldAvailabilityRepository.findAllByField(field);
            return new FieldDetailsDTO(field, images, fieldAvailabilities);
        }).toList();

        return new SportCenterReservationsDTO(sportCenter, sport, fields);
    }

//    TODO: Check if reservation is in working hours
    @Override
    public AddReservationDTO addReservation(AddReservationDTO addReservationDTO) {
        Field field = fieldRepository.findById(addReservationDTO.getFieldId()).orElseThrow(
                () -> new IllegalArgumentException("Field not found"));
        Time startTime = Time.valueOf(addReservationDTO.getStartTime() + ":00");
        Time endTime = Time.valueOf(addReservationDTO.getEndTime() + ":00");

        LocalDateTime dateNow = LocalDateTime.now();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(addReservationDTO.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        LocalDateTime startDate = LocalDateTime.of(year, month, day, startTime.getHours(), startTime.getMinutes());
        LocalDateTime endDate = LocalDateTime.of(year, month, day, endTime.getHours(), endTime.getMinutes());
        if (startDate.isBefore(dateNow) || endDate.isBefore(dateNow)){
            throw new IllegalArgumentException("Invalid date and time, cannot reserve in the past");
        }
        if (startTime.after(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Invalid time range");
        }

        List<Reservation> sameDayReservation = reservationRepository.findAllByFieldAndDate(field, addReservationDTO.getDate());
        for (Reservation reservation : sameDayReservation) {
            if (
                    ((startTime.before(reservation.getEndTime()) && startTime.after(reservation.getStartTime())) ||
                    (endTime.before(reservation.getEndTime()) && endTime.after(reservation.getStartTime())) ||
                    startTime.equals(reservation.getStartTime()) ||
                    endTime.equals(reservation.getEndTime()) ||
                    (startTime.before(reservation.getStartTime()) && endTime.after(reservation.getEndTime()))) &&
                            (reservation.getReservationStatuses().get(reservation.getReservationStatuses().size() - 1).
                                    getStatus().getStatusType().equals("ACTIVE"))
            ) {
                throw new IllegalArgumentException("Field is already reserved at that time");
            }
        }

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

    @Override
    public List<UserReservationDTO> getUserReservations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Reservation> reservations = reservationRepository.findAllByUser(user);
        for (Reservation reservation: reservations) {
            if (reservation.getDate().before(new Date(System.currentTimeMillis()))) {
                List<EntityStatus> reservationStatuses = reservation.getReservationStatuses();
                EntityStatus lastStatus = reservationStatuses.get(reservationStatuses.size() - 1);
                if (lastStatus.getStatus().getStatusType().equals("ACTIVE")) {
                    EntityStatus newStatus = EntityStatus.builder()
                            .status(statusRepository.findByStatusType("FINISHED").orElseThrow())
                            .reservation(reservation)
                            .build();
                    reservation.getReservationStatuses().add(newStatus);
                    entityStatusRepository.save(newStatus);
                }
            }
        }

        return reservations.stream().map(reservation -> {
            List<EntityStatus> reservationStatuses = reservation.getReservationStatuses();
            return new UserReservationDTO(reservation, reservationStatuses.get(reservationStatuses.size() - 1));
        }).toList();
    }

    @Override
    public void cancelReservation(Long reservationId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new IllegalArgumentException("Reservation not found"));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!reservation.getUser().getEmail().equals(user.getEmail())) {
            throw new IllegalArgumentException("You are not authorized to cancel this reservation");
        }
        List<EntityStatus> reservationStatuses = reservation.getReservationStatuses();
        EntityStatus lastStatus = reservationStatuses.get(reservationStatuses.size() - 1);
        if (lastStatus.getStatus().getStatusType().equals("ACTIVE")) {
            EntityStatus newStatus = EntityStatus.builder()
                    .status(statusRepository.findByStatusType("INACTIVE").orElseThrow())
                    .statusComment(reason)
                    .reservation(reservation)
                    .build();
            entityStatusRepository.save(newStatus);
        }
    }
}
