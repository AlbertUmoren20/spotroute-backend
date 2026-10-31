package com.spotroute.repository;

import com.spotroute.core.enums.DocumentStatus;
import com.spotroute.persistence.entity.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {

    @Query("select d from DriverDocument d join fetch d.driverProfile p join fetch p.user " +
            "where d.status = :status order by d.uploadedAt asc")
    List<DriverDocument> findAllByStatusWithDriver(@Param("status") DocumentStatus status);
}
