package org.zavrsni.backend.sportCenter;

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
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.image.ImageRepository;
import org.zavrsni.backend.sportCenter.dto.AddSportCenterDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterDetailsDTO;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
    public List<SportCenterDetailsDTO> getAllSportCentersAdmin() {
        return sportCenterRepository.findAll().stream()
                .filter(sportCenter -> {
                    List<EntityStatus> statuses = sportCenter.getSportCenterStatuses();
                    return sportCenter.getSportCenterStatuses().get(statuses.size() - 1)
                            .getStatus().getStatusType().equals("ACTIVE");
                }).map(SportCenterDetailsDTO::new).collect(Collectors.toList());
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
        Address address = Address.builder()
                .streetAndNumber(addSportCenterDTO.getStreetAndNumber())
                .city(city)
                .build();
        addressRepository.save(address);

        SportCenter sportCenter = sportCenterRepository.findById(sportCenterId).orElseThrow();
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

}
