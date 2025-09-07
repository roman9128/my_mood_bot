package rt.bot.telegram;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@UtilityClass
public class TelegramUtils {

    public static User extractUserFromUpdate(Update update) {
        if (update.hasMessage()) return update.getMessage().getFrom();
        else return null;

    }

    public static Long extractUserIdFromUpdate(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChat().getId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        } else {
            log.error("Не удалось определить id пользователя из обновления!");
            throw new UnsupportedOperationException("Невозможно определить id пользователя из обновления!");
        }
    }

    public static String extractUserTextFromUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        } else if (update.hasMessage() && update.getMessage().hasCaption()) {
            return update.getMessage().getCaption();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        } else {
            return "";
        }
    }

    public static boolean isValidUpdate(Update update) {
        if (!update.hasMessage()) {
            return false;
        }
        return extractUserFromUpdate(update) != null;
    }

}