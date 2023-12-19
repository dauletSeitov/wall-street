package wolf.from.wall.street.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import wolf.from.wall.street.rates.Rate;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final TelegramBotOutput bot;
    @Value("${app.chat.id}")
    private Long chatId;

    @SneakyThrows
    public void sendWealth(List<Rate> rates) {

        String caption = rates.stream().map(itm -> itm.getResource().getName() + ":" + itm.getPrice()).collect(Collectors.joining());
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setParseMode("html");
        sendPhoto.setCaption(caption);
        sendPhoto.setPhoto(new InputFile(new File("/home/phantom/Downloads/photo_5190797942829733495_y.jpg")));
        bot.execute(sendPhoto);
    }
}
