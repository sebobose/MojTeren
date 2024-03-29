package org.zavrsni.backend.sportCenter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.image.ImageRepository;
import org.zavrsni.backend.sportCenter.dto.AddSportCenterDTO;
import org.zavrsni.backend.sportCenter.dto.SportCenterDetailsDTO;
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

        @Override
        @SneakyThrows
        public Void addSportCenter(AddSportCenterDTO addSportCenterDTO) {
            System.out.println(addSportCenterDTO.toString());
            List<byte[]> compressedImages = new ArrayList<>();
            for(MultipartFile image : addSportCenterDTO.getImages()){
                compressedImages.add(compressImage(image));
            }
            User owner = userRepository.findByEmail(addSportCenterDTO.getEmail()).orElseThrow();
            List<Image> savedImages = compressedImages.stream().map(Image::new)
                    .map(imageRepository::save).collect(Collectors.toList());
            SportCenter sportCenter = SportCenter.builder()
                    .owner(owner)
                    .sportCenterName(addSportCenterDTO.getSportCenterName())
                    .address(addSportCenterDTO.getAddress())
                    .images(savedImages)
                    .build();
            sportCenterRepository.save(sportCenter);
            return null;
        }

    @Override
    public List<SportCenterDetailsDTO> getAllSportCentersAdmin() {
        return sportCenterRepository.findAll().stream().map(SportCenterDetailsDTO::new).collect(Collectors.toList());
    }

    @SneakyThrows
    private static byte[] compressImage(MultipartFile image) {
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
