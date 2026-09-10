package com.spotroute.config;

import com.spotroute.persistence.entity.LandMark;
import com.spotroute.persistence.entity.Location;
import com.spotroute.persistence.entity.Route;
import com.spotroute.repository.LandMarkRepository;
import com.spotroute.repository.LocationRepository;
import com.spotroute.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RouteRepository routeRepository;
    private final LandMarkRepository landMarkRepository;
    private final LocationRepository locationRepository;

    @Override
    public void run(String... args) {
       seedLocation();
        if (routeRepository.count() == 0) {
            log.info("Seeding default routes...");

            // Get locations from database
            Location gbagada = getLocation("Gbagada");
            Location victoriaIsland = getLocation("Victoria Island");

            Location yaba = getLocation("Yaba");
            Location lekki = getLocation("Lekki");

            Location surulere = getLocation("Surulere");
            Location ikoyi = getLocation("Ikoyi");

            Location ikeja = getLocation("Ikeja");
            Location marina = getLocation("Marina");


            List<Route> routes = List.of(
                    Route.builder()
                            .origin(gbagada)
                            .destination(victoriaIsland)
                            .pricePerSeat(new BigDecimal("1000.00"))
                            .description("Gbagada → Victoria Island via 3rd Mainland Bridge")
                            .pickupPoints(List.of("Gbagada Phase 1", "Gbagada Phase 2", "Anthony", "Maryland"))
                            .build(),

                    Route.builder()
                            .origin(yaba)
                            .destination(lekki)
                            .pricePerSeat(new BigDecimal("1500.00"))
                            .description("Yaba → Lekki via Eko Bridge")
                            .pickupPoints(List.of("Yaba Bus Stop", "Jibowu", "Ojuelegba"))
                            .build(),

                    Route.builder()
                            .origin(surulere)
                            .destination(ikoyi)
                            .pricePerSeat(new BigDecimal("800.00"))
                            .description("Surulere → Ikoyi via Osborne Road")
                            .pickupPoints(List.of("Surulere Stadium", "Alaka", "Lawanson"))
                            .build(),

                    Route.builder()
                            .origin(ikeja)
                            .destination(marina)
                            .pricePerSeat(new BigDecimal("1200.00"))
                            .description("Ikeja → Marina via Lagos Island")
                            .pickupPoints(List.of("Ikeja Along", "Allen Avenue", "Ogba"))
                            .build()
            );
            List<Route> savedRoutes = routeRepository.saveAll(routes);
            log.info("Seeded {} routes.", routes.size());

            List<LandMark> landmarks = List.of(
                    LandMark.builder()
                            .name("Sabo Yaba")
                            .lat(new BigDecimal("6.5095"))
                            .lng(new BigDecimal("3.3711"))
                            .sequenceOrder(1)
                            .route(savedRoutes.get(0))// route already has an ID now
                            .build(),

                    LandMark.builder()
                            .name("Third Mainland Bridge")
                            .lat(new BigDecimal("6.4869"))
                            .lng(new BigDecimal("3.3903"))
                            .sequenceOrder(1)
                            .route(savedRoutes.get(1))  // Yaba → Lekki
                            .build(),


                    LandMark.builder()
                            .name("Osborne Road")
                            .lat(new BigDecimal("6.4550"))
                            .lng(new BigDecimal("3.4200"))
                            .sequenceOrder(1)
                            .route(savedRoutes.get(2)) // Surulere → Ikoyi
                            .build(),

                    LandMark.builder()
                            .name("Lagos Island")
                            .lat(new BigDecimal("6.4541"))
                            .lng(new BigDecimal("3.3947"))
                            .sequenceOrder(1)
                            .route(savedRoutes.get(3)) // Ikeja → Marina
                            .build()
            );
            landMarkRepository.saveAll(landmarks);
            log.info("Seeded {} routes and {} landmarks.", savedRoutes.size(), landmarks.size());
        }
    }

    public void seedLocation(){

        List<String> locationNames = List.of(
                "Gbagada",
                "Charley Boy",
                "Anthony",
                "Law School",
                "Falomo",
                "Lekki",
                "Ajah",
                "Victoria Island",
                "Yaba",
                "Surulere",
                "Ikoyi",
                "Ikeja",
                "Marina",
                "Maryland",
                "Jibowu",
                "Ojuelegba",
                "Gbagada Phase 1",
                "Gbagada Phase 2",
                "Yaba Bus Stop",
                "Surulere Stadium",
                "Alaka",
                "Lawanson",
                "Ikeja Along",
                "Allen Avenue",
                "Ogba"
        );
        locationNames.forEach(name -> {
            if (!locationRepository.existsByNameIgnoreCase(name)) {
                locationRepository.save(
                        Location.builder().name(name).build()
                );
            }
        });
        log.info("Locations seeded successfully.");
    }
    private Location getLocation(String name) {
        return locationRepository
                .findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new IllegalStateException("Location not found: " + name));
        }
    }

