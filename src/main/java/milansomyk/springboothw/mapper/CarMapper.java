package milansomyk.springboothw.mapper;

import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.entity.Car;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {
    public CarDto toDto(Car car){
        return CarDto.builder()
                .id(car.getId())
                .power(car.getPower())
                .model(car.getModel())
                .producer(car.getProducer())
                .year(car.getYear())
                .type(car.getType())
                .details(car.getDetails())
                .runKm(car.getRunKm())
                .engineVolume(car.getEngineVolume())
                .color(car.getColor())
                .region(car.getRegion())
                .transmission(car.getTransmission())
                .gearbox(car.getGearbox())
                .price(car.getPrice())
                .currencyName(car.getCurrencyName())
                .currencyValue(car.getCurrencyValue())
                .checkCount(car.getCheckCount())
                .watchesPerDay(car.getWatchesPerDay())
                .watchesPerWeek(car.getWatchesPerWeek())
                .watchesPerMonth(car.getWatchesPerMonth())
                .active(car.isActive())
                .creationDate(car.getCreationDate())
                .photo(car.getPhoto())
                .build();
    }
    public Car toCar(CarDto carDto){
        return new Car(carDto.getModel(), carDto.getProducer(), carDto.getYear(), carDto.getPower(), carDto.getType(), carDto.getDetails(), carDto.getRunKm(), carDto.getEngineVolume(), carDto.getColor(), carDto.getRegion(), carDto.getPlace(), carDto.getTransmission(), carDto.getGearbox(), carDto.getPrice(), carDto.getCurrencyName(), carDto.getCurrencyValue(), carDto.getCheckCount(), carDto.getWatchesPerDay(), carDto.getWatchesPerWeek(), carDto.getWatchesPerMonth(), carDto.isActive(), carDto.getCreationDate(),carDto.getPhoto() );
    }
}
