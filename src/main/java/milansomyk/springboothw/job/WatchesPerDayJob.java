package milansomyk.springboothw.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.repository.CarRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WatchesPerDayJob {
    private final CarRepository carRepository;
    @Scheduled(cron = "0 59 23 * * *")
    public void process(){
        carRepository.nullCarWatchesPerDay();
        log.info("Watches per day are null");
    }
}
