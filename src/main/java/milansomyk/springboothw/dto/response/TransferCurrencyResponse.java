package milansomyk.springboothw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCurrencyResponse {
    private String ccy;
    private int value;
    private String currencyValue;
}
