package milansomyk.springboothw.service.entityServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.CurrencyDto;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.CurrencyResponse;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.Currency;
import milansomyk.springboothw.mapper.CurrencyMapper;
import milansomyk.springboothw.repository.CurrencyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Service
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final ObjectMapper objectMapper;
    private final Constants constants;
    public ResponseContainer transferToAllCurrencies(String ccy, String value){
        ResponseContainer responseContainer = new ResponseContainer();
        if(ccy==null){
            log.info("ccy is null");
            return responseContainer.setErrorMessageAndStatusCode("ccy is null",HttpStatus.BAD_REQUEST.value());
        }
        if(value==null){
            log.info("value is null");
            return responseContainer.setErrorMessageAndStatusCode("value is null",HttpStatus.BAD_REQUEST.value());
        }
        List<Currency> all;
        try {
            all = currencyRepository.findAll();
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        Currency foundByCcy;
        try{
            foundByCcy = currencyRepository.findCurrencyByCcy(ccy).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        String sale = foundByCcy.getSale();
        Map<String, String> collect = all.stream().collect(Collectors.toMap(Currency::getCcy, Currency::getSale));
        List<CurrencyResponse> list = new java.util.ArrayList<>(collect.entrySet().stream().map(e -> {
            Double i = (Double.parseDouble(sale) / Double.parseDouble(e.getValue())  * Double.parseDouble(value));
            return new CurrencyResponse(e.getKey(), Double.toString(i));
        }).toList());
        list.removeIf(e->e.getCurrencyName().equals(ccy));
        responseContainer.setSuccessResult(list);
        return responseContainer;
    }
    public ResponseContainer validCurrencyName(String currencyName, ResponseContainer responseContainer){
        List<Currency> all = new ArrayList<>();
        try{
             all = currencyRepository.findAll();
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        List<String> list = all.stream().map(Currency::getCcy).toList();
        if (!list.contains(currencyName)){
            log.info("not valid currency name");
            return responseContainer.setErrorMessageAndStatusCode("Not valid currency name. Currency name could be: "+list.toString().replace("[","").replace("]",""),HttpStatus.BAD_REQUEST.value());
        }
        responseContainer.setSuccessResult(list);
        return responseContainer;
    }
    public ResponseContainer transferToCcy(String finalCcy, String transferedCcy, Integer value, ResponseContainer responseContainer){
        if(finalCcy== null){
            log.info("finalCcy is null");
            return responseContainer.setErrorMessageAndStatusCode("finalCcy is null",HttpStatus.BAD_REQUEST.value());
        }
        if(transferedCcy==null){
            log.info("transferedCcy is null");
            return responseContainer.setErrorMessageAndStatusCode("transferedCcy is null",HttpStatus.BAD_REQUEST.value());
        }
        if(value == null){
            log.info("value is null");
            return responseContainer.setErrorMessageAndStatusCode("value is null",HttpStatus.BAD_REQUEST.value());
        }
        List<Currency> foundByCcy;
        String[] ccies = new String[]{finalCcy,transferedCcy};
        try {
            foundByCcy = currencyRepository.findCurrenciesByCcy(ccies).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (CollectionUtils.isEmpty(foundByCcy)|foundByCcy.size()<2){
            log.info("no such ccies");
            return responseContainer.setErrorMessageAndStatusCode("no such ccies", HttpStatus.BAD_REQUEST.value());
        }
        String sale = foundByCcy.get(0).getSale();
        String ccyCurency = foundByCcy.get(1).getSale();
        double submit = Double.parseDouble(sale) * Double.parseDouble(Integer.toString(value))/Double.parseDouble(ccyCurency);
        responseContainer.setSuccessResult(Math.toIntExact(Math.round(submit)));
        return responseContainer;
    }
    public ResponseContainer uploadCurrencies(){
        ResponseContainer responseContainer = new ResponseContainer();
        URL url;
        try{
        url = new URL(constants.getPrivatApiUrl());
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        String string;
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            string = json.toString();
        } catch (Exception e) {
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        CurrencyDto[] list;
        try {
            list = objectMapper.readValue(string, CurrencyDto[].class);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

            CurrencyDto value1 = Arrays.stream(list).toList().get(0);
            CurrencyDto value2 = Arrays.stream(list).toList().get(1);

            Currency eur;
            Currency usd;
            Currency uah;
            try{
               eur = currencyRepository.findCurrencyByCcy("EUR").orElse(null);
            } catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            eur.setBuy(value1.getBuy()).setSale(value1.getSale()).setCcy(value1.getCcy());

            try{
                usd = currencyRepository.findCurrencyByCcy("USD").orElse(null);
            } catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            usd.setBuy(value2.getBuy()).setSale(value2.getSale()).setCcy(value2.getCcy());

            try {
                uah = currencyRepository.findCurrencyByCcy("UAH").orElse(null);
            } catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            uah.setBuy("1.00000").setSale("1.00000").setCcy("UAH");

            try{
                currencyRepository.save(eur);
            }catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            try{
                currencyRepository.save(usd);
            }catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            try{
                currencyRepository.save(uah);
            }catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            Currency[] updatedCurrencies = new Currency[]{uah, usd, eur};
            responseContainer.setSuccessResult(updatedCurrencies);
            return responseContainer;
    }

}
