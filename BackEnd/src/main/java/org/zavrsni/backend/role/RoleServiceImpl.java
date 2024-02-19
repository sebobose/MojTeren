package org.zavrsni.backend.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    @Override
    public void createRole(String name) {
        Role role = new Role();
        role.setName(name);
        roleRepository.save(role);
    }
}
