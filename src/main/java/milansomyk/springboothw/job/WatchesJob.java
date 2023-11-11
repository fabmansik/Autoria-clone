package milansomyk.springboothw.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.repository.CarRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WatchesJob {
    private final CarRepository carRepository;
    @Scheduled(cron = "@daily")
    public void process(){
        carRepository.nullCarWatchesPerDay();
        log.info("Watches per day are null");
    }
    @Scheduled(cron="@weekly")
    public void processPerWeek(){
        carRepository.nullCarWatchesPerWeek();
        log.info("Watches per week are null");
    }

    @Scheduled(cron="@monthly")
    public void processPerMonth(){
        carRepository.nullCarWatchesPerMonth();
        log.info("Watches per month are null");
    }

}
