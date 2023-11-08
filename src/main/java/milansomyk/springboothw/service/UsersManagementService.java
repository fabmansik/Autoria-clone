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
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
public class UsersManagementService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final CarRepository carRepository;

    public void deleteUserById(int id){
        userRepository.deleteById(id);
    }
    //Manager:
    public CarsResponse getAllCars(){
        return new CarsResponse(carRepository.findAll().stream().map(carMapper::toDto).toList());
    }
    public List<UserDto> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    public UserResponse banUser(int id){
        User foundUser = userRepository.findById(id).get();
        foundUser.setEnabled(false);
        User saved = userRepository.save(foundUser);
        return new UserResponse(userMapper.toDto(saved), null);
    }
    @RolesAllowed("{MANAGER, ADMIN}")
    public String deleteById(int id){
        carRepository.deleteById(id);
        return "Car with this id: "+id+", was deleted";
    }
    @RolesAllowed("{MANAGER, ADMIN}")
    public CarResponse findById(int id){
        Car car = carRepository.findById(id).get();
        CarDto dto = carMapper.toDto(car);
        return new CarResponse(dto);
    }
    //Admin:
    @RolesAllowed("ADMIN")
    public UserDto createManager(UserDto userDto){
        User user = userMapper.fromDto(userDto);
        user.setRole(Role.MANAGER.name());
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
    @RolesAllowed("ADMIN")
    public UserDto setManager(int id){
        User user = userRepository.findById(id).get();
        user.setRole("MANAGER");
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
