package jm;

import jm.model.KafkaMessage;

public interface KafkaClientService {
    void sendMessage(String channelName ,KafkaMessage message);
    void subscribeChannel(String channelName);
}
