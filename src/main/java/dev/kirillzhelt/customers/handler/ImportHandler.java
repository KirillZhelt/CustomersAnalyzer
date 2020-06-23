package dev.kirillzhelt.customers.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class ImportHandler {

    public Mono<ServerResponse> helloWorld(ServerRequest req) {


        return ok().bodyValue("Hello World");
    }

    public Mono<ServerResponse> addImport(ServerRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
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
