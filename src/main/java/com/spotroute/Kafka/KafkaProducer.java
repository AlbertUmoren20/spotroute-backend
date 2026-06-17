package com.spotroute.Kafka;

import com.spotroute.dto.request.CreateRideRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    public KafkaTemplate<String, CreateRideRequest> kafkaTemplate;
    private static final String TOPIC = "ride-request";
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, CreateRideRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendRide(CreateRideRequest req) {
        kafkaTemplate.send(TOPIC, req);
        log.info("Message sent to Kafka topic: " + req + " on " + TOPIC);
    }
}
