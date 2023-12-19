package wolf.from.wall.street.bot;

import jakarta.ws.rs.NotSupportedException;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBotOutput extends TelegramLongPollingBot {

    @Getter
    private final String botUsername;

    public TelegramBotOutput(String botUsername, String token) {
        super(token);
        this.botUsername = botUsername;
    }


    @Override
    public void onUpdateReceived(Update update) {
        throw new NotSupportedException("red not supported from this class");
    }

}