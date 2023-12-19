package wolf.from.wall.street.resource;

import jakarta.persistence.Entity;
import lombok.Data;
import wolf.from.wall.street.common.BaseEntity;

@Data
@Entity(name = "Resources")
public class Resource extends BaseEntity {
    private String name;
    private String unit;
}
