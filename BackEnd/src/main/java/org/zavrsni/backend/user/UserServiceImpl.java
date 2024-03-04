package org.zavrsni.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.role.Role;
import org.zavrsni.backend.role.RoleRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public void createAdmin() {
        if (!userRepository.existsByEmail("admin@admin")) {
            Role role = roleRepository.findByRoleName("ADMIN");
            User admin = new User();
            admin.setEmail("admin@admin");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setContactNumber("123456789");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(role);
            userRepository.save(admin);
        }
    }
}
