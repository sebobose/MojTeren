package org.zavrsni.backend.sport;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Sport {

    @Id
    @GeneratedValue
    private Long sportId = 0L;

    @NotNull
    private String sportName;

    @OneToMany(mappedBy = "sport")
    private List<Field> fields;
}
