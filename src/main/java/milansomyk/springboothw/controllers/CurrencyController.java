package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.response.CurrencyResponse;
import milansomyk.springboothw.service.entityServices.CurrencyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currency")
public class CurrencyController {
    private final CurrencyService currencyService;
    @GetMapping
    public List<CurrencyResponse> transferToAllCurrencies(@RequestParam String ccy, @RequestParam String value){
        return currencyService.transferToAllCurrencies(ccy,value);
    }
}
