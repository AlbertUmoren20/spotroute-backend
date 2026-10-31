package com.spotroute.repository;

import com.spotroute.core.enums.ApplicationStatus;
import com.spotroute.persistence.entity.DriverProfile;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, String> {

        DriverProfile findByUserId(String id);
        boolean existsByUserId(String userId);
//
//        @EntityGraph(attributePaths = "documents")
//        Optional<DriverProfile> findByUserId(Long userId);

        @Query("select p.applicationStatus from DriverProfile p where p.user.id = :userId")
        Optional<ApplicationStatus> findStatusByUserId(@Param("userId") Long userId);

        /** Row lock so concurrent re-uploads/reviews cannot recalculate from stale document state. */
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select p from DriverProfile p where p.user.id = :userId")
        Optional<DriverProfile> findByUserIdForUpdate(@Param("userId") Long userId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select p from DriverProfile p where p.id = " +
                "(select d.driverProfile.id from DriverDocument d where d.id = :documentId)")
        Optional<DriverProfile> findByDocumentIdForUpdate(@Param("documentId") Long documentId);

    List<DriverProfile> findAllByOrderByCreatedAtAsc();

    List<DriverProfile> findAllByApplicationStatusOrderByCreatedAtAsc(ApplicationStatus status);
}

