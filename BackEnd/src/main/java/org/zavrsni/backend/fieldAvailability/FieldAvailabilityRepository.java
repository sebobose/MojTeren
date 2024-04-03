package org.zavrsni.backend.fieldAvailability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldAvailabilityRepository extends JpaRepository<FieldAvailability, Long> {
    List<FieldAvailability> findAllByField_FieldId(Long fieldId);
}
