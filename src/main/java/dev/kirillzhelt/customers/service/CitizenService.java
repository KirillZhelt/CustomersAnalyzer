package dev.kirillzhelt.customers.service;

import dev.kirillzhelt.customers.dto.ImportCitizenDTO;
import dev.kirillzhelt.customers.dto.PatchCitizenDTO;
import dev.kirillzhelt.customers.entity.Citizen;
import dev.kirillzhelt.customers.entity.Relative;
import dev.kirillzhelt.customers.entity.projection.AgesForCityInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public interface CitizenService {

    Mono<ImportCitizenDTO> patchCitizen(Mono<PatchCitizenDTO> patchCitizenMono, Integer importId, Integer citizenId);
    Mono<List<AgesForCityInfo>> countPercentilesForCity(int importId);

    static ImportCitizenDTO mapCitizenToDTO(Citizen citizen, List<Relative> relatives) {
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

}
