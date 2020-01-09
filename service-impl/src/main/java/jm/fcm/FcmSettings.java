package jm.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
public class FcmSettings {
  @Value("${fcm.service-account-file}")
  private String firebaseConfigPath;

  Logger logger = LoggerFactory.getLogger(FcmSettings.class);

  @PostConstruct
  public void initialize() {
    try {
      FirebaseOptions options = new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())).build();
      if (FirebaseApp.getApps().isEmpty()) {


        FirebaseApp.initializeApp(options);
        logger.info("Firebase application has been initialized");
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

}