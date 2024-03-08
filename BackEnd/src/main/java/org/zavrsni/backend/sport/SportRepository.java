package org.zavrsni.backend.sport;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Long> {

    Sport findBySportName(String sportName);
}
