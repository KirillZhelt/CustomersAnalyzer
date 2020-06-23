package dev.kirillzhelt.customers.repository;

import dev.kirillzhelt.customers.entity.Relative;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelativeRepository extends ReactiveCrudRepository<Relative, Integer> {
}
