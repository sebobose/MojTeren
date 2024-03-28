package org.zavrsni.backend.sportCenter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.user.User;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SportCenter {

    @Id
    @GeneratedValue
    private Long sportCenterId = 0L;

    @NotNull
    private String sportCenterName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdTS;

    private String location;

    @NotNull
    private String address;

    @OneToMany(mappedBy = "sportCenter", cascade = CascadeType.ALL)
    private List<Field> fields;

    @ManyToOne
    private User owner;

    @OneToMany
    private List<Image> images;
}
