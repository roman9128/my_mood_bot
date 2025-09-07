package rt.bot.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import rt.bot.entity.BotUser;
import rt.bot.repo.BotUserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthentication {

    private final BotUserRepository botUserRepository;

    public BotUser authenticate(Update update) {
        var userId = TelegramUtils.extractUserIdFromUpdate(update);
        Optional<BotUser> optionalUser = botUserRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            User tgUserFromUpdate = TelegramUtils.extractUserFromUpdate(update);
            BotUser botUser = new BotUser();
            botUser.setTelegramUserId(userId);
            botUser.setTelegramFirstName(tgUserFromUpdate.getFirstName());
            botUser.setTelegramLastName(tgUserFromUpdate.getLastName());
            botUser.setTelegramUsername(tgUserFromUpdate.getUserName());
            botUser.setStatus(BotUser.Status.GUEST);
            return botUserRepository.save(botUser);
        } else {
            return optionalUser.get();
        }
    }
}