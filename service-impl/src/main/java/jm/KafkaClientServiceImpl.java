package jm;

import jm.model.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaClientServiceImpl implements KafkaClientService {
    @Autowired
    KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    @Autowired
    ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> containerFactory;
    @Autowired
    KafkaAdminService kafkaAdminService;
    @Autowired
    MessageListener<String, KafkaMessage> listener;

    @Override
    public void sendMessage(String channelName, KafkaMessage message) {
        kafkaTemplate.send(channelName, message);
    }

    @Override
    public void subscribeChannel(String channelName) {
        ConcurrentMessageListenerContainer<String, KafkaMessage> container = containerFactory.createContainer(channelName);
        container.setupMessageListener(listener);
        container.start();
    }
}