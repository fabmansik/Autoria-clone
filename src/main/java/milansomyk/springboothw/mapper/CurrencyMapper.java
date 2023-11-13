package milansomyk.springboothw.mapper;

import milansomyk.springboothw.dto.CurrencyDto;
import milansomyk.springboothw.entity.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {
    public CurrencyDto toDto(Currency currency){
        return CurrencyDto.builder()
                .id(currency.getId())
                .ccy(currency.getCcy())
                .buy(currency.getBuy())
                .sale(currency.getSale())
                .build();
    }
    public Currency fromDto(CurrencyDto currencyDto){
        return new Currency(currencyDto.getId(), currencyDto.getCcy(), currencyDto.getBuy(), currencyDto.getSale());
    }
}
