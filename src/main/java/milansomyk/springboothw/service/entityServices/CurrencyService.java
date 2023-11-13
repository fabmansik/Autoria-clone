package milansomyk.springboothw.service.entityServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import milansomyk.springboothw.dto.CurrencyDto;
import milansomyk.springboothw.dto.response.CurrencyResponse;
import milansomyk.springboothw.entity.Currency;
import milansomyk.springboothw.mapper.CurrencyMapper;
import milansomyk.springboothw.repository.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final ObjectMapper objectMapper;
    public List<CurrencyResponse> transferToAllCurrencies(String ccy, String value){
        List<Currency> all = currencyRepository.findAll();
        String sale = currencyRepository.findCurrencyByCcy(ccy).getSale();

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
        }
    }
    public Integer transferToCcy(String finalCcy, String transferedCcy, Integer value){
        String CcyCurrency = currencyRepository.findCurrencyByCcy(finalCcy).getSale();
        String sale = currencyRepository.findCurrencyByCcy(transferedCcy).getSale();
        double submit = Double.parseDouble(sale) * Double.parseDouble(Integer.toString(value))/Double.parseDouble(CcyCurrency);
        System.out.println(submit);
        return Math.toIntExact(Math.round(submit));
    }
    public void uploadCurrencies() throws IOException{
        URL url = new URL("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5");
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            String string = json.toString();
            CurrencyDto[] list = objectMapper.readValue(string, CurrencyDto[].class);
            CurrencyDto value1 = Arrays.stream(list).toList().get(0);
            CurrencyDto value2 = Arrays.stream(list).toList().get(1);

            Currency eur = new Currency();
            Currency usd = new Currency();
            Currency uah = new Currency();
            if(currencyRepository.findCurrencyByCcy("EUR")==null){
                eur.setBuy(value1.getBuy()).setSale(value1.getSale()).setCcy(value1.getCcy());
            }else{
                eur = currencyRepository.findCurrencyByCcy("EUR");
                eur.setBuy(value1.getBuy()).setSale(value1.getSale()).setCcy(value1.getCcy());
            }
            if(currencyRepository.findCurrencyByCcy("USD")==null){
                usd.setBuy(value2.getBuy()).setSale(value2.getSale()).setCcy(value2.getCcy());
            }else{
                usd = currencyRepository.findCurrencyByCcy("USD");
                usd.setBuy(value2.getBuy()).setSale(value2.getSale()).setCcy(value2.getCcy());
            }
            if(currencyRepository.findCurrencyByCcy("UAH")==null){
                uah.setBuy("1.00000").setSale("1.00000").setCcy("UAH");
            }else{
                uah = currencyRepository.findCurrencyByCcy("UAH");
                uah.setBuy("1.00000").setSale("1.00000").setCcy("UAH");
            }

            currencyRepository.save(eur);
            currencyRepository.save(usd);
            currencyRepository.save(uah);

        }
    }

}
