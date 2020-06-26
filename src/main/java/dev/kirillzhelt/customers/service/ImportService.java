package dev.kirillzhelt.customers.service;

import dev.kirillzhelt.customers.dto.ImportCitizenDTO;
import dev.kirillzhelt.customers.dto.ImportDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ImportService {

    public Mono<Integer> addImport(Mono<ImportDTO> importMono);
    Mono<Boolean> importExists(int importId);
    Mono<List<ImportCitizenDTO>> getImport(int importId);

}
