package org.zavrsni.backend.user;

import org.zavrsni.backend.user.dto.UserDTO;
import java.util.List;

public interface UserService {
    void createAdmin();

    List<UserDTO> getAllUsers();
}
