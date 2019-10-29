//package jm.kafka;
//
//import jm.KafkaAdminService;
//import org.apache.kafka.clients.admin.*;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.concurrent.ExecutionException;
//
//@Service
//public class KafkaAdminServiceImpl implements KafkaAdminService {
//
//    private KafkaProperties properties;
//
//    public KafkaAdminServiceImpl(KafkaProperties properties) {
//        this.properties = properties;
//    }
//
//    @Override
//    public CreateTopicsResult addTopic(String topicKey) {
//        return addTopic(topicKey, 1, (short)1);
//    }
//
//    @Override
//    public CreateTopicsResult addTopic(String topicKey, int numPartition, short replicationFactor) {
//        try (AdminClient admin = AdminClient.create(properties.buildAdminProperties())) {
//            NewTopic topic = new NewTopic(topicKey, numPartition, replicationFactor);
//            return admin.createTopics(Collections.singleton(topic));
//        }
//    }
//
//    @Override
//    public DeleteTopicsResult deleteTopic (String topicKey) {
//        return deleteTopic(Collections.singleton(topicKey));
//    }
//
//    public DeleteTopicsResult deleteTopic (Collection<String> topics) {
//        try (AdminClient admin = AdminClient.create(properties.buildAdminProperties())) {
//            return admin.deleteTopics(topics);
//        }
//    }
//
//    @Override
//    public Collection<TopicListing> getAllTopics() {
//        Collection<TopicListing> topicListings = null;
//        try (AdminClient admin = AdminClient.create(properties.buildAdminProperties())){
//            topicListings = admin
//                    .listTopics()
//                    .listings()
//                    .get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        return topicListings;
//    }
//
//    @Override
//    public TopicDescription getTopicDescribe(String topicKey) {
//        TopicDescription topicDescription = null;
//        try (AdminClient admin = AdminClient.create(properties.buildAdminProperties())){
//            topicDescription = admin
//                    .describeTopics(Collections.singleton(topicKey))
//                    .all()
//                    .get()
//                    .get(topicKey);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        return topicDescription;
//    }
//
//    @Override
//    public CreatePartitionsResult increasePartitions(String topicKey, int totalPartitionCount) {
//        try (AdminClient admin = AdminClient.create(properties.buildAdminProperties())){
//            return admin.createPartitions(Collections.singletonMap(topicKey, NewPartitions.increaseTo(totalPartitionCount)));
//        }
//    }
//
//}
