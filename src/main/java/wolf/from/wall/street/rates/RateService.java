package wolf.from.wall.street.rates;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import wolf.from.wall.street.bot.MessageService;
import wolf.from.wall.street.resource.Resource;
import wolf.from.wall.street.resource.ResourceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;
    private final ResourceRepository resourceRepository;
    private final MessageService messageService;

    public List<Rate> generateRates() {
        List<Resource> resources = resourceRepository.findAll();
        List<Rate> result = new ArrayList<>();
        for (Resource resource : resources) {
            Rate lastRate = getLastRateByResource(resource);
            Rate rate = new Rate();
            rate.setResource(resource);
            rate.setPrice(generateRate(lastRate.getPrice()));
            rateRepository.save(rate);
            result.add(rate);
        }
        return result;
    }

    public void sendNewRates(List<Rate> rates) {
        messageService.sendWealth(rates);
    }


    private Rate getLastRateByResource(Resource resource) {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.Direction.DESC, "id");
        Page<Rate> rates = rateRepository.findByResource(resource, pageRequest);
        if (rates.isEmpty()) {
            throw new RuntimeException("previous rate not found");
        }
        return rates.getContent().getFirst();
    }


    private int generateRate(Integer price) {
        int percent = 10;
        Random r = new Random();
        int low = price - price * percent / 100;
        int high = price + price * percent / 100;
        return r.nextInt(high - low) + low;
    }
}
