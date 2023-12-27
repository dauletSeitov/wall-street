package wolf.from.wall.street.job;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import wolf.from.wall.street.bot.SimpleMessageService;
import wolf.from.wall.street.image.ImageService;
import wolf.from.wall.street.rates.Rate;
import wolf.from.wall.street.rates.RateService;

import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CronJob {
    private final RateService rateService;
    private final ImageService imageService;
    private final SimpleMessageService simpleMessageService;

    @Scheduled(cron = "${app.cron.expression}")
    @SneakyThrows
    public void scheduler() {
        log.info("scheduler started");
        List<Rate> rates = rateService.generateRates();
        imageService.createNewRateImage(rates);
        simpleMessageService.sendRates();
    }
}
