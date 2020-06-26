package dev.kirillzhelt.customers.service;

import dev.kirillzhelt.customers.dto.ImportCitizenDTO;
import dev.kirillzhelt.customers.dto.PatchCitizenDTO;
import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.Relative;
import dev.kirillzhelt.customers.entity.projection.AgesForCityInfo;
import dev.kirillzhelt.customers.repository.CitizenRepository;
import dev.kirillzhelt.customers.repository.RelativeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CitizenServiceImpl implements CitizenService {

    private final static Logger log = LoggerFactory.getLogger(CitizenServiceImpl.class);

    private final CitizenRepository citizenRepository;
    private final RelativeRepository relativeRepository;

    private final Validator validator;

    private final DatabaseClient databaseClient;

    public CitizenServiceImpl(CitizenRepository citizenRepository, RelativeRepository relativeRepository, Validator validator, DatabaseClient databaseClient) {
        this.citizenRepository = citizenRepository;
        this.relativeRepository = relativeRepository;

        this.validator = validator;

        this.databaseClient = databaseClient;
    }

    private void validatePatchCitizen(PatchCitizenDTO citizen) {
        if (citizen.getTown() == null && citizen.getStreet() == null && citizen.getBuilding() == null &&
            citizen.getApartment() == null && citizen.getName() == null && citizen.getBirthDate() == null &&
            citizen.getGender() == null && citizen.getRelatives() == null) {
            throw new ValidationException("All fields are null");
        }

        Set<ConstraintViolation<PatchCitizenDTO>> violations = this.validator.validate(citizen);
        log.info("Size of set of constraints violations: {}", violations.size());

        if (violations.size() != 0) {
            throw new ValidationException(String.format("Violations: %s", violations));
        }
    }

    private Mono<Tuple2<Citizen, PatchCitizenDTO>> updateCitizen(Tuple2<Citizen, PatchCitizenDTO> data) {
        Citizen citizen = data.getT1();
        PatchCitizenDTO patchCitizen = data.getT2();

        if (patchCitizen.getTown() != null) {
            citizen.setTown(patchCitizen.getTown());
        }

        if (patchCitizen.getStreet() != null) {
            citizen.setTown(patchCitizen.getTown());
        }

        if (patchCitizen.getBuilding() != null) {
            citizen.setBuilding(patchCitizen.getBuilding());
        }

        if (patchCitizen.getApartment() != null) {
            citizen.setApartment(patchCitizen.getApartment());
        }

        if (patchCitizen.getName() != null) {
            citizen.setName(patchCitizen.getName());
        }

        if (patchCitizen.getBirthDate() != null) {
            citizen.setBirthDate(patchCitizen.getBirthDate());
        }

        if (patchCitizen.getGender() != null) {
            citizen.setGender(patchCitizen.getGender());
        }

        return this.citizenRepository.save(citizen).zipWith(Mono.just(patchCitizen));
    }

    @Transactional
    public Mono<List<Relative>> updateRelatives(Tuple2<Citizen, PatchCitizenDTO> data) {
        Citizen citizen = data.getT1();
        PatchCitizenDTO patchCitizen = data.getT2();

        if (patchCitizen.getRelatives() != null) {
            Mono<List<Relative>> relativesMono;
            List<Relative> relatives = patchCitizen.getRelatives()
                .stream()
                .flatMap(relativeId -> Stream.of(new Relative(citizen.getImportId(), citizen.getCitizenId(), relativeId),
                    new Relative(citizen.getImportId(), relativeId, citizen.getCitizenId())))
                .collect(Collectors.toList());

            if (relatives.isEmpty()) {
                relativesMono = Mono.just(Collections.emptyList());
            } else {
                relativesMono = this.relativeRepository.saveAll(relatives).collectList();
            }

            return this.relativeRepository.deleteAllRelativeData(citizen.getImportId(), citizen.getCitizenId())
                .then(relativesMono);
        }

        return this.relativeRepository.findAllByCitizenIdAndImportId(citizen.getCitizenId(), citizen.getImportId()).collectList();
    }

    public Mono<ImportCitizenDTO> patchCitizen(Mono<PatchCitizenDTO> patchCitizenMono, Integer importId, Integer citizenId) {
        Mono<Citizen> citizenMono = this.citizenRepository
            .findByImportIdAndCitizenId(importId, citizenId)
            .doOnNext(citizen -> {
                if (citizen == null) {
                    throw new IllegalArgumentException("No citizen with such import id and citizen id");
                }
            });

        return citizenMono.zipWith(patchCitizenMono)
            .flatMap(this::updateCitizen)
            .zipWhen(this::updateRelatives)
            .map(t -> CitizenService.mapCitizenToDTO(t.getT1().getT1(), t.getT2()));
    }

    public Mono<List<AgesForCityInfo>> countPercentilesForCity(int importId) {
        return this.databaseClient
            .execute("SELECT c.town as town, " +
            "percentile_cont(0.5) WITHIN GROUP (ORDER BY EXTRACT (YEAR FROM age(timezone('utc', now()), c.birth_date))) as p50, " +
            "percentile_cont(0.75) WITHIN GROUP (ORDER BY EXTRACT (YEAR FROM age(timezone('utc', now()), c.birth_date))) as p75, " +
            "percentile_cont(0.99) WITHIN GROUP (ORDER BY EXTRACT (YEAR FROM age(timezone('utc', now()), c.birth_date))) as p99 " +
            "FROM citizen c " +
            "WHERE c.import_id = :importId " +
            "GROUP BY town")
            .bind("importId", importId)
            .map((row, rowMetadata) -> new AgesForCityInfo(
                row.get("town", String.class),
                row.get("p50", Double.class),
                row.get("p75", Double.class),
                row.get("p99", Double.class)
            )).all().collectList();
    }

}
