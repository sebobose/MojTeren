package org.zavrsni.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zavrsni.backend.auth.dto.LoginDto;
import org.zavrsni.backend.auth.dto.RegisterDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.role.Role;
import org.zavrsni.backend.role.RoleRepository;
import org.zavrsni.backend.security.JwtService;
import org.zavrsni.backend.user.User;
import org.zavrsni.backend.user.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @SneakyThrows
    public ResponseEntity<Void> register(RegisterDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            System.out.println("User with email " + request.getEmail() + " already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Role role = roleRepository.findByName(request.getRole());
        if (role == null) {
            System.out.println("Role " + request.getRole() + " does not exist");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User user = new User(request, role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        System.out.println(user);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Map<String, String>> authenticate(LoginDto request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        System.out.println("login " + request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("accessToken", jwtToken, "role", user.getRole().getName()));
    }

}
