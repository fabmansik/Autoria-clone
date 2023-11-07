package milansomyk.springboothw.mapper;

import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
    public User fromDto(UserDto userDto){
        return new User(userDto.getId(), userDto.getUsername(), userDto.getPassword(), userDto.getEmail(), userDto.getPhone(), userDto.getRole());
    }
}
