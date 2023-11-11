package milansomyk.springboothw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageResponse {
    private Integer price;
    private String ccy;
    private Integer amount;
    private String error;
}
