package com.spotroute.repository;

import com.spotroute.persistence.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
