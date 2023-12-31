package milansomyk.springboothw.repository;

import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByCarsContaining(Car car);
    User findByPhone(Integer phone);
    List<User> findByRole(String role);
//    @Modifying
//    @Transactional
//    @Query(value ="update User u set u.cars = :cars where u.id = :id")
////    @Query(nativeQuery = true, value = "INSERT INTO User.cars ()")
//    void updateUserById(@Param("id")Integer id,@Param("cars") List<Car> cars);

}
