package se.magnus.microservices.core.employee;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.employee.persistence.EmployeeEntity;
import se.magnus.microservices.core.employee.persistence.EmployeeRepository;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;


import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private EmployeeRepository repository;

    private EmployeeEntity savedEmployeeEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        EmployeeEntity entity = new EmployeeEntity(1, 1, "name1");
        savedEmployeeEntity = repository.save(entity);

        assertEqualsEmployee(entity, savedEmployeeEntity);
    }

    @Test
    public void createEmployee() {

        EmployeeEntity newEntity = new EmployeeEntity(1, 2, "name2");
        repository.save(newEntity);

        EmployeeEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsEmployee(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void updateEmployee() {
        savedEmployeeEntity.setFullName("name2");
        repository.save(savedEmployeeEntity);

        EmployeeEntity foundEntity = repository.findById(savedEmployeeEntity.getId()).get();
        assertEquals(2, foundEntity.getVersion());
        assertEquals("name2", foundEntity.getFullName());
    }

    @Test
    public void deleteEmployee() {
        repository.delete(savedEmployeeEntity);
        assertFalse(repository.existsById(savedEmployeeEntity.getId()));
    }

    @Test
    public void getByInsuranceCompanyId() {
        List<EmployeeEntity> entityList = repository.findByGymId(savedEmployeeEntity.getGymId());

        MatcherAssert.assertThat(entityList, hasSize(1));
        assertEqualsEmployee(savedEmployeeEntity, entityList.get(0));
    }

    @Test
    public void optimisticLockError() {

        EmployeeEntity entity1 = repository.findById(savedEmployeeEntity.getId()).get();
        EmployeeEntity entity2 = repository.findById(savedEmployeeEntity.getId()).get();

        entity1.setFullName("name2");
        repository.save(entity1);

        try {
            entity2.setFullName("name3");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
        }
        EmployeeEntity updatedEntity = repository.findById(savedEmployeeEntity.getId()).get();
        assertEquals(2, updatedEntity.getVersion());
        assertEquals("name2", updatedEntity.getFullName());
    }

    private void assertEqualsEmployee(EmployeeEntity expectedEntity, EmployeeEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getGymId(), actualEntity.getGymId());
        assertEquals(expectedEntity.getEmployeeId(), actualEntity.getEmployeeId());
        assertEquals(expectedEntity.getFullName(), actualEntity.getFullName());
    }
}