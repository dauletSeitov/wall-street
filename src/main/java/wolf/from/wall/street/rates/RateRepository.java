package wolf.from.wall.street.rates;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wolf.from.wall.street.resource.Resource;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {


    @Query("SELECT R FROM Rates R WHERE R.resource = :resource")
    Page<Rate> findByResource(@Param("resource") Resource resource, PageRequest pageRequest);
}
