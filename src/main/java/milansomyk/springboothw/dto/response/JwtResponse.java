package milansomyk.springboothw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    public final String token;
    public final String refresh;
}
