package dev.kirillzhelt.customers.handler;

import dev.kirillzhelt.customers.dto.*;
import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.Import;
import dev.kirillzhelt.customers.entity.projection.AgesForCityInfo;
import dev.kirillzhelt.customers.entity.projection.PresentsInfo;
import dev.kirillzhelt.customers.entity.Relative;
import dev.kirillzhelt.customers.repository.CitizenRepository;
import dev.kirillzhelt.customers.repository.ImportRepository;
import dev.kirillzhelt.customers.repository.RelativeRepository;
import dev.kirillzhelt.customers.service.CitizenService;
import dev.kirillzhelt.customers.service.ImportService;
import dev.kirillzhelt.customers.service.RelativeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    private final ImportService importService;
    private final CitizenService citizenService;
    private final RelativeService relativeService;

    private final Validator validator;

    public ImportHandler(ImportRepository importRepository, CitizenRepository citizenRepository,
                         RelativeRepository relativeRepository, ImportService importService, CitizenService citizenService,
                         RelativeService relativeService, Validator validator) {
        this.importRepository = importRepository;
        this.citizenRepository = citizenRepository;
        this.relativeRepository = relativeRepository;

        this.importService = importService;
        this.citizenService = citizenService;
        this.relativeService = relativeService;

        this.validator = validator;
    }

    public Mono<ServerResponse> helloWorld(ServerRequest req) {
        return ok().bodyValue("Hello World");
    }

    public Mono<ServerResponse> addImport(ServerRequest req) {
        return this.importService.addImport(req.bodyToMono(ImportDTO.class))
            .flatMap(id -> {
                Map<String, Integer> data = new HashMap<>();
                data.put("import_id", id);
                return status(HttpStatus.CREATED).bodyValue(new DataResponseDTO<>(data));
            })
            .onErrorResume(err -> {
                log.error("", err);
                return ServerResponse.badRequest().build();
            });
    }

    public Mono<ServerResponse> patchCitizen(ServerRequest req) {
        try {
            Integer importId = Integer.parseInt(req.pathVariable("importId"));
            Integer citizenId = Integer.parseInt(req.pathVariable("citizenId"));

            return this.citizenService.patchCitizen(req.bodyToMono(PatchCitizenDTO.class), importId, citizenId)
                .flatMap(updatedCitizen -> ok().bodyValue(new DataResponseDTO<>(updatedCitizen)))
                .onErrorResume(err -> {
                    log.error("", err);
                    return badRequest().build();
                });
        } catch (NumberFormatException ex) {
            return badRequest().build();
        }
    }

    public Mono<ServerResponse> getImport(ServerRequest req) {
        try {
            int importId = Integer.parseInt(req.pathVariable("importId"));

            return this.importService.importExists(importId).flatMap(exists -> {
                if (exists) {
                    return this.importService.getImport(importId)
                        .flatMap(citizens -> ok().bodyValue(new DataResponseDTO<>(citizens)));
                } else {
                    return notFound().build();
                }
            });
        } catch (NumberFormatException ex) {
            return badRequest().build();
        }
    }

    public Mono<ServerResponse> countPresents(ServerRequest req) {
        try {
            int importId = Integer.parseInt(req.pathVariable("importId"));

            return this.importService.importExists(importId).flatMap(exists -> {
               if (exists) {
                   return this.relativeService.countPresentsPerMonth(importId).flatMap(presentsData ->
                       ok().bodyValue(new DataResponseDTO<Map<Integer, Collection<PresentsDTO>>>(presentsData)));
               } else {
                   return notFound().build();
               }
            });
        } catch (NumberFormatException ex) {
            return badRequest().build();
        }
    }

    public Mono<ServerResponse> countStatistics(ServerRequest req) {
        try {
            int importId = Integer.parseInt(req.pathVariable("importId"));

            return this.importService.importExists(importId).flatMap(exists -> {
                if (exists) {
                    return this.citizenService.countPercentilesForCity(importId).flatMap(percentiles -> {
                        System.out.println(percentiles.get(0).getTown());
                        DataResponseDTO<List<AgesForCityInfo>> dataResponse = new DataResponseDTO<>(percentiles);
                        return ok().bodyValue(dataResponse);
                    });
                } else {
                    return notFound().build();
                }
            });
        } catch (NumberFormatException ex) {
            return badRequest().build();
        }
    }

}
