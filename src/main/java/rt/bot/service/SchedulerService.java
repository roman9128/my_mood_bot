package rt.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rt.bot.entity.PictureInfo;
import rt.bot.telegram.MessageSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

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
        log.info("Запущен планировщик");
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

    @Scheduled(cron = "0 30 8 * * ?", zone = "Europe/Moscow")
    public void sendMorningPic() {
        log.info("Запущена задача отправки сообщений в 8.30 утра");
        updatePicsList(PictureInfo.Period.MORNING);
        sendPic(PictureInfo.Period.MORNING);
        log.info("Завершена задача отправки сообщений в 8.30 утра");
    }

    @Scheduled(cron = "0 0 10 * * ?", zone = "Europe/Moscow")
    public void sendNoonPic() {
        log.info("Запущена задача отправки сообщений в 10 утра");
        updatePicsList(PictureInfo.Period.NOON);
        sendPic(PictureInfo.Period.NOON);
        log.info("Завершена задача отправки сообщений в 10 утра");
    }

    @Scheduled(cron = "0 0 21 * * ?", zone = "Europe/Moscow")
    public void sendEveningPic() {
        log.info("Запущена задача отправки сообщений в 21 вечера");
        updatePicsList(PictureInfo.Period.EVENING);
        sendPic(PictureInfo.Period.EVENING);
        log.info("Завершена задача отправки сообщений в 21 вечера");
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

    private void sendPic(PictureInfo.Period period) {
        String picId = pictureInfoService.getActualPicId(period);
        if (picId == null) {
            log.warn("Отсутствуют изображения для отправки в период {}", period);
            return;
        }
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
        } while (fileBytes == null && attempt < 5);
        if (fileBytes == null) {
            log.error("Не удалось получить изображение с Google Drive для отправки в период {}", period);
            return;
        }
        List<Long> allUsersIds = userService.getAllUsersIds();
        sender.sendPicture(allUsersIds, fileBytes);
        log.info("Рассылка {} завершена", period);
    }
}