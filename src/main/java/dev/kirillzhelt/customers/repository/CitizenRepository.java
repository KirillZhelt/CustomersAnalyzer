package dev.kirillzhelt.customers.repository;

import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.projection.AgesForCityInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CitizenRepository extends ReactiveCrudRepository<Citizen, Integer> {

    Flux<Citizen> findAllByImportId(Integer importId);

    Mono<Citizen> findByImportIdAndCitizenId(Integer importId, Integer citizenId);

    @Query("SELECT c.town as town, " +
        "percentile_cont(0.5) WITHIN GROUP (ORDER BY EXTRACT (YEAR FROM age(timezone('utc', now()), c.birth_date))) as p50, " +
        "percentile_cont(0.75) WITHIN GROUP (ORDER BY EXTRACT (YEAR FROM age(timezone('utc', now()), c.birth_date))) as p75, " +
        "percentile_cont(0.99) WITHIN GROUP (ORDER BY EXTRACT (YEAR FROM age(timezone('utc', now()), c.birth_date))) as p99 " +
        "FROM citizen c " +
        "WHERE c.import_id = :importId " +
        "GROUP BY town")
    Flux<AgesForCityInfo> countPercentiles(Integer importId);

}
