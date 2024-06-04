package org.zavrsni.backend.sportCenter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zavrsni.backend.address.Address;
import org.zavrsni.backend.address.AddressRepository;
import org.zavrsni.backend.city.City;
import org.zavrsni.backend.city.CityRepository;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.entityStatus.EntityStatusRepository;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.field.FieldService;
import org.zavrsni.backend.field.dto.FieldsMetadataDTO;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.image.ImageRepository;
import org.zavrsni.backend.reservation.Reservation;
import org.zavrsni.backend.sportCenter.dto.AddSportCenterDTO;
import org.zavrsni.backend.sportCenter.dto.FilteredSportCenterDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterDetailsDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterRequestDTO;
import org.zavrsni.backend.status.Status;
import org.zavrsni.backend.status.StatusRepository;
import org.zavrsni.backend.user.User;
import org.zavrsni.backend.user.UserRepository;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SportCenterServiceImpl implements SportCenterService {

        private final SportCenterRepository sportCenterRepository;
        private final UserRepository userRepository;
        private final ImageRepository imageRepository;
        private final StatusRepository statusRepository;
        private final EntityStatusRepository entityStatusRepository;
        private final FieldService fieldService;
        private final CityRepository cityRepository;
        private final AddressRepository addressRepository;

        @Override
        @SneakyThrows
        @Transactional
        public Void addSportCenter(AddSportCenterDTO addSportCenterDTO) {
            List<byte[]> compressedImages = new ArrayList<>();
            for(MultipartFile image : addSportCenterDTO.getImages()){
                compressedImages.add(compressImage(image));
            }

            City city = cityRepository.findByCityName(addSportCenterDTO.getCityName());
            if (city == null || !Objects.equals(city.getZipCode(), addSportCenterDTO.getZipCode())) {
                throw new Exception("City and zip code do not match");
            }
            Address address = Address.builder()
                    .streetAndNumber(addSportCenterDTO.getStreetAndNumber())
                    .city(city)
                    .longitude(addSportCenterDTO.getLongitude())
                    .latitude(addSportCenterDTO.getLatitude())
                    .build();
            addressRepository.save(address);

            User owner = userRepository.findByEmail(addSportCenterDTO.getEmail()).orElseThrow();
            SportCenter sportCenter = SportCenter.builder()
                    .owner(owner)
                    .sportCenterName(addSportCenterDTO.getSportCenterName())
                    .address(address)
                    .build();
            sportCenterRepository.save(sportCenter);

            compressedImages.stream().map(image -> new Image(image, sportCenter)).forEach(imageRepository::save);

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Status status = user.getRole().getRoleName().equals("ADMIN") ?
                    statusRepository.findByStatusType("ACTIVE").orElseThrow() :
                    statusRepository.findByStatusType("PENDING").orElseThrow();
            EntityStatus entityStatus = EntityStatus.builder()
                    .status(status)
                    .sportCenter(sportCenter)
                    .build();
            entityStatusRepository.save(entityStatus);
            return null;
        }

    @Override
    public List<SportCenterDetailsDTO> getAllOwnerSportCenters() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return sportCenterRepository.findAll().stream()
                .filter(sportCenter -> {
                    List<EntityStatus> statuses = sportCenter.getSportCenterStatuses();
                    if (user.getRole().getRoleName().equals("FIELD_OWNER")) {
                        String status = sportCenter.getSportCenterStatuses().get(statuses.size() - 1)
                                .getStatus().getStatusType();
                        return (sportCenter.getOwner().getEmail().equals(user.getEmail())) &&
                                (status.equals("ACTIVE") || status.equals("PENDING") || status.equals("REJECTED"));
                    }
                    return sportCenter.getSportCenterStatuses().get(statuses.size() - 1)
                            .getStatus().getStatusType().equals("ACTIVE") &&
                            user.getRole().getRoleName().equals("ADMIN");
                }).map(sportCenter -> {
                    String sport = "";
                    if (!sportCenter.getFields().isEmpty()) {
                        sport = sportCenter.getFields().get(0).getSport().getSportName();
                    }
                    return new SportCenterDetailsDTO(sportCenter, sport);
                }).collect(Collectors.toList());
    }

    @Override
    public SportCenterDetailsDTO getSportCenterById(Long sportCenterId) {
            List<Image> images = imageRepository.findAllBySportCenter_SportCenterId(sportCenterId);
            return new SportCenterDetailsDTO(sportCenterRepository.findById(sportCenterId).orElseThrow(), images);
    }

    @Override
    @SneakyThrows
    public Void updateSportCenter(Long sportCenterId, AddSportCenterDTO addSportCenterDTO) {
        List<byte[]> compressedImages = new ArrayList<>();
        for(MultipartFile image : addSportCenterDTO.getImages()){
            compressedImages.add(compressImage(image));
        }

        City city = cityRepository.findByCityName(addSportCenterDTO.getCityName());
        if (city == null || !Objects.equals(city.getZipCode(), addSportCenterDTO.getZipCode())) {
            throw new Exception("City and zip code do not match");
        }

        SportCenter sportCenter = sportCenterRepository.findById(sportCenterId).orElseThrow();
        Address address = sportCenter.getAddress();
        address.setStreetAndNumber(addSportCenterDTO.getStreetAndNumber());
        address.setCity(city);
        address.setLongitude(addSportCenterDTO.getLongitude());
        address.setLatitude(addSportCenterDTO.getLatitude());
        addressRepository.save(address);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Status status = user.getRole().getRoleName().equals("ADMIN") ?
                statusRepository.findByStatusType("ACTIVE").orElseThrow() :
                statusRepository.findByStatusType("PENDING").orElseThrow();
        EntityStatus entityStatus = EntityStatus.builder()
                .status(status)
                .sportCenter(sportCenter)
                .build();
        entityStatusRepository.save(entityStatus);

        List<Image> images = imageRepository.findAllBySportCenter_SportCenterId(sportCenterId);
        imageRepository.deleteAll(images);
        compressedImages.stream().map(image -> new Image(image, sportCenter)).forEach(imageRepository::save);
        sportCenter.setSportCenterName(addSportCenterDTO.getSportCenterName());
        sportCenter.setAddress(address);
        sportCenterRepository.save(sportCenter);
        return null;
    }

    @Override
    public Void deactivateSportCenter(Long sportCenterId, String reason) {
        SportCenter sportCenter = sportCenterRepository.findById(sportCenterId).orElseThrow();
        Status status = statusRepository.findByStatusType("INACTIVE").orElseThrow();
        List<Field> fields = sportCenter.getFields();
        fields.forEach(field -> fieldService.deactivateField(field.getFieldId(), "Sport center deactivated"));
        EntityStatus entityStatus = EntityStatus.builder()
                .status(status)
                .sportCenter(sportCenter)
                .statusComment(reason)
                .build();
        entityStatusRepository.save(entityStatus);
        return null;
    }

    @Override
    public List<FieldsMetadataDTO> getSportCenterFields(Long sportCenterId) {
        SportCenter sportCenter = sportCenterRepository.findById(sportCenterId).orElseThrow();
        return sportCenter.getFields().stream().filter(field -> {
            List<EntityStatus> statuses = field.getFieldStatuses();
            return field.getFieldStatuses().get(statuses.size() - 1)
                    .getStatus().getStatusType().equals("ACTIVE");
        }).map(FieldsMetadataDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<SportCenterDetailsDTO> getAllSportCenters(FilteredSportCenterDTO filteredSportCenterDTO) {
        List<SportCenter> sportCenters = sportCenterRepository.findAll();
        List<SportCenter> activeSportCenters = sportCenters.stream()
                .filter(sportCenter -> {
                    List<EntityStatus> statuses = sportCenter.getSportCenterStatuses();
                    return sportCenter.getSportCenterStatuses().get(statuses.size() - 1)
                            .getStatus().getStatusType().equals("ACTIVE");
                }).toList();

        List<SportCenter> distanceFilteredSportCenters = activeSportCenters.stream()
                .filter(sportCenter -> {
                    double distance = calculateDistance(Double.parseDouble(sportCenter.getAddress().getLatitude()),
                            Double.parseDouble(sportCenter.getAddress().getLongitude()),
                            Double.parseDouble(filteredSportCenterDTO.getLatitude()),
                            Double.parseDouble(filteredSportCenterDTO.getLongitude()));
                    return distance <= filteredSportCenterDTO.getDistance();
                }).toList();

        List<Field> fields = distanceFilteredSportCenters.stream()
                .map(SportCenter::getFields)
                .flatMap(List::stream)
                .toList();

        List<Field> sportFilteredFields = fields.stream()
                .filter(field -> {
                    List<EntityStatus> statuses = field.getFieldStatuses();
                    return field.getFieldStatuses().get(statuses.size() - 1)
                            .getStatus().getStatusType().equals("ACTIVE") &&
                            field.getSport().getSportName().equals(filteredSportCenterDTO.getSport());
                }).toList();

        if (filteredSportCenterDTO.getDate() == null) {
            List<SportCenter> sportCentersList = sportFilteredFields.stream().map(Field::getSportCenter).distinct().toList();
            return sportCentersList.stream().map(sportCenter -> {
                List<Image> images = imageRepository.findAllBySportCenter_SportCenterId(sportCenter.getSportCenterId());
                double distance = calculateDistance(Double.parseDouble(sportCenter.getAddress().getLatitude()),
                        Double.parseDouble(sportCenter.getAddress().getLongitude()),
                        Double.parseDouble(filteredSportCenterDTO.getLatitude()),
                        Double.parseDouble(filteredSportCenterDTO.getLongitude()));
                List<Field> fieldsList = sportFilteredFields.stream()
                        .filter(field -> field.getSportCenter().getSportCenterId().equals(sportCenter.getSportCenterId()))
                        .toList();
                return new SportCenterDetailsDTO(sportCenter, images, fieldsList, distance);
            }).collect(Collectors.toList());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(filteredSportCenterDTO.getDate());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeek = switch (day) {
            case Calendar.MONDAY -> "Monday";
            case Calendar.TUESDAY -> "Tuesday";
            case Calendar.WEDNESDAY -> "Wednesday";
            case Calendar.THURSDAY -> "Thursday";
            case Calendar.FRIDAY -> "Friday";
            case Calendar.SATURDAY -> "Saturday";
            case Calendar.SUNDAY -> "Sunday";
            default -> "";
        };

        List<Field> dayFilteredFields = sportFilteredFields.stream()
                .filter(field -> field.getFieldAvailabilities().stream()
                        .anyMatch(fieldAvailability -> fieldAvailability.getDayOfWeek().equals(dayOfWeek)))
                .toList();

        List<SportCenterDetailsDTO> result = new ArrayList<>();
        List<SportCenter> addedSportCenters = new ArrayList<>();
        for (Field field : dayFilteredFields) {
            if (addedSportCenters.contains(field.getSportCenter())) {
                continue;
            }
            List<Reservation> dateFilteredReservations = field.getReservations().stream().filter(
                    reservation -> {
                        List<EntityStatus> statuses = reservation.getReservationStatuses();
                        String lastStatus = statuses.get(statuses.size() - 1).getStatus().getStatusType();
                        return reservation.getDate().toString().equals(filteredSportCenterDTO.getDate().toString()) &&
                                lastStatus.equals("ACTIVE");
                    })
                    .sorted(Comparator.comparing(Reservation::getStartTime))
                    .collect(Collectors.toCollection(ArrayList::new));

            FieldAvailability fieldAvailability = field.getFieldAvailabilities().stream()
                    .filter(fieldAvailability1 -> fieldAvailability1.getDayOfWeek().equals(dayOfWeek))
                    .findFirst().orElseThrow();

            Reservation dayStart = Reservation.builder().endTime(fieldAvailability.getStartTime()).build();
            Reservation dayEnd = Reservation.builder().startTime(fieldAvailability.getEndTime()).build();
            dateFilteredReservations.add(0, dayStart);
            dateFilteredReservations.add(dayEnd);
            for (int i = 0; i < dateFilteredReservations.size() - 1; i++) {
                Reservation current = dateFilteredReservations.get(i);
                Reservation next = dateFilteredReservations.get(i + 1);
                long diffInMillies = next.getStartTime().getTime() - current.getEndTime().getTime();
                if (diffInMillies >= filteredSportCenterDTO.getReservationTime() * 60 * 1000) {
                    List<Image> images = imageRepository.findAllBySportCenter_SportCenterId(field.getSportCenter().getSportCenterId());
                    double distance = calculateDistance(Double.parseDouble(field.getSportCenter().getAddress().getLatitude()),
                            Double.parseDouble(field.getSportCenter().getAddress().getLongitude()),
                            Double.parseDouble(filteredSportCenterDTO.getLatitude()),
                            Double.parseDouble(filteredSportCenterDTO.getLongitude()));
                    List<Field> fieldsList = sportFilteredFields.stream()
                            .filter(field2 -> field2.getSportCenter().getSportCenterId().equals(field.getSportCenter().getSportCenterId()))
                            .toList();
                    result.add(new SportCenterDetailsDTO(field.getSportCenter(), images, fieldsList, field, distance));
                    addedSportCenters.add(field.getSportCenter());
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public List<SportCenterDetailsDTO> getSportCenterRequests() {
        return sportCenterRepository.findAll().stream()
                .filter(sportCenter -> {
                    List<EntityStatus> statuses = sportCenter.getSportCenterStatuses();
                    return sportCenter.getSportCenterStatuses().get(statuses.size() - 1)
                            .getStatus().getStatusType().equals("PENDING");
                }).map(sportCenter -> {
                    List<Image> images = imageRepository.findAllBySportCenter_SportCenterId(sportCenter.getSportCenterId());
                    return new SportCenterDetailsDTO(sportCenter, images);
                }).collect(Collectors.toList());
    }

    @Override
    public Void resolveSportCenterRequest(SportCenterRequestDTO sportCenterRequestDTO) {
        SportCenter sportCenter = sportCenterRepository.findById(sportCenterRequestDTO.getSportCenterId()).orElseThrow();
        Status status = statusRepository.findByStatusType(sportCenterRequestDTO.getDecision()).orElseThrow();
        EntityStatus entityStatus = EntityStatus.builder()
                .status(status)
                .sportCenter(sportCenter)
                .statusComment(sportCenterRequestDTO.getReason())
                .build();
        entityStatusRepository.save(entityStatus);
        return null;
    }

    @SneakyThrows
    public static byte[] compressImage(MultipartFile image) {
            if (image.getSize() <= 0.5 * 1024 * 1024) {
                image.getBytes();
            }
        final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB
        float compressionQuality = image.getSize() > MAX_SIZE ? 0.5f : 0.75f; // More compression if larger than 5MB

        // Read the MultipartFile into a BufferedImage
        InputStream inputFileStream = image.getInputStream();
        BufferedImage inputImage = ImageIO.read(inputFileStream);

        // Compress the image
        ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(Objects.requireNonNull(image.getContentType()).split("/")[1]);
        ImageWriter writer = writers.next();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(compressedOutputStream);
        writer.setOutput(imageOutputStream);
        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(compressionQuality); // Adjust the compression quality
        writer.write(null, new IIOImage(inputImage, null, null), params);
        writer.dispose();
        imageOutputStream.close();

        // Convert the compressed image to a byte array
        return compressedOutputStream.toByteArray();
    }

    public static double calculateDistance(double startLat, double startLong,
                                           double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c;
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
