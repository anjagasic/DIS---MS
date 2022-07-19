package se.magnus.microservices.core.gym.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.gym.Gym;
import se.magnus.microservices.core.gym.persistence.GymEntity;

@Mapper(componentModel = "spring")
public interface GymMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Gym entityToApi(GymEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    GymEntity apiToEntity(Gym api);
}