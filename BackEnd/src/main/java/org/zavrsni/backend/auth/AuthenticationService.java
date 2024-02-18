package org.zavrsni.backend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.zavrsni.backend.auth.dto.LoginDto;
import org.zavrsni.backend.auth.dto.RegisterDto;

import java.io.IOException;
import java.util.Map;

public interface AuthenticationService {

    ResponseEntity<Void> register(RegisterDto request);

    ResponseEntity<Map<String, String>> authenticate(LoginDto request);

}
