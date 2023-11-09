package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.CurrencyNameValueDto;
import milansomyk.springboothw.dto.response.CurrenciesResponse;
import milansomyk.springboothw.repository.CurrencyValueRepository;
import milansomyk.springboothw.service.CurrencyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currency")
public class CurrencyController {
    private final CurrencyService currencyService;
    @PostMapping
    public List<CurrencyNameValueDto> transferToAllCurrencies(@RequestParam String ccy, @RequestParam String value){
        return currencyService.transferToAllCurrencies(ccy,value);
    }
}
