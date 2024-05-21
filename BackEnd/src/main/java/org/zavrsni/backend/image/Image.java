package org.zavrsni.backend.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.sportCenter.SportCenter;

import java.sql.Types;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long imageId = 0L;

    @ManyToOne
    private Field field;

    @JdbcTypeCode(Types.VARBINARY)
    private byte[] image;

    @ManyToOne(cascade = CascadeType.ALL)
    private SportCenter sportCenter;

    public Image(byte[] image, SportCenter sportCenter) {
        this.image = image;
        this.sportCenter = sportCenter;
    }

    public Image(byte[] image, Field field) {
        this.image = image;
        this.field = field;
    }
}
