package rt.bot.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDriveService {

    private final Drive drive;

    @PostConstruct
    public void initialCheckUp(){
        log.info("Google Drive Service запущен");
    }

    public List<String> getImageIdsByFolder(String folderId) {
        try {
            String query = String.format("'%s' in parents and mimeType contains 'image/' and trashed = false", folderId);

            FileList result = drive.files().list()
                    .setQ(query)
                    .setSpaces("drive")
                    .setFields("files(id)")
                    .execute();

            return result.getFiles().stream()
                    .map(File::getId)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error getting image IDs from folder: {}", folderId, e);
            return Collections.emptyList();
        }
    }

    public byte[] getImageByIdAsBytes(String fileId) {
        try (InputStream inputStream = drive.files()
                .get(fileId)
                .executeMediaAsInputStream()) {

            if (inputStream == null) {
                log.error("Input stream is null for file ID: {}", fileId);
                return null;
            }

            return inputStream.readAllBytes();

        } catch (IOException e) {
            log.error("Error getting image as bytes by ID: {}", fileId, e);
            return null;
        }
    }
}