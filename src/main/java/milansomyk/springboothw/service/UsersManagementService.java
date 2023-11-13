package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //Manager:
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
    //Admin:

    public UserResponse createManager(UserDto userDto){
        User user = userMapper.fromDto(userDto);
        try{
            userService.isUsernameAlreadyExists(user.getUsername());
            userService.isEmailAlreadyExists(user.getEmail());
        }catch (IllegalArgumentException e){
            return new UserResponse(null,e.getMessage());
        }
        user.setRole(Role.MANAGER.name());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return new UserResponse(userMapper.toDto(saved), null);
    }
    public UserDto setManager(int id){
        User user = userRepository.findById(id).get();
        user.setRole("MANAGER");
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
    public String deleteUserById(int id){
        userRepository.deleteById(id);
        return "user with id: "+id+" was deleted";
    }
    public String setPremium(int id){
        User user = userRepository.findById(id).get();
        user.setPremium(true);
        userRepository.save(user);
        return "user with id: "+id+" was set to premium account";
    }
}
