package org.zavrsni.backend.sportCenter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zavrsni.backend.user.User;

import java.util.List;

public interface SportCenterRepository extends JpaRepository<SportCenter, Long> {

    List<SportCenter> findAllByOwner(User user);
}
