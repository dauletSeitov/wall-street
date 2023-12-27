package wolf.from.wall.street.purchases;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import wolf.from.wall.street.common.BaseEntity;
import wolf.from.wall.street.rates.Rate;

@Data
@Entity
@Table(name = "Purchases")
public class Purchase extends BaseEntity {

    @ManyToOne
    private Rate rate;
    private Integer amount;
    private Long userId;
}
