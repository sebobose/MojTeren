package org.zavrsni.backend;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.zavrsni.backend.role.RoleService;
import org.zavrsni.backend.sport.SportService;
import org.zavrsni.backend.status.StatusService;
import org.zavrsni.backend.user.UserService;

@Component
public class DataInitializer {

    private final RoleService roleService;
    private final SportService sportService;
    private final StatusService statusService;
    private final UserService userService;

    private final String[] roles = {"ADMIN", "ATHLETE", "FIELD_OWNER"};
    private final String[] sports = {"Nogomet", "Košarka", "Rukomet", "Tenis", "Stolni tenis", "Badminton",
            "Odbojka", "Američki nogomet", "Hokej", "Vaterpolo"};

    private final String[] statuses = {"PENDING", "ACTIVE", "REJECTED", "INACTIVE"};

    public DataInitializer(RoleService roleService, SportService sportService, StatusService statusService, UserService userService) {
        this.roleService = roleService;
        this.sportService = sportService;
        this.statusService = statusService;
        this.userService = userService;
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
        userService.createAdmin();
    }
}
