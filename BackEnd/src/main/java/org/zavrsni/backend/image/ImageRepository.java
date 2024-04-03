package org.zavrsni.backend.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllBySportCenter_SportCenterId(Long sportCenterId);

    List<Image> findAllByField_FieldId(Long fieldId);
}
