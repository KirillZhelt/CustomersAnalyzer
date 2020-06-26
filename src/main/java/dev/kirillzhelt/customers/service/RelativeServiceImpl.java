package dev.kirillzhelt.customers.service;

import dev.kirillzhelt.customers.dto.PresentsDTO;
import dev.kirillzhelt.customers.entity.projection.PresentsInfo;
import dev.kirillzhelt.customers.repository.RelativeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.management.relation.RelationServiceNotRegisteredException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RelativeServiceImpl implements RelativeService {

    private final RelativeRepository relativeRepository;

    public RelativeServiceImpl(RelativeRepository relativeRepository) {
        this.relativeRepository = relativeRepository;
    }

    public Mono<Map<Integer, Collection<PresentsDTO>>> countPresentsPerMonth(int importId) {
        Map<Integer, Collection<PresentsDTO>> presentsForMonth = IntStream.range(1, 13)
            .boxed().collect(Collectors.toMap(i -> i, i -> new ArrayList<>()));

        return this.relativeRepository.countRelativesBirthdays(importId)
            .collectMultimap(PresentsInfo::getMonth,
                presentsInfo -> new PresentsDTO(presentsInfo.getCitizenId(), presentsInfo.getPresents()),
                () -> presentsForMonth);
    }

}
