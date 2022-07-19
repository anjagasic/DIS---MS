package se.magnus.microservices.core.client.service;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.client.Client;
import se.magnus.microservices.core.client.persistence.ClientEntity;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Client entityToApi(ClientEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ClientEntity apiToEntity(Client api);

    List<Client> entityListToApiList(List<ClientEntity> entity);

    List<ClientEntity> apiListToEntityList(List<Client> api);
}