package milansomyk.springboothw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyNameValueDto {
    private String currencyName;
    private String currencyValue;

}
