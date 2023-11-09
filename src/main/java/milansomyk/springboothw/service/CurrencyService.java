package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.CurrencyNameValueDto;
import milansomyk.springboothw.entity.CurrencyValue;
import milansomyk.springboothw.mapper.CurrencyValueMapper;
import milansomyk.springboothw.repository.CurrencyValueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Service
public class CurrencyService {
    private final CurrencyValueRepository currencyValueRepository;
    private final CurrencyValueMapper currencyValueMapper;
    public List<CurrencyNameValueDto> transferToAllCurrencies(String ccy, String value){
        List<CurrencyValue> all = currencyValueRepository.findAll();
        String sale = currencyValueRepository.findCurrencyValueByCcy(ccy).getSale();

        Map<String, String> collect = all.stream().collect(Collectors.toMap(CurrencyValue::getCcy, CurrencyValue::getSale));
        List<CurrencyNameValueDto> list = new java.util.ArrayList<>(collect.entrySet().stream().map(e -> {
            Double i = (Double.parseDouble(sale) / Double.parseDouble(e.getValue())  * Double.parseDouble(value));
            return new CurrencyNameValueDto(e.getKey(), Double.toString(i));
        }).toList());
        list.removeIf(e->e.getCurrencyName().equals(ccy));
        return list;
    }
    public void isValidCurrencyName(String currencyName){
        List<CurrencyValue> all = currencyValueRepository.findAll();
        List<String> list = all.stream().map(CurrencyValue::getCcy).toList();
        if (!list.contains(currencyName)){
            throw new IllegalArgumentException("Not valid currency name. Currency name could be: "+list.toString().replace("[","").replace("]",""));
        };
    }
    public Integer transferToCcy(String finalCcy, String transferedCcy, Integer value){
        String CcyCurrency = currencyValueRepository.findCurrencyValueByCcy(finalCcy).getSale();
        String sale = currencyValueRepository.findCurrencyValueByCcy(transferedCcy).getSale();
        double submit = Double.parseDouble(sale) * Double.parseDouble(Integer.toString(value))/Double.parseDouble(CcyCurrency);
        System.out.println(submit);
        return Math.toIntExact(Math.round(submit));
    }

}
