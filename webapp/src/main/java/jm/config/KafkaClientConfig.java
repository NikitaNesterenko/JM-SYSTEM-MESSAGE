//package jm.config;
//
//import jm.model.KafkaMessage;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaClientConfig {
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//    @Value("${spring.kafka.consumer.group-id}")
//    private String consumerGroupId;
//
//    @Bean
//    public KafkaTemplate<String, KafkaMessage> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    @Bean
//    public ProducerFactory<String, KafkaMessage> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs(),
//                new StringSerializer(),
//                new JsonSerializer<>());
//    }
//
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        return props;
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(messageConsumerFactory());
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<String, KafkaMessage> messageConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfig(),
//                new StringDeserializer(),
//                new JsonDeserializer<>(KafkaMessage.class));
//    }
//
//    @Bean
//    public Map<String, Object> consumerConfig() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        return props;
//    }
//}