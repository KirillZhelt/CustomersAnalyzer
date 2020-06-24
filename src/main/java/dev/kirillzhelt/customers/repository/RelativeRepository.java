package dev.kirillzhelt.customers.repository;

import dev.kirillzhelt.customers.entity.Relative;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RelativeRepository extends ReactiveCrudRepository<Relative, Integer> {

    Flux<Relative> findAllByCitizenIdAndImportId(Integer citizenId, Integer importId);

}
