package wolf.from.wall.street.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wolf.from.wall.street.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
