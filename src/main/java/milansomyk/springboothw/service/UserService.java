package milansomyk.springboothw.service;

import jakarta.persistence.NonUniqueResultException;
import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.ErrorDto;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.exceptions.UserAlreadyExistAuthenticationException;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DbUserDetailsService dbUserDetailsService;

    public Optional<UserDto> getById(int id){
        User user = userRepository.findById(id).get();
        UserDto userDto = userMapper.toDto(user);
        return Optional.ofNullable(userDto);
    }
    public List<UserDto> getAll(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    public UserDto create(UserDto userDto) throws NonUniqueResultException {
        System.out.println(userDto);
        User user = userMapper.fromDto(userDto);
        String encoded = user.getPassword();
        user.setPassword(encoded);
        User savedUser = userRepository.save(user.setPremium(false).setEnabled(true).setRole(Role.SELLER.toString()));
        return userMapper.toDto(savedUser);
    }

    public void deleteById(int id){
        userRepository.deleteById(id);
    }

}
