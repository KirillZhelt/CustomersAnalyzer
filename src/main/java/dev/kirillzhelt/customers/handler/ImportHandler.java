package dev.kirillzhelt.customers.handler;

import dev.kirillzhelt.customers.dto.ImportCitizenDTO;
import dev.kirillzhelt.customers.dto.DataResponseDTO;
import dev.kirillzhelt.customers.dto.ImportDTO;
import dev.kirillzhelt.customers.dto.PatchCitizenDTO;
import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.Import;
import dev.kirillzhelt.customers.entity.Relative;
import dev.kirillzhelt.customers.repository.CitizenRepository;
import dev.kirillzhelt.customers.repository.ImportRepository;
import dev.kirillzhelt.customers.repository.RelativeRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class ImportHandler {

    private static final Logger log = LoggerFactory.getLogger(ImportHandler.class);

    private final ImportRepository importRepository;
    private final CitizenRepository citizenRepository;
    private final RelativeRepository relativeRepository;

    private final Validator validator;

    public ImportHandler(ImportRepository importRepository, CitizenRepository citizenRepository,
                         RelativeRepository relativeRepository, Validator validator) {
        this.importRepository = importRepository;
        this.citizenRepository = citizenRepository;
        this.relativeRepository = relativeRepository;

        this.validator = validator;
    }

    public Mono<ServerResponse> helloWorld(ServerRequest req) {
        return ok().bodyValue("Hello World");
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

    public Mono<ServerResponse> addImport(ServerRequest req) {
        return req.bodyToMono(ImportDTO.class)
            .doOnNext(this::validateImportData)
            .zipWhen(newImport -> this.importRepository.save(new Import()))
            .zipWhen(this::saveCitizens)
            .zipWhen(this::saveRelatives)
            .flatMap(id -> {
                Map<String, Integer> data = new HashMap<>();
                data.put("import_id", id.getT1().getT1().getT2().getId());
                return status(HttpStatus.CREATED).bodyValue(new DataResponseDTO<>(data));
            })
            .onErrorResume(err -> {
                log.error("", err);
                return ServerResponse.badRequest().build();
            });
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

    public Mono<ServerResponse> patchCitizen(ServerRequest req) {
        try {
            Integer importId = Integer.parseInt(req.pathVariable("importId"));
            Integer citizenId = Integer.parseInt(req.pathVariable("citizenId"));

            Mono<Citizen> citizenMono = this.citizenRepository
                .findByImportIdAndCitizenId(importId, citizenId)
                .doOnNext(citizen -> {
                    if (citizen == null) {
                        throw new IllegalArgumentException("No citizen with such import id and citizen id");
                    }
                });

            Mono<PatchCitizenDTO> patchCitizenMono = req.bodyToMono(PatchCitizenDTO.class)
                .doOnNext(this::validatePatchCitizen);

            return citizenMono.zipWith(patchCitizenMono)
                .flatMap(this::updateCitizen)
                .zipWhen(this::updateRelatives)
                .flatMap(updatedCitizen -> {
                    ImportCitizenDTO importCitizen = this.mapCitizenToDTO(updatedCitizen.getT1().getT1(), updatedCitizen.getT2());
                    return ok().bodyValue(new DataResponseDTO<>(importCitizen));
                })
                .onErrorResume(err -> {
                    log.error("", err);
                    return badRequest().build();
                });
        } catch (NumberFormatException ex) {
            return badRequest().build();
        }
    }

    private Mono<Tuple2<List<Relative>, Citizen>> findRelativesForCitizen(Citizen citizen, Integer importId) {
        Mono<List<Relative>> relatives = this.relativeRepository.findAllByCitizenIdAndImportId(citizen.getCitizenId(), importId).collectList();
        return relatives.zipWith(Mono.just(citizen));
    }

    private ImportCitizenDTO mapCitizenToDTO(Tuple2<List<Relative>, Citizen> citizenAndRelatives) {
        return this.mapCitizenToDTO(citizenAndRelatives.getT2(), citizenAndRelatives.getT1());
    }

    private ImportCitizenDTO mapCitizenToDTO(Citizen citizen, List<Relative> relatives) {
        ImportCitizenDTO importCitizen = new ImportCitizenDTO();
        importCitizen.setCitizenId(citizen.getCitizenId());
        importCitizen.setTown(citizen.getTown());
        importCitizen.setStreet(citizen.getStreet());
        importCitizen.setBuilding(citizen.getBuilding());
        importCitizen.setApartment(citizen.getApartment());
        importCitizen.setName(citizen.getName());
        importCitizen.setBirthDate(citizen.getBirthDate());
        importCitizen.setGender(citizen.getGender());
        importCitizen.setRelatives(relatives.stream().filter(relative -> relative.getCitizenId() == citizen.getCitizenId()).map(Relative::getRelativeId).collect(Collectors.toList()));

        return importCitizen;
    }

    public Mono<ServerResponse> getImport(ServerRequest req) {
        try {
            int importId = Integer.parseInt(req.pathVariable("importId"));

            return this.importRepository.existsById(importId).flatMap(exists -> {
                if (exists) {
                    Mono<List<ImportCitizenDTO>> citizensMono =
                        this.citizenRepository.findAllByImportId(importId)
                            .flatMap(citizen -> this.findRelativesForCitizen(citizen, importId))
                            .map(this::mapCitizenToDTO)
                            .collect(Collectors.toList());

                    return citizensMono
                        .flatMap(citizens -> ok().bodyValue(new DataResponseDTO<>(citizens)));
                } else {
                    return status(HttpStatus.BAD_REQUEST).build();
                }
            });
        } catch (NumberFormatException ex) {
            return status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public Mono<ServerResponse> countPresents(ServerRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    public Mono<ServerResponse> countStatistics(ServerRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
