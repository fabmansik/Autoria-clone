package milansomyk.springboothw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import milansomyk.springboothw.entity.Car;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "username required")
    @Size(min = 3, max = 20, message = "username: min: {min}, max: {max} characters")
    private String username;
    @NotBlank(message = "password required")
    @Pattern(regexp = "^(?=.*\\d).{4,8}$", flags = Pattern.Flag.UNICODE_CASE, message = "invalid password")
    private String password;
    @Email(message = "Not a email")
    private String email;
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "Invalid phone number")
    private Integer phone;
    private List<Car> cars;
    public UserDto(){

    }
}
