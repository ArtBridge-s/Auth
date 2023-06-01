package com.artbridge.gateway.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberNameProducerImpl implements MemberNameProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "member-name";
    private final ObjectMapper objectMapper;

    public MemberNameProducerImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void memberNameProduce(Long id, String name) {
        MemberNameDTO memberNameDTO = new MemberNameDTO(id, name);
        log.info("Producing member name: {}", memberNameDTO);

        try {
            String message = objectMapper.writeValueAsString(memberNameDTO);
            log.info("Producing member name message: {}", message);
            kafkaTemplate.send(new ProducerRecord<>(TOPIC, message)).get();
            log.info("Produced member name to Kafka Producer successfully: {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
