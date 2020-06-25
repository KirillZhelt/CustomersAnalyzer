package dev.kirillzhelt.customers.repository;

import dev.kirillzhelt.customers.entity.Citizen;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CitizenRepository extends ReactiveCrudRepository<Citizen, Integer> {

    Flux<Citizen> findAllByImportId(Integer importId);
    Mono<Citizen> findByImportIdAndCitizenId(Integer importId, Integer citizenId);

}
