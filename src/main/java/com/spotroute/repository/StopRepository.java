package com.spotroute.repository;

import com.spotroute.persistence.entity.Route;
import com.spotroute.persistence.entity.Stop;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StopRepository extends JpaRepository<Stop, UUID> {
    List<Stop> findByRouteOrderBySequenceOrder(Route route);

    @Query(value = """
        SELECT s.* FROM stops s
        WHERE ST_DWithin(s.coordinates, :point, :radiusMeters)
        ORDER BY ST_Distance(s.coordinates, :point)
        LIMIT 1
        """, nativeQuery = true)
    Optional<Stop> findNearestStop(
            @Param("point") Point point,
            @Param("radiusMeters") double radiusMeters
    );
}