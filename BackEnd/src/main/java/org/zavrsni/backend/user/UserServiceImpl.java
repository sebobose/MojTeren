package org.zavrsni.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.role.Role;
import org.zavrsni.backend.role.RoleRepository;
import org.zavrsni.backend.user.dto.UserDTO;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void createAdmin() {
        if (!userRepository.existsByEmail("admin@admin.com")) {
            Role role = roleRepository.findByRoleName("ADMIN");
            User admin = new User();
            admin.setEmail("admin@admin.com");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setContactNumber("123456789");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(role);
            userRepository.save(admin);
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().filter(user -> !user.getRole().getRoleName().equals("ADMIN")).map(UserDTO::new).toList();
    }

    @Override
    public ResponseEntity<Boolean> checkToken() {
        return ResponseEntity.ok(true);
    }
}
