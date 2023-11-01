package com.deundeunhaku.reliablekkuserver.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class FCMConfig {
  @Bean
  FirebaseMessaging firebaseMessaging() throws IOException {
    ClassPathResource resource = new ClassPathResource("/submodule/reliable-kku-server-submodule/deundeunhaku-43d86-firebase-adminsdk-9gnn1-757df95b46.json");

    InputStream refreshToken = resource.getInputStream();

    FirebaseApp firebaseApp = null;
    List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

    if (firebaseAppList != null && !firebaseAppList.isEmpty()) {
      for (FirebaseApp app : firebaseAppList) {
        if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
          firebaseApp = app;
        }
      }
    } else {
      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(refreshToken))
          .build();

      firebaseApp = FirebaseApp.initializeApp(options);
    }
    log.info("firebase init successful");
    assert firebaseApp != null;
    return FirebaseMessaging.getInstance(firebaseApp);
  }
}
