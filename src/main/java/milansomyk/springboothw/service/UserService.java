package milansomyk.springboothw.service;

import jakarta.annotation.security.RolesAllowed;
import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.exceptions.NotPremiumAccountException;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final UserMapper userMapper;
    private final CarMapper carMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DbUserDetailsService dbUserDetailsService;

    //Seller:
    public CarsResponse createCar(CarDto carDto, String username){
        Car car = carMapper.toCar(carDto);
        User user = userRepository.findByUsername(username);
        try{
            if(carLimit(user)){
                throw new NotPremiumAccountException("Not premium account! You can`t upload more than 1");
            }
        }catch (NotPremiumAccountException e){
            return new CarsResponse(null).setError(e.getMessage());
        }
        List<Car> cars = user.getCars();
        cars.add(car);
        user.setCars(cars);
        User saved = userRepository.save(user);
        return new CarsResponse(saved.getCars().stream().map(carMapper::toDto).toList());
    }
    public CarResponse editMyCar(int id, CarDto carDto, String username){
        User user = userRepository.findByUsername(username);
        List<Car> cars = user.getCars();
        try{
            isPersonalCarAndIndex(cars, id);
            Car car = carRepository.findById(id).get();
            car.update( carDto.getModel(), carDto.getProducer(), carDto.getYear(),
                    carDto.getPower(),carDto.getType(),carDto.getDetails(),carDto.getRunKm(),carDto.getEngineVolume(),
                    carDto.getColor(),carDto.getRegion(),carDto.getPlace(),carDto.getTransmission(),carDto.getGearbox(),
                    carDto.getPhoto());
            Car save = carRepository.save(car);
            return new CarResponse(carMapper.toDto(save));
        }catch (IllegalArgumentException e){
            return new CarResponse().setError(e.getMessage());
        }
    }
    public CarsResponse getMyCars(String username){
        User user = userRepository.findByUsername(username);
        List<Car> cars = user.getCars();
        return new CarsResponse(cars.stream().map(carMapper::toDto).toList());
    }

    public boolean isUsernameAlreadyExists(String username){
        return userRepository.findByUsername(username) != null;
    }
    public boolean isEmailAlreadyExists(String email){
        return userRepository.findByEmail(email) != null;
    }
    public boolean carLimit(User user){ return !(user.getPremium() || user.getCars().size()<1);}
    public void isPersonalCarAndIndex(List<Car> cars, int id){
        List<Integer> list = cars.stream().map(Car::getId).toList();
        int i = list.indexOf(id);
        if (!list.contains(id)){
            throw new IllegalArgumentException("Not legal argument");
        };
    }

}

