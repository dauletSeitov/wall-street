package wolf.from.wall.street.bot;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBotInput extends TelegramLongPollingBot {

    @Getter
    private final String botUsername;

    public TelegramBotInput(String botUsername, String token) {
        super(token);
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("update = " + update);
        if(update.hasMessage() && update.getMessage() != null){

            String text = update.getMessage().getText().trim();
            if("/start".equalsIgnoreCase(text)){

            }
        }

    }

}