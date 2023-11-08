package milansomyk.springboothw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import milansomyk.springboothw.dto.UserDto;

@Data
@AllArgsConstructor
public class UserResponse {
    public final UserDto user;
    public final String error;
}
