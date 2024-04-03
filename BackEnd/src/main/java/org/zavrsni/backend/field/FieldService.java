package org.zavrsni.backend.field;

import org.zavrsni.backend.field.dto.AddFieldDTO;
import org.zavrsni.backend.field.dto.FieldDetailsDTO;

public interface FieldService {
    Void addField(AddFieldDTO addFieldDTO);

    FieldDetailsDTO getField(Long fieldId);

    Void updateField(Long fieldId, AddFieldDTO addFieldDTO);

    Void deactivateField(Long fieldId, String reason);
}
