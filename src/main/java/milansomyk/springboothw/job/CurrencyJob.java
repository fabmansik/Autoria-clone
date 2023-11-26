package milansomyk.springboothw.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.service.entityServices.CurrencyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class CurrencyJob {
    private final CurrencyService currencyService;

    @Scheduled(cron = "@daily")
    public void process(){
        ResponseContainer responseContainer = currencyService.uploadCurrencies();
        if (responseContainer.isError()){
            log.info("Currency update failed: "+responseContainer.getErrorMessage());
        }
        log.info("Currency value updated...");
    }
}
