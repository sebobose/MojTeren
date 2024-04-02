package org.zavrsni.backend.field;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.entityStatus.EntityStatusRepository;
import org.zavrsni.backend.field.dto.AddFieldDTO;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;
import org.zavrsni.backend.fieldAvailability.FieldAvailabilityRepository;
import org.zavrsni.backend.fieldAvailability.dto.FieldAvailabilityDTO;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.image.ImageRepository;
import org.zavrsni.backend.sport.Sport;
import org.zavrsni.backend.sport.SportRepository;
import org.zavrsni.backend.sportCenter.SportCenter;
import org.zavrsni.backend.sportCenter.SportCenterRepository;
import org.zavrsni.backend.status.Status;
import org.zavrsni.backend.status.StatusRepository;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.zavrsni.backend.sportCenter.SportCenterServiceImpl.compressImage;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final SportRepository sportRepository;
    private final SportCenterRepository sportCenterRepository;
    private final ImageRepository imageRepository;
    private final StatusRepository statusRepository;
    private final EntityStatusRepository entityStatusRepository;
    private final FieldAvailabilityRepository fieldAvailabilityRepository;

    @Override
    public Void addField(AddFieldDTO addFieldDTO) {
        List<byte[]> compressedImages = new ArrayList<>();
        for(MultipartFile image : addFieldDTO.getImages()){
            compressedImages.add(compressImage(image));
        }

        Sport sport = sportRepository.findById(addFieldDTO.getSportId()).orElseThrow();
        SportCenter sportCenter = sportCenterRepository.findById(addFieldDTO.getSportCenterId()).orElseThrow();
        Field field = Field.builder()
                .fieldName(addFieldDTO.getFieldName())
                .sport(sport)
                .sportCenter(sportCenter)
                .minResTime(addFieldDTO.getMinResTime())
                .timeSlot(addFieldDTO.getTimeSlot())
                .description(addFieldDTO.getDescription())
                .build();
        fieldRepository.save(field);

        compressedImages.stream().map(image -> new Image(image, field)).forEach(imageRepository::save);

        Status status = statusRepository.findByStatusType("ACTIVE").orElseThrow();
        EntityStatus entityStatus = EntityStatus.builder()
                .status(status)
                .field(field)
                .build();
        entityStatusRepository.save(entityStatus);

        for (FieldAvailabilityDTO fieldAvailabilityDTO : addFieldDTO.getFieldAvailabilities()) {
            FieldAvailability fieldAvailability = FieldAvailability.builder()
                    .field(field)
                    .dayOfWeek(fieldAvailabilityDTO.getDayOfWeek())
                    .startTime(Time.valueOf(fieldAvailabilityDTO.getStartTime() + ":00"))
                    .endTime(Time.valueOf(fieldAvailabilityDTO.getEndTime() + ":00"))
                    .build();
            fieldAvailabilityRepository.save(fieldAvailability);
        }
        return null;
    }
}
