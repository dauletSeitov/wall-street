package wolf.from.wall.street.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import wolf.from.wall.street.bot.TelegramBotInput;
import wolf.from.wall.street.bot.TelegramBotOutput;

@Component
public class TelegramBotConfig {

    @Value("${app.bot.username}")
    private String botUsername;

    @Value("${app.bot.token}")
    private String botToken;

    @Bean
    public TelegramBotInput telegramBotInput() {
        return new TelegramBotInput(botUsername, botToken);
    }

    @Bean
    public TelegramBotOutput telegramBotOutput() {
        return new TelegramBotOutput(botUsername, botToken);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBotInput input) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(input);
        return telegramBotsApi;
    }


}
