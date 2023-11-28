package milansomyk.springboothw.service.entityServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.CurrencyDto;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.dto.response.TransferCurrencyResponse;
import milansomyk.springboothw.entity.Currency;
import milansomyk.springboothw.repository.CurrencyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Service
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;
    private final Constants constants;

    public ResponseContainer transferToAllCurrencies(String ccy, String value) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (ccy == null) {
            log.error("ccy is null");
            return responseContainer.setErrorMessageAndStatusCode("ccy is null", HttpStatus.BAD_REQUEST.value());
        }
        if (value == null) {
            log.error("value is null");
            return responseContainer.setErrorMessageAndStatusCode("value is null", HttpStatus.BAD_REQUEST.value());
        }
        List<Currency> all;
        try {
            all = currencyRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (CollectionUtils.isEmpty(all)){
            log.error("Currencies is empty");
            return responseContainer.setErrorMessageAndStatusCode("Currencies is empty",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        Currency foundByCcy;
        try {
            foundByCcy = currencyRepository.findCurrencyByCcy(ccy).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(foundByCcy)) {
            log.error("currencies not found");
            return responseContainer.setErrorMessageAndStatusCode("currencies not found", HttpStatus.BAD_REQUEST.value());
        }
        String sale = foundByCcy.getSale();
        Map<String, String> collect = all.stream().collect(Collectors.toMap(Currency::getCcy, Currency::getSale));
        List<TransferCurrencyResponse> list = new java.util.ArrayList<>(collect.entrySet().stream().map(e -> {
            double i = (Double.parseDouble(value) * Double.parseDouble(sale)) / Double.parseDouble(e.getValue()) ;
            return new TransferCurrencyResponse(e.getKey(), (int) i,e.getValue());
        }).toList());
        list.removeIf(e -> e.getCcy().equalsIgnoreCase(ccy));
        responseContainer.setSuccessResult(list);
        return responseContainer;
    }

    public ResponseContainer validCurrencyName(String currencyName, ResponseContainer responseContainer) {
        List<Currency> all;
        try {
            all = currencyRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(CollectionUtils.isEmpty(all)){
            log.error("Currencies is empty");
            return responseContainer.setErrorMessageAndStatusCode("Currencies is empty",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        List<String> list = all.stream().map(Currency::getCcy).toList();
        if (!list.contains(currencyName)) {
            log.error("not valid currency name");
            return responseContainer.setErrorMessageAndStatusCode("Not valid currency name. Currency name could be: " + list.toString().replace("[", "").replace("]", ""), HttpStatus.BAD_REQUEST.value());
        }
        responseContainer.setSuccessResult(list);
        return responseContainer;
    }

    public ResponseContainer transferToCcy(String finalCcy, String transferedCcy, Integer value, ResponseContainer responseContainer) {
        if (finalCcy == null) {
            log.error("finalCcy is null");
            return responseContainer.setErrorMessageAndStatusCode("finalCcy is null", HttpStatus.BAD_REQUEST.value());
        }
        if (transferedCcy == null) {
            log.error("transferedCcy is null");
            return responseContainer.setErrorMessageAndStatusCode("transferedCcy is null", HttpStatus.BAD_REQUEST.value());
        }
        if (value == null) {
            log.error("value is null");
            return responseContainer.setErrorMessageAndStatusCode("value is null", HttpStatus.BAD_REQUEST.value());
        }
        List<Currency> foundAll;
        try {
            foundAll = currencyRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(CollectionUtils.isEmpty(foundAll)){
            log.error("currencies not found");
            return responseContainer.setErrorMessageAndStatusCode("currencies not found",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        List<Currency> list = foundAll.stream().filter(item -> item.getCcy().equalsIgnoreCase(finalCcy)).toList();
        if(CollectionUtils.isEmpty(list)){
            log.error("ccy is illegal");
            return responseContainer.setErrorMessageAndStatusCode("ccy is illegal",HttpStatus.BAD_REQUEST.value());
        }
        Currency foundFinal = list.get(0);
        if(ObjectUtils.isEmpty(foundFinal)){
            log.error("ccy is illegal");
            return responseContainer.setErrorMessageAndStatusCode("ccy is illegal",HttpStatus.BAD_REQUEST.value());
        }
        Currency foundTransfered = foundAll.stream().filter(item-> item.getCcy().equalsIgnoreCase(transferedCcy)).toList().get(0);

        if (ObjectUtils.isEmpty(foundFinal)||ObjectUtils.isEmpty(foundTransfered)) {
            log.error("not found currencies");
            return responseContainer.setErrorMessageAndStatusCode("not found currencies", HttpStatus.BAD_REQUEST.value());
        }

        String sale = foundTransfered.getSale();
        String ccyCurency = foundFinal.getSale();
        double submit = Double.parseDouble(sale) * Double.parseDouble(Integer.toString(value)) / Double.parseDouble(ccyCurency);
        responseContainer.setSuccessResult(new TransferCurrencyResponse(foundFinal.getCcy(), Math.toIntExact(Math.round(submit)),foundFinal.getSale()));
        return responseContainer;
    }

    public ResponseContainer uploadCurrencies() {
        ResponseContainer responseContainer = new ResponseContainer();
        URL url;
        try {
            url = new URL(constants.getPrivatApiUrl());
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
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
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        CurrencyDto[] list;
        try {
            list = objectMapper.readValue(string, CurrencyDto[].class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        CurrencyDto value1 = Arrays.stream(list).toList().get(0);
        CurrencyDto value2 = Arrays.stream(list).toList().get(1);

        Currency eur;
        Currency usd;
        Currency uah;
        try {
            eur = currencyRepository.findCurrencyByCcy("EUR").orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(eur)) {
            log.error("currency not found");
            return responseContainer.setErrorMessageAndStatusCode("currency not found", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        eur.setBuy(value1.getBuy()).setSale(value1.getSale()).setCcy(value1.getCcy());

        try {
            usd = currencyRepository.findCurrencyByCcy("USD").orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(usd)) {
            log.error("currency not found");
            return responseContainer.setErrorMessageAndStatusCode("currency not found", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        usd.setBuy(value2.getBuy()).setSale(value2.getSale()).setCcy(value2.getCcy());

        try {
            uah = currencyRepository.findCurrencyByCcy("UAH").orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(uah)){
            log.error("currency not found");
            return responseContainer.setErrorMessageAndStatusCode("currency not found",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        uah.setBuy("1.00000").setSale("1.00000").setCcy("UAH");

        try {
            currencyRepository.save(eur);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            currencyRepository.save(usd);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            currencyRepository.save(uah);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        Currency[] updatedCurrencies = new Currency[]{uah, usd, eur};
        responseContainer.setSuccessResult(updatedCurrencies);
        return responseContainer;
    }

}
