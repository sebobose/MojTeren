package org.zavrsni.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zavrsni.backend.role.RoleService;

@Component
public class DataInitializer {

    private final RoleService roleService;
    private final String[] roles = {"admin", "athlete", "fieldOwner"};

    public DataInitializer(RoleService roleService) {
        this.roleService = roleService;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        for (String role : roles) {
            roleService.createRole(role);
        }

    }
}
