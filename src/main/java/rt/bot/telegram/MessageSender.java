package rt.bot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSender {
    private final TelegramClient telegramClient;

    public void send(Long userID, String text) {
        try {
            telegramClient.execute(
                    SendMessage.builder()
                            .chatId(userID)
                            .text(text)
                            .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения [{}] пользователю с id {}", text, userID);
        }
    }

    public void sendFile(Long userID, InputFile file) {
        try {
            telegramClient.execute(
                    SendDocument.builder()
                            .chatId(userID)
                            .document(file)
                            .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке файла пользователю {}: {}", userID, e.getMessage());
        }
    }


    public void sendPicture(Long userId, InputFile photo) {
        try {
            telegramClient.execute(
                    SendPhoto.builder()
                            .chatId(userId)
                            .photo(photo)
                            .build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке изображения пользователю с id {}: {}", userId, e.getMessage());
        }
    }

    public void sendPicture(List<Long> allUsersIds, byte[] fileBytes) {
        Queue<Long> idQueue = new LinkedList<>(allUsersIds);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(() -> {
            Long userId = idQueue.poll();
            if (userId != null) {
                InputStream stream = new ByteArrayInputStream(fileBytes);
                sendPicture(userId, new InputFile(stream, "pic.png"));
            } else {
                executor.shutdown();
            }
        }, 0, 51, TimeUnit.MILLISECONDS);
    }
}