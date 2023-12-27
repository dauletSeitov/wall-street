package wolf.from.wall.street.wealth;

import java.util.Map;

public record WealthDto(Integer resourcesPrice, Integer userBalance, Integer totalPrice,
                        Map<Integer, Resource> resourceMap) {
    record Resource(Integer id, String name, Integer amount, Integer price, String unit) {
    }
}

