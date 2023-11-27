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
        try {
            carRepository.nullCarWatchesPerDay();
        } catch (Exception e){
            log.error(e.getMessage());
        }
        log.info("Watches per day are null");
    }
    @Scheduled(cron="@weekly")
    public void processPerWeek(){
        try {
            carRepository.nullCarWatchesPerWeek();
        } catch (Exception e){
            log.error(e.getMessage());
        }
        log.info("Watches per week are null");
    }

    @Scheduled(cron="@monthly")
    public void processPerMonth(){
        try {
            carRepository.nullCarWatchesPerMonth();
        } catch (Exception e){
            log.error(e.getMessage());
        }
        log.info("Watches per month are null");
    }

}
