package rt.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rt.bot.entity.PictureInfo;
import rt.bot.telegram.MessageSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PictureSendingService {

    @Value("${google.folders.morning}")
    private String folderMorning;
    @Value("${google.folders.noon}")
    private String folderNoon;
    @Value("${google.folders.evening}")
    private String folderEvening;

    private final UserService userService;
    private final PictureInfoService pictureInfoService;
    private final GoogleDriveService googleDriveService;
    private final MessageSender sender;

    @PostConstruct
    public void initialCheckUp() {
        if (folderMorning != null && !folderMorning.isBlank()) {
            log.info("Папка утро инициализирована");
        }
        if (folderNoon != null && !folderNoon.isBlank()) {
            log.info("Папка день инициализирована");
        }
        if (folderEvening != null && !folderEvening.isBlank()) {
            log.info("Папка вечер инициализирована");
        }
    }

    public void work(PictureInfo.Period period) {
        updatePicsList(period);
        sendPicToEveryone(period);
    }

    public void sendMorningPicToNewUser(Long userId) {
        String picId = pictureInfoService.getLastSentPicIdByPeriod(PictureInfo.Period.MORNING);
        if (picId == null) {
            log.error("Отсутствует изображение для отправки новому пользователю с id {}", userId);
            return;
        }
        byte[] fileBytes = getImageAsBytes(picId);
        if (fileBytes == null) {
            log.error("Не удалось получить изображение с Google Drive для отправки новому пользователю с id {}", userId);
            return;
        }
        sender.sendPicture(userId, fileBytes);
    }

    private void updatePicsList(PictureInfo.Period period) {
        String folder;
        switch (period) {
            case MORNING -> folder = folderMorning;
            case NOON -> folder = folderNoon;
            case EVENING -> folder = folderEvening;
            default -> {
                log.error("Неверный период. Обновить список картинок не удалось");
                return;
            }
        }

        List<String> picsInFolder = googleDriveService.getImageIdsByFolder(folder);
        List<String> picsInDB = pictureInfoService.findGoogleDriveFileIdsByPeriod(period);
        Set<String> inFolder = new HashSet<>(picsInFolder);
        Set<String> inDB = new HashSet<>(picsInDB);

        Set<String> onlyInFolder = new HashSet<>(inFolder);
        onlyInFolder.removeAll(inDB);
        pictureInfoService.addNewPicsToDB(onlyInFolder, period);

        Set<String> onlyInDB = new HashSet<>(inDB);
        onlyInDB.removeAll(inFolder);
        pictureInfoService.removeAbsentPicsFromDB(onlyInDB);
        log.info("Актуализация изображений в БД завершена");
    }

    private void sendPicToEveryone(PictureInfo.Period period) {
        String picId = pictureInfoService.getActualPicId(period);
        if (picId == null) {
            log.warn("Отсутствуют изображения для отправки в период {}", period);
            return;
        }
        byte[] fileBytes = getImageAsBytes(picId);
        if (fileBytes == null) {
            log.error("Не удалось получить изображение с Google Drive для отправки в период {}", period);
            return;
        }
        List<Long> allUsersIds = userService.getAllUsersIds();
        sender.sendPicture(allUsersIds, fileBytes);
        log.info("Рассылка {} завершена", period);
    }

    private byte[] getImageAsBytes(String picId) {
        byte[] fileBytes;
        int attempt = 0;
        do {
            fileBytes = googleDriveService.getImageByIdAsBytes(picId);
            if (fileBytes == null) {
                try {
                    Thread.sleep(999L * attempt);
                } catch (InterruptedException e) {
                    log.error("Ошибка: {}", e.getMessage());
                }
            }
            attempt++;
        } while (fileBytes == null && attempt < 3);
        return fileBytes;
    }
}