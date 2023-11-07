package milansomyk.springboothw.repository;

import milansomyk.springboothw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
