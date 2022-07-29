package se.magnus.microservices.core.gym;

import com.mongodb.DuplicateKeyException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.gym.persistence.GymEntity;
import se.magnus.microservices.core.gym.persistence.GymRepository;

import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@ContextConfiguration
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private GymRepository repository;

    private GymEntity savedGymEntity;

    @Before
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
        GymEntity entity = new GymEntity(1, "Top Form", "Rumenacka 24, Novi Sad");
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedGymEntity = createdEntity;
                    return areGymEqual(entity, savedGymEntity);
                }).verifyComplete();
    }

    @Test
    public void createGym() {
        GymEntity newEntity = new GymEntity(2, "Addiction", "Novi Sad");
        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(createdEntity ->
                        newEntity.getGymId() == createdEntity.getGymId())
                .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> areGymEqual(newEntity, foundEntity))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
    public void updateGym() {
        savedGymEntity.setName("name2");
        StepVerifier.create(repository.save(savedGymEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("nameUpdated"))
                .verifyComplete();

        StepVerifier.create(repository.findById(savedGymEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getName().equals("name2"))
                .verifyComplete();
    }

    @Test
    public void deleteGym() {
        StepVerifier.create(repository.delete(savedGymEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedGymEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
    public void getByGymId() {
        StepVerifier.create(repository.findByGymId(savedGymEntity.getGymId()))
                .expectNextMatches(foundEntity -> areGymEqual(savedGymEntity, foundEntity))
                .verifyComplete();
    }

    @Test
    public void optimisticLockError() {
        // Store the saved entity in two separate entity objects
        GymEntity entity1 = repository.findById(savedGymEntity.getId()).block();
        GymEntity entity2 = repository.findById(savedGymEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1).block();

        // Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e.
        // a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedGymEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getName().equals("n1"))
                .verifyComplete();
    }

    @Test
    public void duplicateError() {
        GymEntity entity = new GymEntity(savedGymEntity.getGymId(), "name", "address");
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    private Boolean areGymEqual(GymEntity expectedEntity, GymEntity actualEntity) {
        return (expectedEntity.getId().equals(actualEntity.getId())) &&
                (expectedEntity.getVersion() == actualEntity.getVersion()) &&
                (expectedEntity.getGymId() == actualEntity.getGymId()) &&
                (expectedEntity.getName().equals(actualEntity.getName())) &&
                (expectedEntity.getAddress().equals(actualEntity.getAddress()));
    }
}