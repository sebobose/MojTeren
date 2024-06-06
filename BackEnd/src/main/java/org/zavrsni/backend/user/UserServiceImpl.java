package org.zavrsni.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.reservation.Reservation;
import org.zavrsni.backend.role.Role;
import org.zavrsni.backend.role.RoleRepository;
import org.zavrsni.backend.sportCenter.SportCenter;
import org.zavrsni.backend.sportCenter.SportCenterRepository;
import org.zavrsni.backend.user.dto.FilteredStatisticsDTO;
import org.zavrsni.backend.user.dto.GetStatisticDTO;
import org.zavrsni.backend.user.dto.StatisticDTO;
import org.zavrsni.backend.user.dto.UserDTO;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SportCenterRepository sportCenterRepository;

    public void createAdmin() {
        if (!userRepository.existsByEmail("admin@admin.com")) {
            Role role = roleRepository.findByRoleName("ADMIN");
            User admin = new User();
            admin.setEmail("admin@admin.com");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setContactNumber("123456789");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(role);
            userRepository.save(admin);
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().filter(user -> !user.getRole().getRoleName().equals("ADMIN")).map(UserDTO::new).toList();
    }

    @Override
    public ResponseEntity<Map<String, String>> checkToken() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(Map.of("role", user.getRole().getRoleName(),
                "email", user.getEmail()));
    }

    @Override
    public ResponseEntity<UserDTO> getProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new UserDTO(user));
    }

    @Override
    public ResponseEntity<Void> editProfile(UserDTO userDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setContactNumber(userDTO.getContact());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Override
    public List<StatisticDTO> getSportCenterAndFields() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return sportCenterRepository.findAllByOwner(user).stream().map(StatisticDTO::new).toList();
    }

    @Override
    public FilteredStatisticsDTO getFilteredStatistics(GetStatisticDTO getStatisticDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SportCenter> sportCenters = sportCenterRepository.findAllByOwner(user);
        List<Field> fields;
        List<Reservation> reservations = new ArrayList<>();
        String chosenField = "";
        String bestField = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String creationDate = formatter.format(user.getCreatedTS());

        if (getStatisticDTO.getSportCenters().contains("Svi") || getStatisticDTO.getSportCenters().size() > 1) {
            if (!getStatisticDTO.getSportCenters().contains("Svi")) {
                sportCenters = sportCenters.stream().filter(sportCenter -> getStatisticDTO.getSportCenters().contains(sportCenter.getSportCenterName()))
                        .collect(Collectors.toList());
            }
            fields = sportCenters.stream().flatMap(sportCenter -> sportCenter.getFields().stream()).toList();
            reservations = fields.stream().flatMap(field -> field.getReservations().stream()).toList();
            chosenField = this.findMostPopularSportCenter(sportCenters, getStatisticDTO.getPeriod(), getStatisticDTO.getDate());
            bestField = this.findMostPopularField(fields, getStatisticDTO.getPeriod(), getStatisticDTO.getDate());
        }
        else if (getStatisticDTO.getSportCenters().size() == 1 && (getStatisticDTO.getFields().size() > 1 || getStatisticDTO.getFields().contains("Svi"))) {
            SportCenter sportCenter = sportCenters.stream().filter(sportCenter1 -> sportCenter1.getSportCenterName().equals(getStatisticDTO.getSportCenters().get(0)))
                    .findFirst().orElseThrow();
            fields = sportCenter.getFields();
            if (!getStatisticDTO.getFields().contains("Svi")) {
                fields = fields.stream().filter(field -> getStatisticDTO.getFields().contains(field.getFieldName()))
                        .toList();
            }
            reservations = fields.stream().flatMap(field -> field.getReservations().stream()).toList();
            chosenField = sportCenter.getSportCenterName();
            bestField = this.findMostPopularField(fields, getStatisticDTO.getPeriod(), getStatisticDTO.getDate());
            List<EntityStatus> statuses = sportCenter.getSportCenterStatuses();
            creationDate = formatter.format(statuses.get(0).getStatusChangeTS());
        }
        else if (getStatisticDTO.getFields().size() == 1) {
            Field field = sportCenters.stream().filter(sportCenter -> getStatisticDTO.getSportCenters().contains(sportCenter.getSportCenterName()))
                    .flatMap(sportCenter -> sportCenter.getFields().stream())
                    .filter(field1 -> field1.getFieldName().equals(getStatisticDTO.getFields().get(0)))
                    .findFirst().orElseThrow();
            reservations = field.getReservations();
            chosenField = field.getFieldName();
            List<EntityStatus> statuses = field.getFieldStatuses();
            creationDate = formatter.format(statuses.get(0).getStatusChangeTS());
        }
        return FilteredStatisticsDTO.builder()
                .chosenField(chosenField)
                .bestField(bestField)
                .sport(this.findMostPopularSport(reservations, getStatisticDTO.getPeriod(), getStatisticDTO.getDate()))
                .activeReservations(this.findActiveReservations(reservations, getStatisticDTO.getPeriod(), getStatisticDTO.getDate()))
                .canceledReservations(this.findCanceledReservations(reservations, getStatisticDTO.getPeriod(), getStatisticDTO.getDate()))
                .finishedReservations(this.findFinishedReservations(reservations, getStatisticDTO.getPeriod(), getStatisticDTO.getDate()))
                .creationDate(Date.valueOf(creationDate))
                .averageReservationTime(this.findAverageReservationTime(reservations, getStatisticDTO.getPeriod(), getStatisticDTO.getDate()))
                .income(this.findIncome(reservations, getStatisticDTO.getPeriod(), getStatisticDTO.getDate()))
                .build();
    }



    private String findMostPopularField(List<Field> fields, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return fields.stream()
                         .max(Comparator.comparingInt(field -> field.getReservations().size())).orElseThrow().getFieldName();
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return fields.stream().max(Comparator.comparingInt(field -> field.getReservations().stream()
                                .filter(reservation -> {
                                    Calendar reservationDate = Calendar.getInstance();
                                    reservationDate.setTime(reservation.getDate());
                                    List<EntityStatus> statuses = reservation.getReservationStatuses();
                                    String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                                    return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                            reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                            (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                                }).mapToInt(reservation -> 1).sum())).orElseThrow().getFieldName();
            }
            case "Tjedan" -> {
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return fields.stream().max(Comparator.comparingInt(field -> field.getReservations().stream()
                                .filter(reservation -> {
                                    Calendar reservationDate = Calendar.getInstance();
                                    reservationDate.setTime(reservation.getDate());
                                    List<EntityStatus> statuses = reservation.getReservationStatuses();
                                    String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                                    return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                            reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                            (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                                }).mapToInt(reservation -> 1).sum())).orElseThrow().getFieldName();
            }
            default -> {
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return fields.stream().max(Comparator.comparingInt(field -> field.getReservations().stream()
                                .filter(reservation -> {
                                    Calendar reservationDate = Calendar.getInstance();
                                    reservationDate.setTime(reservation.getDate());
                                    List<EntityStatus> statuses = reservation.getReservationStatuses();
                                    String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                                    return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                            reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                            (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                                }).mapToInt(reservation -> 1).sum())).orElseThrow().getFieldName();
            }
        }
    }

    private String findMostPopularSport(List<Reservation> reservations, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return reservations.stream()
                        .collect(Collectors.groupingBy(reservation -> reservation.getField().getSport().getSportName(), Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                    (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                        })
                        .collect(Collectors.groupingBy(reservation -> reservation.getField().getSport().getSportName(), Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            }
            case "Tjedan" -> {
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                    (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                        })
                        .collect(Collectors.groupingBy(reservation -> reservation.getField().getSport().getSportName(), Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            }
            default -> {
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                    (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                        })
                        .collect(Collectors.groupingBy(reservation -> reservation.getField().getSport().getSportName(), Collectors.counting()))
                        .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
            }
        }
    }

    private Long findActiveReservations(List<Reservation> reservations, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            return statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("ACTIVE");
                        })
                        .count();
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                    statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("ACTIVE");
                        })
                        .count();
            }
            case "Tjedan" -> {
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                    statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("ACTIVE");
                        })
                        .count();
            }
            default -> {
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                    statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("ACTIVE");
                        })
                        .count();
            }
        }
    }

    private Long findCanceledReservations(List<Reservation> reservations, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            return statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("CANCELED");
                        })
                        .count();
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                    statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("CANCELED");
                        })
                        .count();
            }
            case "Tjedan" -> {
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                    statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("CANCELED");
                        })
                        .count();
            }
            default -> {
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                    statuses.get(statuses.size() - 1).getStatus().getStatusType().equals("CANCELED");
                        })
                        .count();
            }
        }

    }

    private Long findFinishedReservations(List<Reservation> reservations, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return lastStatus.equals("FINISHED");
                        })
                        .count();
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                    lastStatus.equals("FINISHED");
                        })
                        .count();
            }
            case "Tjedan" -> {
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                    lastStatus.equals("FINISHED");
                        })
                        .count();
            }
            default -> {
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                    lastStatus.equals("FINISHED");
                        })
                        .count();
            }
        }
    }

    private Double findAverageReservationTime(List<Reservation> reservations, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return lastStatus.equals("FINISHED") || lastStatus.equals("ACTIVE");
                        })
                        .mapToDouble(reservation -> (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / 1000.0 / 60.0)
                        .average().orElse(0);
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                    (lastStatus.equals("FINISHED") || lastStatus.equals("ACTIVE"));
                        })
                        .mapToDouble(reservation -> (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / 1000.0 / 60.0)
                        .average().orElse(0);
            }
            case "Tjedan" -> {
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                    (lastStatus.equals("FINISHED") || lastStatus.equals("ACTIVE"));
                        })
                        .mapToDouble(reservation -> (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / 1000.0 / 60.0)
                        .average().orElse(0);
            }
            default -> {
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                    (lastStatus.equals("FINISHED") || lastStatus.equals("ACTIVE"));
                        })
                        .mapToDouble(reservation -> (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / 1000.0 / 60.0)
                        .average().orElse(0);
            }
        }
    }

    private Double findIncome(List<Reservation> reservations, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return lastStatus.equals("FINISHED");
                        })
                        .mapToDouble(reservation -> {
                            double reservationLength = (double) (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / (1000 * 60);
                            return reservationLength / reservation.getField().getMinResTime() * reservation.getField().getPrice();
                        }).sum();
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                    lastStatus.equals("FINISHED");
                        })
                        .mapToDouble(reservation -> {
                            double reservationLength = (double) (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / (1000 * 60);
                            return reservationLength / reservation.getField().getMinResTime() * reservation.getField().getPrice();
                        }).sum();
            }
            case "Tjedan" -> {
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                    lastStatus.equals("FINISHED");
                        })
                        .mapToDouble(reservation -> {
                            double reservationLength = (double) (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / (1000 * 60);
                            return reservationLength / reservation.getField().getMinResTime() * reservation.getField().getPrice();
                        }).sum();
            }
            default -> {
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                int selectedYear = date.get(Calendar.YEAR);
                return reservations.stream()
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                    lastStatus.equals("FINISHED");
                        })
                        .mapToDouble(reservation -> {
                            double reservationLength = (double) (reservation.getEndTime().getTime() - reservation.getStartTime().getTime()) / (1000 * 60);
                            return reservationLength / reservation.getField().getMinResTime() * reservation.getField().getPrice();
                        }).sum();
            }
        }
    }

    private String findMostPopularSportCenter(List<SportCenter> sportCenters, String period, Calendar date) {
        switch (period) {
            case "Bilo kad" -> {
                return sportCenters.stream().max(Comparator.comparingInt(sportCenter -> sportCenter.getFields().stream()
                        .flatMap(field -> field.getReservations().stream())
                        .filter(reservation -> {
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED");
                        }).mapToInt(reservation -> 1).sum())).orElseThrow().getSportCenterName();
            }
            case "Mjesec" -> {
                int selectedMonth = date.get(Calendar.MONTH);
                int selectedYear = date.get(Calendar.YEAR);
                return sportCenters.stream().max(Comparator.comparingInt(sportCenter -> sportCenter.getFields().stream()
                        .flatMap(field -> field.getReservations().stream())
                        .filter(reservation -> {
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.MONTH) == selectedMonth &&
                                    (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                        }).mapToInt(reservation -> 1).sum())).orElseThrow().getSportCenterName();
            }
            case "Tjedan" -> {
                int selectedYear = date.get(Calendar.YEAR);
                int selectedWeek = date.get(Calendar.WEEK_OF_YEAR);
                return sportCenters.stream().max(Comparator.comparingInt(sportCenter -> sportCenter.getFields().stream()
                        .flatMap(field -> field.getReservations().stream())
                        .filter(reservation -> {
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.WEEK_OF_YEAR) == selectedWeek &&
                                    (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                        }).mapToInt(reservation -> 1).sum())).orElseThrow().getSportCenterName();
            }
            default -> {
                int selectedYear = date.get(Calendar.YEAR);
                int selectedDay = date.get(Calendar.DAY_OF_YEAR);
                return sportCenters.stream().max(Comparator.comparingInt(sportCenter -> sportCenter.getFields().stream()
                        .flatMap(field -> field.getReservations().stream())
                        .filter(reservation -> {
                            Calendar reservationDate = Calendar.getInstance();
                            reservationDate.setTime(reservation.getDate());
                            List<EntityStatus> statuses = reservation.getReservationStatuses();
                            String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                            return reservationDate.get(Calendar.YEAR) == selectedYear &&
                                    reservationDate.get(Calendar.DAY_OF_YEAR) == selectedDay &&
                                    (lastStatus.equals("ACTIVE") || lastStatus.equals("FINISHED"));
                        }).mapToInt(reservation -> 1).sum())).orElseThrow().getSportCenterName();
            }
        }
    }
}
