package com.allygo.allygoauthenticatorms;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        // Depuración: muestra el valor de la variable de entorno y verifica si el archivo existe
        String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        System.out.println("GOOGLE_APPLICATION_CREDENTIALS: " + credPath);
        File credentialsFile = new File(credPath);
        System.out.println("Credenciales existen: " + credentialsFile.exists());

        if (FirebaseApp.getApps().isEmpty()) {
            // Toma automáticamente las credenciales de la variable de entorno GOOGLE_APPLICATION_CREDENTIALS
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setProjectId("allygo") // tu Project ID real
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}