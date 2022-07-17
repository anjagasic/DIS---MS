package se.magnus.microservices.core.employee.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface EmployeeRepository extends CrudRepository<EmployeeEntity, String> {

    Optional<EmployeeEntity> findByGymId(int gymId);
}