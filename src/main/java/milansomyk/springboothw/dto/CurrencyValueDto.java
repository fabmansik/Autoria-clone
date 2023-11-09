package milansomyk.springboothw.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyValueDto {
    private Integer id;
    private String ccy;
    private String buy;
    private String sale;
}
