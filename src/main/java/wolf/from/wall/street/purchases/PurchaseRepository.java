package wolf.from.wall.street.purchases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    @Query("SELECT P FROM Purchase P WHERE P.userId = :userId")
    Page<Purchase> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
