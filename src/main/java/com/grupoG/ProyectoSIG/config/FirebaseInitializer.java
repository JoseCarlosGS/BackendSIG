package com.grupoG.ProyectoSIG.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseInitializer {
    public FirebaseInitializer() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/proyecto-sig-247c3-firebase-adminsdk-fbsvc-27aeaeeecb.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://proyecto-sig-247c3-default-rtdb.firebaseio.com")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK inicializado correctamente.");
            } else {
                System.out.println("Firebase Admin SDK ya está inicializado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
