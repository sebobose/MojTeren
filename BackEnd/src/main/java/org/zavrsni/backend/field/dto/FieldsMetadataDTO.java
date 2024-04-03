package org.zavrsni.backend.field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldsMetadataDTO {

    private String fieldName;
    private Long fieldId;

    public FieldsMetadataDTO(Field field) {
        this.fieldName = field.getFieldName();
        this.fieldId = field.getFieldId();
    }
}
