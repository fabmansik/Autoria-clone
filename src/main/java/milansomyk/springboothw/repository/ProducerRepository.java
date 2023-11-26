package milansomyk.springboothw.repository;

import milansomyk.springboothw.entity.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Integer> {
    Optional<Producer> findProducerByName(String producer);
}
