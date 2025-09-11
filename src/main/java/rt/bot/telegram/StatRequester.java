package rt.bot.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import rt.bot.entity.BotUser;
import rt.bot.service.StatService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatRequester {

    private final MessageSender sender;
    private final StatService statService;

    public void sendStat(BotUser botUser) {
        InputFile report = statService.getReport();
        if (report != null) {
            sender.sendFile(botUser.getTelegramUserId(), report);
        } else {
            sender.send(botUser.getTelegramUserId(), "Не удалось подготовить отчёт в XLSX");
        }
    }
}