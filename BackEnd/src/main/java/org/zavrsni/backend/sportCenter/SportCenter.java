package org.zavrsni.backend.sportCenter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.address.Address;
import org.zavrsni.backend.entityStatus.EntityStatus;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.user.User;

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

    private String location;

    @ManyToOne
    private Address address;

    @OneToMany(mappedBy = "sportCenter", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Field> fields;

    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "sportCenter")
    private List<Image> images;

    @OneToMany(mappedBy = "sportCenter")
    private List<EntityStatus> sportCenterStatuses;
}
