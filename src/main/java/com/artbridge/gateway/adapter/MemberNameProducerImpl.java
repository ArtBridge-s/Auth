package com.artbridge.gateway.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
@Slf4j
@Service
public class MemberNameProducerImpl implements MemberNameProducer {

    private static final String TOPIC = "member-name";
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    public MemberNameProducerImpl(ObjectMapper objectMapper, KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;

    }

    public void memberNameProduce(Long id, String name) {
        MemberNameDTO memberNameDTO = new MemberNameDTO(id, name);
        log.info("Producing member name: {}", memberNameDTO);
        try {
            String message = objectMapper.writeValueAsString(memberNameDTO);

            log.info("Producing member name message: {}", message);

            Message<byte[]> kafkaMessage = MessageBuilder
                .withPayload(message.getBytes())
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .build();
            kafkaTemplate.send(kafkaMessage);

            log.info("Produced member name to Kafka Producer successfully: {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
