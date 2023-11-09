package milansomyk.springboothw.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrenciesResponse {
    private String eur;
    private String usd;
    private String uah;
}
