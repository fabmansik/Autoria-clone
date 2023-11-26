package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.service.entityServices.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currency")
public class CurrencyController {
    private final CurrencyService currencyService;
    @GetMapping
    public ResponseEntity<ResponseContainer> transferToAllCurrencies(@RequestParam String ccy, @RequestParam String value){
        ResponseContainer responseContainer = currencyService.transferToAllCurrencies(ccy, value);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
}
