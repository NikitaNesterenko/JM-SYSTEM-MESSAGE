package jm.controller;

import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

// Контроллер для тестирования работоспособности KafkaAdminService
// В будующем топики будут создаваться при создании Channel и отдельный контроллер для работы с топиками будет не нужен
// Хохлов Алексей, 22.10.2019

@Controller
public class KafkaTestController {

    private void printTopic(Collection<TopicListing> listTopic) {
    }
}
