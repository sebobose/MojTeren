package org.zavrsni.backend.field;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.zavrsni.backend.fieldAvailability.FieldAvailability;
import org.zavrsni.backend.fieldStatus.FieldStatus;
import org.zavrsni.backend.image.Image;
import org.zavrsni.backend.reservation.Reservation;
import org.zavrsni.backend.review.Review;
import org.zavrsni.backend.sport.Sport;
import org.zavrsni.backend.sportCenter.SportCenter;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Field {

    @Id
    @GeneratedValue
    private Long fieldId = 0L;

    @NotNull
    private String fieldName;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdTS;

    @NotNull
    private Long minResTime;

    @NotNull
    private Long timeSlot;

    @NotNull
    private String description;

    @OneToMany(mappedBy = "field")
    private List<Image> images;

    @OneToMany(mappedBy = "field")
    private List<FieldAvailability> fieldAvailabilities;

    @OneToMany(mappedBy = "field")
    private List<FieldStatus> fieldStatuses;

    @OneToMany(mappedBy = "field")
    private List<Review> reviews;

    @OneToMany(mappedBy = "field")
    private List<Reservation> reservations;

    @ManyToOne
    private SportCenter sportCenter;

    @ManyToOne
    private Sport sport;
}
