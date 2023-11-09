package milansomyk.springboothw.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.CurrencyValueDto;
import milansomyk.springboothw.entity.CurrencyValue;
import milansomyk.springboothw.mapper.CurrencyValueMapper;
import milansomyk.springboothw.repository.CurrencyValueRepository;
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
public class JobCurrencyValue {
    private final ObjectMapper objectMapper;
    private final CurrencyValueRepository currencyValueRepository;
    @Scheduled(cron = " 0 0 12 * * *")
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
            CurrencyValueDto[] list = objectMapper.readValue(string, CurrencyValueDto[].class);
            CurrencyValueDto value1 = Arrays.stream(list).toList().get(0);
            CurrencyValueDto value2 = Arrays.stream(list).toList().get(1);

            CurrencyValue eur = currencyValueRepository.findById(1).get();
            CurrencyValue usd = currencyValueRepository.findById(2).get();

            eur.setBuy(value1.getBuy()).setSale(value1.getSale());
            usd.setBuy(value2.getBuy()).setSale(value2.getSale());

            log.info("Currency Value Updated...");
            currencyValueRepository.save(eur);
            currencyValueRepository.save(usd);
        }
    }
}
