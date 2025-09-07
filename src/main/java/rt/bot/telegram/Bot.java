package rt.bot.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import rt.bot.config.TelegramConfiguration;

@Component
@RequiredArgsConstructor
public class Bot implements SpringLongPollingBot {
    private final TelegramConfiguration telegramConfiguration;
    private final UpdateConsumer updateConsumer;

    @Override
    public String getBotToken() {
        return telegramConfiguration.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}