package dev.kirillzhelt.customers.repository;

import dev.kirillzhelt.customers.entity.Import;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportRepository extends ReactiveCrudRepository<Import, Integer> {
}
