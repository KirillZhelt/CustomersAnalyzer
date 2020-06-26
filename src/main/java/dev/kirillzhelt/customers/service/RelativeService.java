package dev.kirillzhelt.customers.service;

import dev.kirillzhelt.customers.dto.PresentsDTO;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

public interface RelativeService {

    Mono<Map<Integer, Collection<PresentsDTO>>> countPresentsPerMonth(int importId);

}
