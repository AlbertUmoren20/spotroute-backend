package com.spotroute.config;

import com.spotroute.persistence.entity.Route;
import com.spotroute.persistence.entity.Stop;
import com.spotroute.repository.RouteRepository;
import com.spotroute.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final GeometryFactory geometryFactory;

    @Override
    public void run(String... args) {
        if (routeRepository.count() > 0) {
            log.info("Routes already seeded, skipping.");
            return;
        }

        log.info("Seeding routes and stops...");

        Route gbagadaToVI = Route.builder()
                .origin("Gbagada")
                .destination("Victoria Island")
                .pricePerSeat(new BigDecimal("1000.00"))
                .description("Gbagada → Victoria Island via 3rd Mainland Bridge")
                .build();

        Route yabaToLekki = Route.builder()
                .origin("Yaba")
                .destination("Lekki")
                .pricePerSeat(new BigDecimal("1500.00"))
                .description("Yaba → Lekki via Eko Bridge")
                .build();

        List<Route> savedRoutes = routeRepository.saveAll(List.of(gbagadaToVI, yabaToLekki));
        Route savedGbagadaToVI = savedRoutes.get(0);
        Route savedYabaToLekki = savedRoutes.get(1);
        log.info("Seeded {} routes.", savedRoutes.size());

        List<Stop> stops = List.of(
                stop("Gbagada Phase 1", 3.3890, 6.5560, 1, savedGbagadaToVI),
                stop("Anthony", 3.3650, 6.5850, 2, savedGbagadaToVI),
                stop("Third Mainland Bridge", 3.3903, 6.4869, 3, savedGbagadaToVI),
                stop("Victoria Island", 3.4219, 6.4281, 4, savedGbagadaToVI),

                stop("Yaba Bus Stop", 3.3711, 6.5095, 1, savedYabaToLekki),
                stop("Jibowu", 3.3750, 6.5180, 2, savedYabaToLekki),
                stop("Eko Bridge", 3.3800, 6.4650, 3, savedYabaToLekki),
                stop("Lekki Phase 1", 3.4726, 6.4474, 4, savedYabaToLekki)
        );

        stopRepository.saveAll(stops);
        log.info("Seeded {} stops.", stops.size());
    }

    private Stop stop(String name, double lng, double lat, int sequenceOrder, Route route) {
        return Stop.builder()
                .name(name)
                .coordinates(geometryFactory.createPoint(new Coordinate(lng, lat)))
                .sequenceOrder(sequenceOrder)
                .route(route)
                .build();
    }
}