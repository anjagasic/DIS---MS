package se.magnus.microservices.core.employee.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface EmployeeRepository extends CrudRepository<EmployeeEntity, String> {

    List<EmployeeEntity> findByGymId(int gymId);
}