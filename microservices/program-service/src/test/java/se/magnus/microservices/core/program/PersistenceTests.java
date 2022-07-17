package se.magnus.microservices.core.program;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import se.magnus.microservices.core.program.persistence.ProgramEntity;
import se.magnus.microservices.core.program.persistence.ProgramRepository;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class PersistenceTests {

    @Autowired
    private ProgramRepository repository;

    private ProgramEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll();

        ProgramEntity entity = new ProgramEntity(1, 1, "Program 1");
        savedEntity = repository.save(entity);

        assertEqualsComments(entity, savedEntity);
    }

    @Test
    public void create() {

        ProgramEntity newEntity = new ProgramEntity(1, 2, "Program 2");
        repository.save(newEntity);

        ProgramEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsComments(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void update() {
        savedEntity.setName("Program 2 updated");
        repository.save(savedEntity);

        ProgramEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long) foundEntity.getVersion());
        assertEquals("Program 2 updated", foundEntity.getName());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
    public void getByGymId() {
        List<ProgramEntity> entityList = repository.findByGymId(savedEntity.getGymId());

        assertEquals(entityList.size(), 1);
        assertEqualsComments(savedEntity, entityList.get(0));
    }

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        ProgramEntity entity1 = repository.findById(savedEntity.getId()).get();
        ProgramEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setName("optimistic lock name");
        repository.save(entity1);

        // Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e.
        // a Optimistic Lock Error
        try {
            entity2.setName("optimistic lock name fail");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
        }

        // Get the updated entity from the database and verify its new sate
        ProgramEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, updatedEntity.getVersion());
        assertEquals("optimistic lock name", updatedEntity.getName());
    }

    private void assertEqualsComments(ProgramEntity expectedEntity, ProgramEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getGymId(), actualEntity.getGymId());
        assertEquals(expectedEntity.getProgramId(), actualEntity.getProgramId());
        assertEquals(expectedEntity.getName(), actualEntity.getName());
    }

}