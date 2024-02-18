package org.zavrsni.backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.auth.dto.LoginDto;
import org.zavrsni.backend.auth.dto.RegisterDto;

import java.util.Map;

@RestController
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl service;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @ModelAttribute RegisterDto request
    ) {
        System.out.println("controller " + request);
        return service.register(request);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> authenticate(
            @RequestBody LoginDto request
    ) {
        System.out.println("controller " + request);
        return service.authenticate(request);
    }

}
