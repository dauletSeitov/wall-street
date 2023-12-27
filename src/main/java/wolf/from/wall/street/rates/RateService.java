package wolf.from.wall.street.rates;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
    @Value("${app.rate.max.random.percent}")
    private int max;

    @Value("${app.rate.min.random.percent}")
    private int min;

    public List<Rate> generateRates() {
        List<Resource> resources = resourceRepository.findAll();
        List<Rate> result = new ArrayList<>();
        for (Resource resource : resources) {
            Rate lastRate = getLastRateByResourceId(resource.getId());
            Rate rate = new Rate();
            rate.setResource(resource);
            rate.setPrice(generateRate(lastRate.getPrice()));
            rateRepository.save(rate);
            result.add(rate);
        }
        return result;
    }

    public Rate getLastRateByResourceId(Integer resourceId) {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.Direction.DESC, "id");
        Page<Rate> rates = rateRepository.findByResourceId(resourceId, pageRequest);
        if (rates.isEmpty()) {
            throw new RuntimeException("previous rate not found");
        }
        return rates.getContent().getFirst();
    }


    private int generateRate(Integer price) {
        Random r = new Random();
        int low = price - price * min / 100;
        int high = price + price * max / 100;
        return r.nextInt(high - low) + low;
    }
}
