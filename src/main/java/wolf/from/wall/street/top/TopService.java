package wolf.from.wall.street.top;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import wolf.from.wall.street.bot.TelegramBotOutput;
import wolf.from.wall.street.entity.User;
import wolf.from.wall.street.repository.UserRepository;
import wolf.from.wall.street.wealth.WealthDto;
import wolf.from.wall.street.wealth.WealthService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopService {

    private final UserRepository userRepository;
    private final WealthService wealthService;
    private final TelegramBotOutput telegramBotOutput;

    public List<TopDto> getTopList() {
        List<User> users = userRepository.findAll();
        List<TopDto> topList = new ArrayList<>();
        for (User user : users) {
            WealthDto wealth = wealthService.getWealthByUserId(user.getUserId());
            topList.add(new TopDto(user.getUserName(), wealth.totalPrice()));
        }
        topList.sort(Comparator.comparingInt(TopDto::price));
        return topList;
    }

    @SneakyThrows
    public void sendTopMessage(Long chatId) {
        List<TopDto> topList = getTopList();

        StringBuilder sb = new StringBuilder();
        for (TopDto topDto : topList) {
            sb.append(String.format("@%s %d tg\n", topDto.userName(), topDto.price()));
        }

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Top\n" + sb)
                .build();
        telegramBotOutput.execute(sendMessage);
    }
}
