package milansomyk.springboothw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyResponse {
    private String currencyName;
    private String currencyValue;

}
