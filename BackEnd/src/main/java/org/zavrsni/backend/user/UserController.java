package org.zavrsni.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zavrsni.backend.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "${FRONTEND_API_URL}")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/check-token")
    public ResponseEntity<Map<String, String>> checkToken() {
        return userService.checkToken();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile() {
        return userService.getProfile();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> editProfile(@RequestBody UserDTO userDTO) {
        return userService.editProfile(userDTO);
    }
}
