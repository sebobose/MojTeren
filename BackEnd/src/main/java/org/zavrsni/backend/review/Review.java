package org.zavrsni.backend.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.field.Field;
import org.zavrsni.backend.user.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue
    private Long reviewId = 0L;

    @NotNull
    private Long rating;

    @NotNull
    private String reviewComment;

    @ManyToOne
    private User user;

    @ManyToOne
    private Field field;
}
