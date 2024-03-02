package org.zavrsni.backend.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.zavrsni.backend.auth.dto.RegisterDto;
import org.zavrsni.backend.reservation.Reservation;
import org.zavrsni.backend.review.Review;
import org.zavrsni.backend.role.Role;
import org.zavrsni.backend.sportCenter.SportCenter;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_User")
public class User implements UserDetails {

    @Id
    @Size(min = 3, max = 50)
    private String email;

    @Size(min = 8)
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String contactNumber;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdTS;

    @ManyToOne
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "owner")
    private List<SportCenter> sportCenters;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User(RegisterDto user, Role role) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.contactNumber = user.getContactNumber();
        this.role = role;
    }
}
