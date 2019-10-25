package jm.model;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class KafkaMessageListener implements MessageListener<String, KafkaMessage> {

    @Override
    public void onMessage(ConsumerRecord<String, KafkaMessage> data) {
        System.out.println("dateCreate: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(data.timestamp()), ZoneId.systemDefault()) + ", topic: " + data.topic() + ", " + data.value());
    }
}