package jm.fcm;


import com.google.firebase.messaging.*;
import jm.FcmService;
import jm.model.fcm.PushNotifyConf;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;


@Service
public class FcmServiceImpl implements FcmService {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FcmServiceImpl.class);


  public String sendByTopic(PushNotifyConf conf, String channel) {
    Message message = Message.builder().setTopic(channel)
            .setWebpushConfig(WebpushConfig.builder()
                    .putHeader("ttl", conf.getTtlInSeconds())
                    .putData("click_action", conf.getClick_action())
                    .setNotification(createBuilder(conf).build())
                    .build())
            .build();

    try {
      return FirebaseMessaging.getInstance()
              .sendAsync(message)
              .get();
    } catch (InterruptedException | ExecutionException e) {
      logger.error(" error from sendByTopic" + Arrays.toString(e.getStackTrace()));
      return null;
    }
  }

  public String sendPersonal(PushNotifyConf conf, String clientToken)
          throws ExecutionException, InterruptedException {
    Message message = Message.builder().setToken(clientToken)
            .setWebpushConfig(WebpushConfig.builder()
                    .putHeader("ttl", conf.getTtlInSeconds())
                    .setNotification(createBuilder(conf).build())
                    .build())
            .build();

    return FirebaseMessaging.getInstance()
            .sendAsync(message)
            .get();
  }

  public void subscribe(String workspace_id, String clientToken) {
    try {
      TopicManagementResponse response = FirebaseMessaging.getInstance()
              .subscribeToTopicAsync(Collections.singletonList(clientToken), workspace_id).get();
      logger.info(" tokens were subscribed successfully");
    }
    catch (InterruptedException | ExecutionException e) {
      logger.error("subscribe", e);
    }
  }

  public void addDeviceToUser(String userName, String deviceToken) {
  }

  private WebpushNotification.Builder createBuilder(PushNotifyConf conf){
    WebpushNotification.Builder builder = WebpushNotification.builder();
    builder.addAction(new WebpushNotification
            .Action(conf.getClick_action(), "Открыть"))
            .setImage(conf.getIcon())
            .setTitle(conf.getTitle())
            .setBody(conf.getBody());
    return builder;
  }
}
