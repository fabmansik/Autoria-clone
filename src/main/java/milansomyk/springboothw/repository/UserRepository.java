package milansomyk.springboothw.repository;

import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByCarsContaining(Car car);
    Optional<User> findByPhone(Integer phone);
    Optional<List<User>> findByRole(String role);
}
