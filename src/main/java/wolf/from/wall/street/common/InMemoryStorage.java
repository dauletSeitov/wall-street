package wolf.from.wall.street.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryStorage {
    @Value("${app.message.cache.size}")
    private int maxSize;
    @Value("${app.message.ttl}")
    private int messageTtl;
    private final Map<Long, MessageData> map = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, MessageData> eldest) {
            return this.size() > maxSize;
        }
    };

    public void put(Long userId, MessageData messageData) {
        map.put(userId, messageData);
    }

    public Optional<MessageData> get(Long userId) {

        if(!map.containsKey(userId)){
            return Optional.empty();
        }

        MessageData messageData = map.get(userId);

        long minutes = ChronoUnit.MINUTES.between(messageData.ttl(), LocalDateTime.now());

        if(minutes > messageTtl){
            map.remove(userId);
            return Optional.empty();
        }

        return Optional.of(messageData);
    }

    public boolean remove(Long userId) {
        return map.remove(userId) != null;
    }


    public record ResourcesDto(Integer id, String name, Integer amount, String unit) {
    }

    public record MessageData(List<ResourcesDto> current, List<ResourcesDto> future, LocalDateTime ttl, Integer messageId) {
    }

}

