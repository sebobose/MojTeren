package org.zavrsni.backend;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zavrsni.backend.role.RoleService;
import org.zavrsni.backend.sport.SportService;
import org.zavrsni.backend.status.StatusService;

@Component
public class DataInitializer {

    private final RoleService roleService;
    private final SportService sportService;
    private final StatusService statusService;

    private final String[] roles = {"admin", "athlete", "fieldOwner"};
    private final String[] sports = {"Nogomet", "Košarka", "Rukomet", "Tenis", "Stolni tenis", "Badminton",
            "Odbojka", "Američki nogomet", "Hokej", "Vaterpolo"};

    private final String[] statuses = {"PENDING", "APPROVED", "REJECTED"};

    public DataInitializer(RoleService roleService, SportService sportService, StatusService statusService) {
        this.roleService = roleService;
        this.sportService = sportService;
        this.statusService = statusService;
    }

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        for (String role : roles) {
            roleService.createRole(role);
        }
        for (String sport : sports) {
            sportService.createSport(sport);
        }
        for (String status : statuses) {
            statusService.createStatus(status);
        }
    }
}
