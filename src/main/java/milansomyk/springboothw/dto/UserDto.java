package milansomyk.springboothw.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.view.Views;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "username required")
    @Size(min = 3, max = 20, message = "username: min: {min}, max: {max} characters")
    private String username;
    @NotBlank(message = "password required")
    @Pattern(regexp = "^(?=.*\\d).{4,8}$", flags = Pattern.Flag.UNICODE_CASE, message = "invalid password")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
    @Email(message = "Not a email")
    private String email;
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "Invalid phone number")
    private Integer phone;
    private boolean premium;
    @JsonView(Views.LevelManagerAdmin.class)
    private boolean enabled;
    @JsonView(Views.LevelManagerAdmin.class)
    private String role;
    private List<Car> cars;
}
