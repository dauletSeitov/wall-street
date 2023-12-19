package wolf.from.wall.street.job;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import wolf.from.wall.street.bot.MessageService;
import wolf.from.wall.street.bot.TelegramBotOutput;
import wolf.from.wall.street.rates.Rate;
import wolf.from.wall.street.rates.RateService;

import java.io.File;
import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CronJob {
    private final RateService rateService;

    @Scheduled(cron = "${app.cron.expression}")
    @SneakyThrows
    public void scheduler() {
        log.info("scheduler started");
        List<Rate> rates = rateService.generateRates();
        rateService.sendNewRates(rates);
    }
}
