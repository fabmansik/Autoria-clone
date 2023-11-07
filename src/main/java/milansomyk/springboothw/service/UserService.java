package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
    public UserDto create(UserDto userDto){
        User user = userMapper.fromDto(userDto);
        User savedUser = userRepository.save(user.setPremium(false).setEnabled(true));
        return userMapper.toDto(savedUser);
    }
    public void deleteById(int id){
        userRepository.deleteById(id);
    }

}
