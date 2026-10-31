package com.spotroute.repository;

import com.spotroute.persistence.entity.LandMark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LandMarkRepository extends JpaRepository<LandMark,Long> {
    List<LandMark> findByRouteId(Long routeId);
}
