package wolf.from.wall.street.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import wolf.from.wall.street.purchases.PurchaseService;
import wolf.from.wall.street.top.TopService;
import wolf.from.wall.street.wealth.WealthService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotInput extends TelegramLongPollingBot {
    @Autowired
    private SimpleMessageService simpleMessageService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private WealthService wealthService;
    @Autowired
    private TopService topService;

    @Getter
    private final String botUsername;

    public TelegramBotInput(String botUsername, String token) {
        super(token);
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("update = " + update);
        if (update.hasMessage() && update.getMessage() != null) {

            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText().trim();
            if (text.startsWith("/start")) {
                simpleMessageService.sendStartMessage(chatId);
            } else if (text.equalsIgnoreCase("/top")) {
                topService.sendTopMessage(chatId);
            } else if (text.equalsIgnoreCase("/history")) {
                purchaseService.sendHistory(update.getMessage());
            } else if (text.equalsIgnoreCase("/account")) {
                wealthService.sendWealth(update.getMessage());
            } else if (text.equalsIgnoreCase("/buy") && (update.getMessage().getChat().isSuperGroupChat() || update.getMessage().getChat().isGroupChat())) {
                purchaseService.handleBuy(update.getMessage());
            } else if (text.equalsIgnoreCase("/rate")) {
                simpleMessageService.sendRatesManually();
            }
        } else if (update.hasCallbackQuery()) {
            purchaseService.handleCallBack(update.getCallbackQuery());
        }

    }

}