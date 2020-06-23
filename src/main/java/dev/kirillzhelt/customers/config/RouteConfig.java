package dev.kirillzhelt.customers.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.kirillzhelt.customers.handler.ImportHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Configuration
public class RouteConfig {

    private final ImportHandler importHandler;

    public RouteConfig(ImportHandler importHandler) {
        this.importHandler = importHandler;
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    RouterFunction<ServerResponse> routes() {
        return route(GET("/hello-world"), this.importHandler::helloWorld)
            .and(route(POST("/imports"), this.importHandler::addImport))
            .and(route(PATCH("/imports/{importId}/citizens/{citizen_id}"),
                this.importHandler::patchCitizen))
            .and(route(GET("/imports/{importId}/citizens"),
                this.importHandler::getImport))
            .and(route(GET("/imports/{importId}/citizens/birthdays"),
                this.importHandler::countPresents))
            .and(route(GET("/imports/{importId}/towns/percentile/age"),
                this.importHandler::countStatistics));
    }
}
