package se.magnus.microservices.core.gym;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.gym.persistence.GymEntity;
import se.magnus.microservices.core.gym.persistence.GymRepository;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private GymRepository repository;

    private GymEntity savedGymEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        GymEntity entity = new GymEntity(1, 1, "Top Form", "Rumenacka 24, Novi Sad");
        savedGymEntity = repository.save(entity);

        assertEqualsEmployee(entity, savedGymEntity);
    }

    @Test
    public void createEmployee() {

        GymEntity newEntity = new GymEntity(1, 2, "Addiction", "Novi Sad");
        repository.save(newEntity);

        GymEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsEmployee(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void updateEmployee() {
        savedGymEntity.setName("name2");
        repository.save(savedGymEntity);

        GymEntity foundEntity = repository.findById(savedGymEntity.getId()).get();
        assertEquals(2, foundEntity.getVersion());
        assertEquals("name2", foundEntity.getName());
    }

    @Test
    public void deleteEmployee() {
        repository.delete(savedGymEntity);
        assertFalse(repository.existsById(savedGymEntity.getId()));
    }

    @Test
    public void getByInsuranceCompanyId() {
        List<GymEntity> entityList = repository.findByGymId(savedGymEntity.getGymId());

        MatcherAssert.assertThat(entityList, hasSize(1));
        assertEqualsEmployee(savedGymEntity, entityList.get(0));
    }

    @Test
    public void optimisticLockError() {

        GymEntity entity1 = repository.findById(savedGymEntity.getId()).get();
        GymEntity entity2 = repository.findById(savedGymEntity.getId()).get();

        entity1.setName("name2");
        repository.save(entity1);

        try {
            entity2.setName("name3");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
        }
        GymEntity updatedEntity = repository.findById(savedGymEntity.getId()).get();
        assertEquals(2, updatedEntity.getVersion());
        assertEquals("name2", updatedEntity.getName());
    }

    private void assertEqualsEmployee(GymEntity expectedEntity, GymEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getGymId(), actualEntity.getGymId());
        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getAddress(), actualEntity.getAddress());
    }
}