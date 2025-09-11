package rt.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rt.bot.entity.BotUser;
import rt.bot.repo.BotUserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final BotUserRepository botUserRepository;

    public void changeUserStatus(Long userId) {
        BotUser user = botUserRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setStatus(BotUser.Status.USER);
            botUserRepository.save(user);
        } else {
            log.error("Не удалось найти пользователя в базе данных по id {} и изменить его статус", userId);
        }
    }

    public List<Long> getAllUsersIds() {
        return botUserRepository.findAllTelegramUserIds();
    }
}