package rt.bot.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class GoogleApiConfig {

    @Value("${google.credentials.path}")
    private Resource credentialsResource;
    @Value("${google.application.name}")
    private String applicationName;

    private GoogleCredentials loadCredentials() throws Exception {
        try (InputStream in = credentialsResource.getInputStream()) {
            return GoogleCredentials.fromStream(in)
                    .createScoped(List.of(DriveScopes.DRIVE));
        }
    }

    @Bean
    public Drive driveService() throws Exception {
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(loadCredentials()))
                .setApplicationName(applicationName)
                .build();
    }
}