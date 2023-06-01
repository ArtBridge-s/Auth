package com.artbridge.gateway.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MemberNameRequestConsumer {

    private final MemberNameService memberNameService;
    private static final String TOPIC_MEMBER_NAME_REQUEST = "member-name-request";

    public MemberNameRequestConsumer(MemberNameService memberNameService) {
        this.memberNameService = memberNameService;
    }


    @KafkaListener(topics = TOPIC_MEMBER_NAME_REQUEST)
    public void processMessage(String name) {
        log.info("MemberNameRequestConsumer: {}", name);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();


        try {
            map = mapper.readValue(name, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        memberNameService.produceMemberName((Long) map.get("name"));
        log.info("MemberNameRequestConsumer: {}", map.get("name"));
    }
}
