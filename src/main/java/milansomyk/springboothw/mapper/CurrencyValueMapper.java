package milansomyk.springboothw.mapper;

import milansomyk.springboothw.dto.CurrencyValueDto;
import milansomyk.springboothw.entity.CurrencyValue;
import org.springframework.stereotype.Component;

@Component
public class CurrencyValueMapper {
    public CurrencyValueDto toDto(CurrencyValue currencyValue){
        return CurrencyValueDto.builder()
                .id(currencyValue.getId())
                .ccy(currencyValue.getCcy())
                .buy(currencyValue.getBuy())
                .sale(currencyValue.getSale())
                .build();
    }
    public CurrencyValue fromDto(CurrencyValueDto currencyValueDto){
        return new CurrencyValue(currencyValueDto.getId(), currencyValueDto.getCcy(), currencyValueDto.getBuy(), currencyValueDto.getSale());
    }
}
