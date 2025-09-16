package rt.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rt.bot.telegram.MessageSender;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpamService {

    private final UserService userService;
    private final MessageSender sender;

    public void askDonat() {
        List<Long> allUsersIds = userService.getAllUsersIds();
        sender.send(allUsersIds, "Поддержать и поблагодарить проект можно здесь\nhttps://pay.cloudtips.ru/p/24367064");
    }
}