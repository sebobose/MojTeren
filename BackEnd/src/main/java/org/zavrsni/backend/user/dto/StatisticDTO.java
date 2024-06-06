package org.zavrsni.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.sportCenter.SportCenter;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDTO {
    private String sportCenter;
    private List<String> fields;

    public StatisticDTO(SportCenter sportCenter) {
        this.sportCenter = sportCenter.getSportCenterName();
        this.fields = sportCenter.getFields().stream().map(Field::getFieldName).toList();
    }
}
