package org.zavrsni.backend.user.dto;

import lombok.Data;
import org.zavrsni.backend.user.User;

@Data
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private String role;

    public UserDTO(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.contact = user.getContactNumber();
        this.role = user.getRole().getRoleName();
    }
}
