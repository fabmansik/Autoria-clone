package milansomyk.springboothw.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.service.entityServices.CurrencyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class CurrencyJob {
    private final CurrencyService currencyService;

    @Scheduled(cron = "@daily")
    public void process() throws IOException {
        currencyService.uploadCurrencies();
        log.info("Currency value updated...");
    }
}
