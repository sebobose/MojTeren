package org.zavrsni.backend.fieldAvailability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zavrsni.backend.field.Field;

import java.util.List;

public interface FieldAvailabilityRepository extends JpaRepository<FieldAvailability, Long> {
    List<FieldAvailability> findAllByField_FieldId(Long fieldId);

    List<FieldAvailability> findAllByField(Field field);
}
