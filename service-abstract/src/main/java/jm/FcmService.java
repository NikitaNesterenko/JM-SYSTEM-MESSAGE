package jm;

import jm.model.fcm.PushNotifyConf;

import java.util.concurrent.ExecutionException;

public interface FcmService {

    String sendByTopic(PushNotifyConf conf, String channel) throws InterruptedException, ExecutionException;

    String sendPersonal(PushNotifyConf conf, String clientToken) throws ExecutionException, InterruptedException;

    void subscribe(String channel_id, String clientToken);

    void addDeviceToUser(String userName, String deviceToken);
}
