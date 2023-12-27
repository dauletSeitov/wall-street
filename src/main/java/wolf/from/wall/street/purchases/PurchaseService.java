package wolf.from.wall.street.purchases;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import wolf.from.wall.street.bot.TelegramBotOutput;
import wolf.from.wall.street.common.AccountService;
import wolf.from.wall.street.common.InMemoryStorage;
import wolf.from.wall.street.common.ResourceService;
import wolf.from.wall.street.common.UserService;
import wolf.from.wall.street.entity.Account;
import wolf.from.wall.street.entity.User;
import wolf.from.wall.street.rates.Rate;
import wolf.from.wall.street.rates.RateService;
import wolf.from.wall.street.resource.Resource;
import wolf.from.wall.street.wealth.WealthDto;
import wolf.from.wall.street.wealth.WealthService;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final TelegramBotOutput bot;
    private final UserService userService;
    private final AccountService accountService;
    private final ResourceService resourceService;
    private final RateService rateService;
    private final PurchaseRepository purchaseRepository;
    private final WealthService wealthService;

    private final InMemoryStorage inMemoryStorage;

    public void handleBuy(Message message) {
        User user = userService.createIfAbsent(message.getFrom().getId(), message.getFrom().getUserName());

        if (user.getLastBoughDate().isEqual(LocalDate.now())) {
            wealthService.sendWealth(message);
        } else {
            handleBuyInternal(message.getChatId(), user);
        }
    }

    @SneakyThrows
    public void handleBuyInternal(Long chatId, User user) {

        Map<Integer, Account> resourceIdAccountMap = accountService.getAccountsByUserId(user.getUserId()).stream().collect(Collectors.toMap(itm -> itm.getResource().getId(), itm -> itm));

        List<InMemoryStorage.ResourcesDto> currentResources = new ArrayList<>();
        List<InMemoryStorage.ResourcesDto> futureResources = new ArrayList<>();
        currentResources.add(new InMemoryStorage.ResourcesDto(-1, "balance", user.getBalance(), "tg"));
        futureResources.add(new InMemoryStorage.ResourcesDto(-1, "cost", 0, "tg"));
        for (Resource resource : resourceService.findAll()) {
            if (resourceIdAccountMap.containsKey(resource.getId())) {
                Account account = resourceIdAccountMap.get(resource.getId());
                currentResources.add(new InMemoryStorage.ResourcesDto(resource.getId(), account.getResource().getName(), account.getAmount(), account.getResource().getUnit()));
            } else {
                currentResources.add(new InMemoryStorage.ResourcesDto(resource.getId(), resource.getName(), 0, resource.getUnit()));
            }
            futureResources.add(new InMemoryStorage.ResourcesDto(resource.getId(), resource.getName(), 0, resource.getUnit()));
        }

        InMemoryStorage.MessageData messageData = new InMemoryStorage.MessageData(currentResources, futureResources, LocalDateTime.now(), -1);
        Integer messageId = sendMessage(chatId, messageData);
        inMemoryStorage.put(user.getUserId(), new InMemoryStorage.MessageData(currentResources, futureResources, LocalDateTime.now(), messageId));
    }

    @SneakyThrows
    @Transactional
    public void handleCallBack(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();
        if (data.startsWith("CONFIRM")) {
            handleConfirm(callbackQuery);
        } else if (data.startsWith("CANCEL")) {
            handleCancel(callbackQuery);
        } else {
            handleUpdate(callbackQuery);
        }

    }

    @SneakyThrows
    public void handleUpdate(CallbackQuery callbackQuery) {

        InMemoryStorage.MessageData messageData = inMemoryStorage.get(callbackQuery.getFrom().getId())
                .orElseThrow(() -> new NoSuchElementException("timeout for user " + callbackQuery.getFrom().getUserName()));

        if (!callbackQuery.getMessage().getMessageId().equals(messageData.messageId())) {
            throw new RuntimeException("it is not you message: " + callbackQuery.getFrom().getUserName());
        }
        String data = callbackQuery.getData();

        String[] split = data.split("-");
        String command = split[0];
        int amount = Integer.parseInt(split[1]);
        Integer resourceId = Integer.valueOf(split[2]);

        int index = -1;
        InMemoryStorage.ResourcesDto resourcesDto = null;
        for (int i = 0; i < messageData.future().size(); i++) {

            if (messageData.future().get(i).id().equals(resourceId)) {
                resourcesDto = messageData.future().get(i);
                index = i;
                break;
            }
        }

        int val;
        if ("buy".equals(command)) {
            val = amount;
        } else {
            val = -amount;
        }

        InMemoryStorage.ResourcesDto evaluated = new InMemoryStorage.ResourcesDto(resourcesDto.id(), resourcesDto.name(), resourcesDto.amount() + val, resourcesDto.unit());
        messageData.future().set(index, evaluated);

        Rate rate = rateService.getLastRateByResourceId(resourcesDto.id());
        Integer resourceCost = rate.getPrice() * val;

        InMemoryStorage.ResourcesDto resourcesTotalCost = messageData.future().getFirst();
        InMemoryStorage.ResourcesDto evaluatedCost = new InMemoryStorage.ResourcesDto(resourcesTotalCost.id(), resourcesTotalCost.name(), resourcesTotalCost.amount() + resourceCost, resourcesTotalCost.unit());
        messageData.future().set(0, evaluatedCost);

        EditMessageCaption messageCaption = EditMessageCaption.builder()
                .caption(createMessage(messageData))
                .messageId(messageData.messageId())
                .chatId(callbackQuery.getMessage().getChatId())
                .replyMarkup(callbackQuery.getMessage().getReplyMarkup())
                .build();

        bot.execute(messageCaption);
    }

    @SneakyThrows
    private void handleConfirm(CallbackQuery callbackQuery) {

        InMemoryStorage.MessageData messageData = inMemoryStorage.get(callbackQuery.getFrom().getId())
                .orElseThrow(() -> new NoSuchElementException("timeout for user " + callbackQuery.getFrom().getUserName()));

        if (!callbackQuery.getMessage().getMessageId().equals(messageData.messageId())) {
            throw new RuntimeException("it is not you message: " + callbackQuery.getFrom().getUserName());
        }

        Integer sum = 0;
        String resourcesString = "";
        for (InMemoryStorage.ResourcesDto resourcesDto : messageData.future()) {
            if (resourcesDto.amount() == 0 || resourcesDto.id() == -1) {
                continue;
            }

            Rate rate = rateService.getLastRateByResourceId(resourcesDto.id());
            Purchase purchase = new Purchase();
            purchase.setRate(rate);
            purchase.setAmount(resourcesDto.amount());
            purchase.setUserId(callbackQuery.getFrom().getId());
            purchaseRepository.save(purchase);

            sum += rate.getPrice() * resourcesDto.amount();

            resourcesString += String.format("%s %d %s\n", resourcesDto.name(), resourcesDto.amount(), resourcesDto.unit());
        }

        User user = userService.getUser(callbackQuery.getFrom().getId()).orElseThrow(() -> new NoSuchElementException("user not found"));

        String message;
        if (sum == 0) {
            message = String.format("@%s Sorry, but you cannot buy nothing.", callbackQuery.getFrom().getUserName());
        } else if (sum > user.getBalance()) {

            message = String.format("""
                     @%s Sorry, you have not enough money.
                     Your balance is: %d tg and total cost is: %d tg
                    """, callbackQuery.getFrom().getUserName(), user.getBalance(), sum);

        } else {


            for (InMemoryStorage.ResourcesDto resourcesDto : messageData.future()) {
                if (resourcesDto.amount() == 0 || resourcesDto.id() == -1) {
                    continue;
                }
                Optional<Account> accountOpt = accountService.getAccountByResourceIdAndUserId(resourcesDto.id(), user.getUserId());
                Account account;
                if (accountOpt.isPresent()) {
                    account = accountOpt.get();
                    account.setAmount(account.getAmount() + resourcesDto.amount());
                } else {

                    Resource resource = new Resource();
                    resource.setId(resourcesDto.id());

                    account = new Account();
                    account.setUserId(user.getUserId());
                    account.setAmount(resourcesDto.amount());
                    account.setResource(resource);
                }
                accountService.save(account);
            }

            user.setBalance(user.getBalance() - sum);
            user.setLastBoughDate(LocalDate.now());
            userService.save(user);

            WealthDto wealth = wealthService.getWealthByUserId(user.getUserId());
            message = String.format("""
                    @%s You successfully bought:
                    %s for: %d tg
                    Your current balance is: %d
                    Your wealth is: %d
                    """, callbackQuery.getFrom().getUserName(), resourcesString, sum, user.getBalance(), wealth.totalPrice());
            inMemoryStorage.remove(callbackQuery.getFrom().getId());
        }

        SendMessage sendMessage = SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .text(message)
                .build();


        bot.execute(sendMessage);

    }

    @SneakyThrows
    public Integer sendMessage(Long chatId, InMemoryStorage.MessageData messageData) {

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setParseMode("html");
        sendPhoto.setCaption(createMessage(messageData));
        sendPhoto.setPhoto(new InputFile(new File("/home/phantom/Downloads/photo_5190797942829733495_y.jpg")));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> line1 = new ArrayList<>();
        List<InlineKeyboardButton> line2 = new ArrayList<>();
        for (InMemoryStorage.ResourcesDto resourcesDto : messageData.current()) {
            if (resourcesDto.id() == -1) {
                continue;
            }
            line1.add(InlineKeyboardButton.builder().text("+10 " + resourcesDto.name()).callbackData("buy-10-" + resourcesDto.id()).build());
            line2.add(InlineKeyboardButton.builder().text("-10 " + resourcesDto.name()).callbackData("sell-10-" + resourcesDto.id()).build());
        }

        markupInline.setKeyboard(List.of(line1, line2,
                List.of(InlineKeyboardButton.builder().text("Confirm").callbackData("CONFIRM").build()),
                List.of(InlineKeyboardButton.builder().text("Cancel").callbackData("CANCEL").build())));
        sendPhoto.setReplyMarkup(markupInline);


        Message execute = bot.execute(sendPhoto);
        return execute.getMessageId();

    }

    private String createMessage(InMemoryStorage.MessageData messageData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < messageData.current().size(); i++) {
            InMemoryStorage.ResourcesDto current = messageData.current().get(i);
            InMemoryStorage.ResourcesDto feature = messageData.future().get(i);
            stringBuilder.append(String.format("%s: %d %s  %s: %d %s ", current.name(), current.amount(), current.unit(), feature.name(), feature.amount(), feature.unit()));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @SneakyThrows
    private void handleCancel(CallbackQuery callbackQuery) {

        boolean isRemoved = inMemoryStorage.remove(callbackQuery.getFrom().getId());
        if (isRemoved) {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .text(String.format("@%s bye bye!", callbackQuery.getFrom().getUserName()))
                    .build();

            bot.execute(sendMessage);
        }
    }

    @SneakyThrows
    public void sendHistory(Message message) {//TODO make better report

        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, "createdAt");
        Page<Purchase> page = purchaseRepository.findByUserId(message.getFrom().getId(), pageRequest);

        String content = page.getContent().stream()
                .map(itm -> String.format("%-12s | %-10s | %-7d | %-5d", itm.getCreatedAt().toLocalDate(), itm.getRate().getResource().getName(), itm.getAmount(), itm.getRate().getPrice() * itm.getAmount()))
                .collect(Collectors.joining("\n"));

        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text("Date | Resource | Amount | Price\n" + content)
                .build();
        bot.execute(sendMessage);
    }
}
