package milansomyk.springboothw.repository;

import milansomyk.springboothw.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> findByProducerAndModelAndActive(String producer, String model, boolean active);
    List<Car> findByProducerAndModelAndActiveAndRegion(String producer, String model, boolean active, String region);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "SELECT * FROM cars WHERE cars.active = true")
    List<Car> findAllActive();

    @Modifying
    @Transactional
    @Query("update Car set watchesPerDay = 0")
    void nullCarWatchesPerDay();
    @Modifying
    @Transactional
    @Query("update Car set watchesPerWeek = 0")
    void nullCarWatchesPerWeek();
    @Modifying
    @Transactional
    @Query("update Car set watchesPerMonth = 0")
    void nullCarWatchesPerMonth();

}
