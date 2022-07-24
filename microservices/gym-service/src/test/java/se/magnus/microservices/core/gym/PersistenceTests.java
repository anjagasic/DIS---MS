package se.magnus.microservices.core.gym;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.gym.persistence.GymEntity;
import se.magnus.microservices.core.gym.persistence.GymRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

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
    public void createGym() {

        GymEntity newEntity = new GymEntity(2, 2, "Addiction", "Novi Sad");
        repository.save(newEntity);

        GymEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsEmployee(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void updateGym() {
        savedGymEntity.setName("name2");
        repository.save(savedGymEntity);

        GymEntity foundEntity = repository.findById(savedGymEntity.getId()).get();
        assertEquals(2, foundEntity.getVersion());
        assertEquals("name2", foundEntity.getName());
    }

    @Test
    public void deleteGym() {
        repository.delete(savedGymEntity);
        assertFalse(repository.existsById(savedGymEntity.getId()));
    }

    @Test
    public void getByEmployeeId() {
        Optional<GymEntity> entityList = repository.findByGymId(savedGymEntity.getGymId());

        assertTrue(entityList.isPresent());
        assertEqualsEmployee(savedGymEntity, entityList.get());
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

    @Test
    public void paging() {

        repository.deleteAll();

        List<GymEntity> newInsuranceCompanies = rangeClosed(1001, 1010)
                .mapToObj(i -> new GymEntity(i, i,"name"+i, "address"+i))
                .collect(Collectors.toList());
        repository.saveAll(newInsuranceCompanies);

        Pageable nextPage = PageRequest.of(0, 4, ASC, "gymId");
        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
    }

    private Pageable testNextPage(Pageable nextPage, String gymIds, boolean expectsNextPage) {
        Page<GymEntity> gymPage = repository.findAll(nextPage);
        assertEquals(gymIds, gymPage.getContent().stream().map(p -> p.getGymId()).collect(Collectors.toList()).toString());
        assertEquals(expectsNextPage, gymPage.hasNext());
        return gymPage.nextPageable();
    }

    private void assertEqualsEmployee(GymEntity expectedEntity, GymEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getGymId(), actualEntity.getGymId());
        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getAddress(), actualEntity.getAddress());
    }
}