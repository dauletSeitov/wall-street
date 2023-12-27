package wolf.from.wall.street.wealth;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import wolf.from.wall.street.bot.TelegramBotOutput;
import wolf.from.wall.street.entity.Account;
import wolf.from.wall.street.entity.User;
import wolf.from.wall.street.rates.Rate;
import wolf.from.wall.street.rates.RateService;
import wolf.from.wall.street.repository.AccountRepository;
import wolf.from.wall.street.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WealthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RateService rateService;
    private final TelegramBotOutput bot;

    public WealthDto getWealthByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("no user found" + userId));

        List<Account> accounts = accountRepository.findByUserId(userId);
        Map<Integer, WealthDto.Resource> map = new HashMap<>();
        int sum = 0;
        for (Account account : accounts) {
            Rate rate = rateService.getLastRateByResourceId(account.getResource().getId());
            int price = rate.getPrice() * account.getAmount();
            sum += price;
            WealthDto.Resource resource = new WealthDto.Resource(account.getResource().getId(), account.getResource().getName(), account.getAmount(), price, account.getResource().getUnit());
            map.put(account.getResource().getId(), resource);
        }

        return new WealthDto(sum, user.getBalance(), sum + user.getBalance(), map);
    }

    public void sendWealth(Message message) {
        sendWealth(message.getFrom().getId(), message.getFrom().getUserName(), message.getChatId());
    }

    @SneakyThrows
    public void sendWealth(Long userId, String userName, Long chatId) {

        WealthDto wealth = getWealthByUserId(userId);

        StringBuilder sb = new StringBuilder();
        for (var entry : wealth.resourceMap().entrySet()) {
            WealthDto.Resource resource = entry.getValue();
            sb.append(String.format("%s %d%s %dtg\n", resource.name(), resource.amount(), resource.unit(), resource.price()));
        }

        String messageText = String.format("""
                @%s your current wealth is:
                %s
                Your balance is: %d
                Your total resource price is: %d
                Your wealth is %d:""", userName, sb, wealth.userBalance(), wealth.resourcesPrice(), wealth.totalPrice());
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        bot.execute(sendMessage);
    }


}
