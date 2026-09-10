package com.spotroute.repository;

import com.spotroute.persistence.entity.Ride;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {

    @Query("SELECT r FROM Ride r WHERE r.status = 'SCHEDULED' AND r.departureTime > :now ORDER BY r.departureTime ASC")
    List<Ride> findAvailableRides(LocalDateTime now);
    List<Ride> findByDriverIdOrderByDepartureTimeDesc(String driverId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("" +
            "SELECT r FROM Ride r WHERE r.id = :id" +
            "")
    Optional<Ride> findByIdForUpdate(@Param("id") String id);
}
