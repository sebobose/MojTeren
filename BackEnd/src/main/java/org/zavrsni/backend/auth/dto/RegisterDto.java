package org.zavrsni.backend.auth.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RegisterDto {
    private String email;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String password;
    private String role;
}
