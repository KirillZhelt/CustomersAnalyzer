package dev.kirillzhelt.customers.repository;

import dev.kirillzhelt.customers.entity.Relative;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RelativeRepository extends ReactiveCrudRepository<Relative, Integer> {

    Flux<Relative> findAllByCitizenIdAndImportId(Integer citizenId, Integer importId);

    @Query("DELETE FROM relative r WHERE r.import_id = :importId and (r.citizen_id = :citizenId or r.relative_id = :citizenId)")
    Mono<Void> deleteAllRelativeData(Integer importId, Integer citizenId);


}
