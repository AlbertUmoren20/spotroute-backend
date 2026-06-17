package com.spotroute.Kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "ride-request" , groupId = "spotroute-consumer-group")
    public void consumeMessage(String message) {
       log.info("Received message: " + message );

    }
}
