package com.spotroute.repository;

import com.spotroute.persistence.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.stops WHERE true ORDER BY r.id")
    List<Route> findAllWithStops();
}
