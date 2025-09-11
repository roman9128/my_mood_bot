package rt.bot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rt.bot.entity.BotUser;
import rt.bot.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMessageReplier {

    private final MessageSender sender;
    private final UserService userService;

    public void reply(BotUser botUser) {
        Long userId = botUser.getTelegramUserId();
        if (botUser.getStatus() == BotUser.Status.ADMIN) {
            sender.send(userId, "Помни про новые картинки");
        } else if (botUser.getStatus() == BotUser.Status.GUEST) {
            sender.send(userId, "Привет!");
            userService.changeUserStatus(userId);
        } else {
            sender.send(userId, "Вы хотели что-то сказать?\nМожете написать @kayat_mari");
        }
        log.info("Ответил на входящее сообщение от пользователя с id {}", userId);
    }
}