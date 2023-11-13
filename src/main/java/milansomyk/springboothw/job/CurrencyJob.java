package milansomyk.springboothw.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.CurrencyDto;
import milansomyk.springboothw.entity.Currency;
import milansomyk.springboothw.repository.CurrencyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrencyJob {
    private final ObjectMapper objectMapper;
    private final CurrencyRepository currencyRepository;
    @Scheduled(cron = "@daily")
    public void process() throws IOException {
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

            Currency eur = currencyRepository.findById(1).get();
            Currency usd = currencyRepository.findById(2).get();

            eur.setBuy(value1.getBuy()).setSale(value1.getSale());
            usd.setBuy(value2.getBuy()).setSale(value2.getSale());

            log.info("Currency Value Updated...");
            currencyRepository.save(eur);
            currencyRepository.save(usd);
        }
    }
}
