package wolf.from.wall.street.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import wolf.from.wall.street.image.ImageService;
import wolf.from.wall.street.rates.Rate;
import wolf.from.wall.street.rates.RateService;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimpleMessageService {
    private final TelegramBotOutput bot;
    @Value("${app.chat.id}")
    private Long chatId;
    @Value("${app.rate.image.path}")
    private String path;

    private final RateService rateService;
    private final ImageService imageService;

    @SneakyThrows
    public void sendRates() {

        String caption = "New rates have been announced. Don't delay, purchase now!";
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setParseMode("html");
        sendPhoto.setCaption(caption);
        sendPhoto.setPhoto(new InputFile(new File(path)));
        bot.execute(sendPhoto);
    }

    @SneakyThrows
    public void sendRatesManually() {
        if (!new File(path).exists()) {
            List<Rate> rates = rateService.generateRates();
            imageService.createNewRateImage(rates);
        }
        sendRates();
    }

    @SneakyThrows
    public void sendStartMessage(Long chatId) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText("""
                /start (g/p) to get a description.
                /top (g/p) to get the top wealthy players.
                /account (g/p) to get current resources.
                /buy (g) to buy resources.
                /history (g/p) to get purchase history.
                /rate (g/p) to get last rates.
                                
                Game Rule: Each day you can make one purchase.
                The next day, resource costs will change depending on the market.
                You will receive a message with new prices.
                Your wealth will be evaluated based on the current rate, and your place on the top list will be determined.""");
        msg.setParseMode("html");
        msg.setDisableWebPagePreview(true);
        bot.execute(msg);
    }

}
