package com.spotroute.repository;

import com.spotroute.persistence.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, String> {
    Optional<DriverProfile> findByUserId(String userId);
}
