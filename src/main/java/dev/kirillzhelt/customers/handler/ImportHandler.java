package dev.kirillzhelt.customers.handler;

import dev.kirillzhelt.customers.dto.CitizenDTO;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class ImportHandler {

    private static final Logger log = LoggerFactory.getLogger(ImportHandler.class);

    private final ImportRepository importRepository;
    private final CitizenRepository citizenRepository;
    private final RelativeRepository relativeRepository;

    public ImportHandler(ImportRepository importRepository, CitizenRepository citizenRepository, RelativeRepository relativeRepository) {
        this.importRepository = importRepository;
        this.citizenRepository = citizenRepository;
        this.relativeRepository = relativeRepository;
    }

    public Mono<ServerResponse> helloWorld(ServerRequest req) {
        return ok().bodyValue("Hello World");
    }

    public Mono<ServerResponse> addImport(ServerRequest req) {
        return req.bodyToMono(ImportDTO.class)
            .doOnError((e) -> {
                log.error("", e);
            })
            .zipWhen(newImport -> this.importRepository.save(new Import())) // validate before
            .zipWhen(t -> {
                int importId = t.getT2().getId();

                List<Citizen> citizens = t.getT1().getCitizens().stream().map(citizenDTO -> {
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
            })
            .zipWhen(t -> {
                int importId = t.getT1().getT2().getId();
                List<CitizenDTO> citizens = t.getT1().getT1().getCitizens();

                List<Relative> relatives = citizens
                    .stream()
                    .flatMap(citizen -> citizen
                        .getRelatives()
                        .stream()
                        .map(relativeId -> new Relative(importId, citizen.getCitizenId(), relativeId)))
                    .collect(Collectors.toList());

                return this.relativeRepository.saveAll(relatives).collectList();
            })
            .flatMap(id -> {
                Map<String, Integer> data = new HashMap<>();
                data.put("import_id", id.getT1().getT1().getT2().getId());
                return status(HttpStatus.CREATED).bodyValue(new DataResponseDTO<>(data));
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
