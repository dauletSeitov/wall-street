package wolf.from.wall.street.rates;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import wolf.from.wall.street.common.BaseEntity;
import wolf.from.wall.street.resource.Resource;

@Data
@Entity(name = "Rates")
public class Rate extends BaseEntity {
    @ManyToOne
    private Resource resource;
    private Integer price;
}
