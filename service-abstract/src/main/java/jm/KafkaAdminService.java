package jm;

import org.apache.kafka.clients.admin.*;

import java.util.Collection;

public interface KafkaAdminService {

    CreateTopicsResult addTopic(String topicKey);

    CreateTopicsResult addTopic(
            String topicKey,
            int numPartition,
            short replicationFactor);

    DeleteTopicsResult deleteTopic (String topicKey);

    DeleteTopicsResult deleteTopic (Collection<String> topics);

    Collection<TopicListing> getAllTopics();

    TopicDescription getTopicDescribe(String topicKey);

    CreatePartitionsResult increasePartitions(String topicKey, int totalPartitionCount);
}
