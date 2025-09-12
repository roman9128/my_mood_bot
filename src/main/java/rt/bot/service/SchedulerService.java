package rt.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rt.bot.entity.PictureInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final PictureSendingService pictureSendingService;

    @PostConstruct
    public void initialCheckUp() {
        log.info("Запущен планировщик");
    }

    @Scheduled(cron = "0 30 8 * * ?", zone = "Europe/Moscow")
    public void sendMorningPic() {
        log.info("Запущена задача отправки сообщений в 8.30 утра");
        pictureSendingService.work(PictureInfo.Period.MORNING);
        log.info("Завершена задача отправки сообщений в 8.30 утра");
    }

    @Scheduled(cron = "0 0 10 * * ?", zone = "Europe/Moscow")
    public void sendNoonPic() {
        log.info("Запущена задача отправки сообщений в 10 утра");
        pictureSendingService.work(PictureInfo.Period.NOON);
        log.info("Завершена задача отправки сообщений в 10 утра");
    }

    @Scheduled(cron = "0 0 21 * * ?", zone = "Europe/Moscow")
    public void sendEveningPic() {
        log.info("Запущена задача отправки сообщений в 21 вечера");
        pictureSendingService.work(PictureInfo.Period.EVENING);
        log.info("Завершена задача отправки сообщений в 21 вечера");
    }
}