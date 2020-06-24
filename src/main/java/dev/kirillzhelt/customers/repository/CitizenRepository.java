package dev.kirillzhelt.customers.repository;

import dev.kirillzhelt.customers.entity.Citizen;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenRepository extends ReactiveCrudRepository<Citizen, Integer> {
}
