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
    private final StatRequester statRequester;

    public void process(Update update) {
        if (TelegramUtils.isValidUpdate(update)) {
            BotUser botUser = userAuthentication.authenticate(update);
            if (adminRequestForStat(update, botUser)) {
                statRequester.sendStat(botUser);
            } else {
                userMessageReplier.reply(botUser);
            }
        } else {
            log.info("Отсутствует поддерживаемое содержание в обновлении: {}", update);
        }
    }

    private boolean adminRequestForStat(Update update, BotUser botUser) {
        if (botUser.getStatus() != BotUser.Status.ADMIN) return false;
        return TelegramUtils.extractUserTextFromUpdate(update).equalsIgnoreCase("статистика");
    }
}