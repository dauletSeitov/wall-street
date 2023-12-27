package wolf.from.wall.street.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import wolf.from.wall.street.common.BaseEntity;
import wolf.from.wall.street.resource.Resource;

@Data
@Entity
@Table(name = "Accounts")
public class Account extends BaseEntity {

    private Long userId;
    @ManyToOne
    private Resource resource;
    private Integer amount;

}
