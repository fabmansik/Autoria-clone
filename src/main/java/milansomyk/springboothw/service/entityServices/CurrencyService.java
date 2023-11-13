package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import milansomyk.springboothw.dto.response.CurrencyResponse;
import milansomyk.springboothw.entity.Currency;
import milansomyk.springboothw.mapper.CurrencyMapper;
import milansomyk.springboothw.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    public List<CurrencyResponse> transferToAllCurrencies(String ccy, String value){
        List<Currency> all = currencyRepository.findAll();
        String sale = currencyRepository.findCurrencyValueByCcy(ccy).getSale();

        Map<String, String> collect = all.stream().collect(Collectors.toMap(Currency::getCcy, Currency::getSale));
        List<CurrencyResponse> list = new java.util.ArrayList<>(collect.entrySet().stream().map(e -> {
            Double i = (Double.parseDouble(sale) / Double.parseDouble(e.getValue())  * Double.parseDouble(value));
            return new CurrencyResponse(e.getKey(), Double.toString(i));
        }).toList());
        list.removeIf(e->e.getCurrencyName().equals(ccy));
        return list;
    }
    public void isValidCurrencyName(String currencyName){
        List<Currency> all = currencyRepository.findAll();
        List<String> list = all.stream().map(Currency::getCcy).toList();
        if (!list.contains(currencyName)){
            throw new IllegalArgumentException("Not valid currency name. Currency name could be: "+list.toString().replace("[","").replace("]",""));
        };
    }
    public Integer transferToCcy(String finalCcy, String transferedCcy, Integer value){
        String CcyCurrency = currencyRepository.findCurrencyValueByCcy(finalCcy).getSale();
        String sale = currencyRepository.findCurrencyValueByCcy(transferedCcy).getSale();
        double submit = Double.parseDouble(sale) * Double.parseDouble(Integer.toString(value))/Double.parseDouble(CcyCurrency);
        System.out.println(submit);
        return Math.toIntExact(Math.round(submit));
    }

}
