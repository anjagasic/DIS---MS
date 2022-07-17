package se.magnus.microservices.core.program.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProgramRepository extends CrudRepository<ProgramEntity, Integer> {

    @Transactional(readOnly = true)
    List<ProgramEntity> findByGymId(int gymId);
}
