package dev.kirillzhelt.customers.handler;

import dev.kirillzhelt.customers.dto.ImportCitizenDTO;
import dev.kirillzhelt.customers.dto.DataResponseDTO;
import dev.kirillzhelt.customers.dto.ImportDTO;
import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.Import;
import dev.kirillzhelt.customers.entity.Relative;
import dev.kirillzhelt.customers.repository.CitizenRepository;
import dev.kirillzhelt.customers.repository.ImportRepository;
import dev.kirillzhelt.customers.repository.RelativeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
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

    private void validateImportData(ImportDTO newImport) {
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

    public Mono<ServerResponse> patchCitizen(ServerRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    public Mono<ServerResponse> getImport(ServerRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    public Mono<ServerResponse> countPresents(ServerRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    public Mono<ServerResponse> countStatistics(ServerRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
