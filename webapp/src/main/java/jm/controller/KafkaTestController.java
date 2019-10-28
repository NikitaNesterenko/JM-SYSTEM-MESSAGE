package jm.controller;

import jm.KafkaAdminService;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

// Контроллер для тестирования работоспособности KafkaAdminService
// В будующем топики будут создаваться при создании Channel и отдельный контроллер для работы с топиками будет не нужен
// Хохлов Алексей, 22.10.2019

@Controller
public class KafkaTestController {

    private KafkaAdminService kafkaAdminService;

    public KafkaTestController(KafkaAdminService kafkaAdminService) {
        this.kafkaAdminService = kafkaAdminService;
    }

    @GetMapping("/test-kafka")
    public String testKafkaAdmin() {
        System.out.println("Все существующие топики в Kafka до начала теста");
        printTopic(kafkaAdminService.getAllTopics());
        String topicKey = "Workspace.Channel.test";
        kafkaAdminService.addTopic(topicKey);
        System.out.println("Топики после добавления");
        printTopic(kafkaAdminService.getAllTopics());
        System.out.println("Описание добавленного топика до увеличения количества партиций");
        System.out.println(kafkaAdminService.getTopicDescribe(topicKey));
        System.out.println("Увеличение количества партиций в новом топике");
        kafkaAdminService.increasePartitions(topicKey, 5);
        System.out.println(kafkaAdminService.getTopicDescribe(topicKey));
        kafkaAdminService.deleteTopic(topicKey);
        System.out.println("Топики после удаления");
        printTopic(kafkaAdminService.getAllTopics());
        return "home-page";
    }

    private void printTopic(Collection<TopicListing> listTopic) {
        for (TopicListing topic : listTopic) {
            System.out.println(topic);
        }
    }
}
