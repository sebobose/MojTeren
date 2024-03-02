package org.zavrsni.backend.role;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zavrsni.backend.user.User;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue
    private Long roleId = 0L;

    @NotNull
    @Size(max = 20)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<User> users;

}
