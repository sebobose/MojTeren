package org.zavrsni.backend.address;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.city.City;
import org.zavrsni.backend.sportCenter.SportCenter;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long addressId = 0L;

    @NotNull
    private String streetAndNumber;

    @ManyToOne
    private City city;

    @OneToMany(mappedBy = "address")
    private List<SportCenter> sportCenters;

}
