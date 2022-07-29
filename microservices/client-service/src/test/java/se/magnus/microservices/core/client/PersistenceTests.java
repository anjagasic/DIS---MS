package se.magnus.microservices.core.client;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.client.persistence.ClientEntity;
import se.magnus.microservices.core.client.persistence.ClientRepository;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;


import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private ClientRepository repository;

    private ClientEntity savedClientEntity;

    @Before
    public void setupDb() {
        repository.deleteAll().block();

        ClientEntity entity = new ClientEntity(1, 1, "Milica Gasic", "Female", "22");
        savedClientEntity = repository.save(entity).block();

        assertEqualsEmployee(entity, savedClientEntity);
    }

    @Test
    public void createEmployee() {
        ClientEntity newEntity = new ClientEntity(1, 2, "Anja Gasic", "Female", "25");
        repository.save(newEntity).block();

        ClientEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsEmployee(newEntity, foundEntity);

        assertEquals(2, repository.count().block());
    }

    @Test
    public void updateEmployee() {
        savedClientEntity.setFullName("name2");
        repository.save(savedClientEntity).block();

        ClientEntity foundEntity = repository.findById(savedClientEntity.getId()).block();
        assertEquals(2, foundEntity.getVersion());
        assertEquals("name2", foundEntity.getFullName());
    }

    @Test
    public void deleteEmployee() {
        repository.delete(savedClientEntity).block();
        assertFalse(repository.existsById(savedClientEntity.getId()).block());
    }

    @Test
    public void getByInsuranceCompanyId() {
        List<ClientEntity> entityList = repository.findByGymId(savedClientEntity.getGymId()).collectList().block();

        MatcherAssert.assertThat(entityList, hasSize(1));
        assertEqualsEmployee(savedClientEntity, entityList.get(0));
    }

    @Test
    public void optimisticLockError() {

        ClientEntity entity1 = repository.findById(savedClientEntity.getId()).block();
        ClientEntity entity2 = repository.findById(savedClientEntity.getId()).block();

        entity1.setFullName("name2");
        repository.save(entity1).block();

        try {
            entity2.setFullName("name3");
            repository.save(entity2).block();

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
        }
        ClientEntity updatedEntity = repository.findById(savedClientEntity.getId()).block();
        assertEquals(2, updatedEntity.getVersion());
        assertEquals("name2", updatedEntity.getFullName());
    }

    private void assertEqualsEmployee(ClientEntity expectedEntity, ClientEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getGymId(), actualEntity.getGymId());
        assertEquals(expectedEntity.getClientId(), actualEntity.getClientId());
        assertEquals(expectedEntity.getFullName(), actualEntity.getFullName());
        assertEquals(expectedEntity.getGender(), actualEntity.getGender());
        assertEquals(expectedEntity.getAge(), actualEntity.getAge());
    }
}