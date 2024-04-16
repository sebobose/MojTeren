package org.zavrsni.backend.city;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

    City findByCityName(String cityName);
}
