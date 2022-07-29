package se.magnus.microservices.core.employee.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends ReactiveCrudRepository<EmployeeEntity, String> {

    Flux<EmployeeEntity> findByGymId(int gymId);
}