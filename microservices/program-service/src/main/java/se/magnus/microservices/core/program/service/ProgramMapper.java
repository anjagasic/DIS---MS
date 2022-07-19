package se.magnus.microservices.core.program.service;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.program.Program;
import se.magnus.microservices.core.program.persistence.ProgramEntity;

@Mapper(componentModel = "spring")
public interface ProgramMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Program entityToApi(ProgramEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ProgramEntity apiToEntity(Program api);

    List<Program> entityListToApiList(List<ProgramEntity> entity);

    List<ProgramEntity> apiListToEntityList(List<Program> api);
}