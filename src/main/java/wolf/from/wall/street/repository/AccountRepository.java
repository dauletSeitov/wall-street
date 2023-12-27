package wolf.from.wall.street.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wolf.from.wall.street.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT A FROM Account A WHERE A.userId = :userId")
    List<Account> findByUserId(@Param("userId") Long userId);

    @Query("SELECT A FROM Account A WHERE A.userId = :userId AND A.resource.id = :resourceId")
    Optional<Account> getAccountByResourceIdAndUserId(@Param("resourceId") Integer resourceId, @Param("userId") Long userId);
}
