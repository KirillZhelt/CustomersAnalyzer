package dev.kirillzhelt.customers;

import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.Import;
import dev.kirillzhelt.customers.entity.Relative;
import dev.kirillzhelt.customers.entity.util.Gender;
import dev.kirillzhelt.customers.repository.CitizenRepository;
import dev.kirillzhelt.customers.repository.ImportRepository;
import dev.kirillzhelt.customers.repository.RelativeRepository;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner addImports(ImportRepository importRepository, CitizenRepository citizenRepository, RelativeRepository relativeRepository) {
//        return (args) -> {
//            Import newImport = new Import();
//            newImport = importRepository.save(newImport).block();
//
//            Import newImport1 = new Import();
//            importRepository.save(newImport1).block();
//
//            Citizen citizen = new Citizen(newImport.getId(), 5, "town", "street", "building", 5, "name",
//                LocalDate.now(), Gender.MALE);
//            Citizen citizen1 = new Citizen(newImport.getId(), 6, "town", "street", "building", 5, "name",
//                LocalDate.now(), Gender.MALE);
//            citizen = citizenRepository.save(citizen).block();
//            citizen1 = citizenRepository.save(citizen1).block();
//
//            Relative relative = new Relative(newImport.getId(), citizen.getCitizenId(), citizen1.getCitizenId());
//            relativeRepository.save(relative).block();
//        };
//    }

}
