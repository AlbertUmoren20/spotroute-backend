package com.spotroute.config;

import com.spotroute.entity.Route;
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

    @Override
    public void run(String... args) {
        if (routeRepository.count() == 0) {
            log.info("Seeding default routes...");
            List<Route> routes = List.of(
                Route.builder()
                    .origin("Gbagada")
                    .destination("Victoria Island")
                    .pricePerSeat(new BigDecimal("1000.00"))
                    .description("Gbagada → Victoria Island via 3rd Mainland Bridge")
                    .pickupPoints(List.of("Gbagada Phase 1", "Gbagada Phase 2", "Anthony", "Maryland"))
                        .landmarks(List.of("charley boy", "Gbagada", "Iyana Ipaja", "Third mainland bridge", "Osbourne", "Falomo", "Checking point"))
                    .build(),

                Route.builder()
                    .origin("Yaba")
                    .destination("Lekki")
                    .pricePerSeat(new BigDecimal("1500.00"))
                    .description("Yaba → Lekki via Eko Bridge")
                    .pickupPoints(List.of("Yaba Bus Stop", "Jibowu", "Ojuelegba"))
                        .landmarks(List.of("Sabo Yaba"))
                    .build(),

                Route.builder()
                    .origin("Surulere")
                    .destination("Ikoyi")
                    .pricePerSeat(new BigDecimal("800.00"))
                    .description("Surulere → Ikoyi via Osborne Road")
                    .pickupPoints(List.of("Surulere Stadium", "Alaka", "Lawanson"))
                        .landmarks(List.of("Lawanson"))
                    .build(),

                Route.builder()
                    .origin("Ikeja")
                    .destination("Marina")
                    .pricePerSeat(new BigDecimal("1200.00"))
                    .description("Ikeja → Marina via Lagos Island")
                    .pickupPoints(List.of("Ikeja Along", "Allen Avenue", "Ogba"))
                        .landmarks(List.of("Ogba"))
                    .build()
            );
            routeRepository.saveAll(routes);
            log.info("Seeded {} routes.", routes.size());
        }
    }
}
