package rt.bot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rt.bot.entity.BotUser;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateProcessor {
    private final UserAuthentication userAuthentication;
    private final UserMessageReplier userMessageReplier;

    public void process(Update update) {
        if (TelegramUtils.isValidUpdate(update)) {
            BotUser botUser = userAuthentication.authenticate(update);
            userMessageReplier.reply(botUser);
        } else {
            log.info("Отсутствует поддерживаемое содержание в обновлении [{}]", update.getUpdateId());
        }
    }
}