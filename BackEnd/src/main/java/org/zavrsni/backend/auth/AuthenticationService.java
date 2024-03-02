package org.zavrsni.backend.auth;

import org.springframework.http.ResponseEntity;
import org.zavrsni.backend.auth.dto.LoginDto;
import org.zavrsni.backend.auth.dto.RegisterDto;

import java.util.Map;

public interface AuthenticationService {

    ResponseEntity<Void> register(RegisterDto request);

    ResponseEntity<Map<String, String>> authenticate(LoginDto request);

}
