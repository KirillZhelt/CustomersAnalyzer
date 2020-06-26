package dev.kirillzhelt.customers.service;

import dev.kirillzhelt.customers.dto.ImportCitizenDTO;
import dev.kirillzhelt.customers.dto.ImportDTO;
import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.Import;
import dev.kirillzhelt.customers.entity.Relative;
import dev.kirillzhelt.customers.repository.CitizenRepository;
import dev.kirillzhelt.customers.repository.ImportRepository;
import dev.kirillzhelt.customers.repository.RelativeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

    private final ImportRepository importRepository;
    private final CitizenRepository citizenRepository;
    private final RelativeRepository relativeRepository;

    private final Validator validator;

    public ImportServiceImpl(ImportRepository importRepository, CitizenRepository citizenRepository,
                             RelativeRepository relativeRepository, Validator validator) {
        this.importRepository = importRepository;
        this.citizenRepository = citizenRepository;
        this.relativeRepository = relativeRepository;

        this.validator = validator;
    }

    private Set<Integer> getCitizenIds(ImportDTO newImport) {
        return newImport.getCitizens()
            .stream()
            .map(ImportCitizenDTO::getCitizenId)
            .collect(Collectors.toSet());
    }

    private void checkCitizenIds(ImportDTO newImport) {
        Set<Integer> citizenIds = this.getCitizenIds(newImport);

        if (citizenIds.size() != newImport.getCitizens().size()) {
            throw new ValidationException("Citizens ids are not unique");
        }
    }

    private void checkRelations(ImportDTO newImport) {
        Map<Integer, Set<Integer>> relationsForCitizenId = new HashMap<>();
        newImport.getCitizens().forEach(citizen -> {
            relationsForCitizenId.put(citizen.getCitizenId(), new HashSet<>(citizen.getRelatives()));
        });

        newImport.getCitizens().forEach(citizen -> {
            citizen.getRelatives().forEach(relativeId -> {
                Set<Integer> otherRelatives = relationsForCitizenId.get(relativeId);
                if (otherRelatives == null || !otherRelatives.contains(citizen.getCitizenId())) {
                    throw new ValidationException("Invalid id in relatives field");
                }
            });
        });
    }

    private void validateImportData(ImportDTO newImport) {
        this.checkCitizenIds(newImport);
        this.checkRelations(newImport);

        newImport.getCitizens().forEach(citizen -> {
            Set<ConstraintViolation<ImportCitizenDTO>> violations = this.validator.validate(citizen);
            log.info("Size of set of constraints violations: {}", violations.size());

            if (violations.size() != 0) {
                throw new ValidationException(String.format("Violations: %s", violations));
            }
        });
    }

    private Mono<List<Citizen>> saveCitizens(Tuple2<ImportDTO, Import> savedImport) {
        int importId = savedImport.getT2().getId();

        List<Citizen> citizens = savedImport.getT1().getCitizens().stream().map(citizenDTO -> {
            return new Citizen(
                importId,
                citizenDTO.getCitizenId(),
                citizenDTO.getTown(),
                citizenDTO.getStreet(),
                citizenDTO.getBuilding(),
                citizenDTO.getApartment(),
                citizenDTO.getName(),
                citizenDTO.getBirthDate(),
                citizenDTO.getGender()
            );
        }).collect(Collectors.toList());

        return this.citizenRepository.saveAll(citizens).collectList();
    }

    private Mono<List<Relative>> saveRelatives(Tuple2<Tuple2<ImportDTO, Import>, List<Citizen>> savedData) {
        int importId = savedData.getT1().getT2().getId();
        List<ImportCitizenDTO> citizens = savedData.getT1().getT1().getCitizens();

        List<Relative> relatives = citizens
            .stream()
            .flatMap(citizen -> citizen
                .getRelatives()
                .stream()
                .map(relativeId -> new Relative(importId, citizen.getCitizenId(), relativeId)))
            .collect(Collectors.toList());

        return this.relativeRepository.saveAll(relatives).collectList();
    }

    public Mono<Integer> addImport(Mono<ImportDTO> importMono) {
        return importMono
            .doOnNext(this::validateImportData)
            .zipWhen(newImport -> this.importRepository.save(new Import()))
            .zipWhen(this::saveCitizens)
            .zipWhen(this::saveRelatives)
            .map(t -> t.getT1().getT1().getT2().getId());
    }

    public Mono<Boolean> importExists(int importId) {
        return this.importRepository.existsById(importId);
    }

    private Mono<Tuple2<List<Relative>, Citizen>> findRelativesForCitizen(Citizen citizen, Integer importId) {
        Mono<List<Relative>> relatives = this.relativeRepository.findAllByCitizenIdAndImportId(citizen.getCitizenId(), importId).collectList();
        return relatives.zipWith(Mono.just(citizen));
    }

    private ImportCitizenDTO mapCitizenToDTO(Tuple2<List<Relative>, Citizen> citizenAndRelatives) {
        return CitizenService.mapCitizenToDTO(citizenAndRelatives.getT2(), citizenAndRelatives.getT1());
    }

    public Mono<List<ImportCitizenDTO>> getImport(int importId) {
        return this.citizenRepository.findAllByImportId(importId)
            .flatMap(citizen -> this.findRelativesForCitizen(citizen, importId))
            .map(this::mapCitizenToDTO)
            .collect(Collectors.toList());
    }
}
