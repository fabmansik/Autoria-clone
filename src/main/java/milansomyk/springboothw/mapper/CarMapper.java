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
                .photo(car.getPhoto())
                .build();
    }
    public Car toCar(CarDto carDto){
        return new Car(carDto.getId(), carDto.getModel(), carDto.getProducer(), carDto.getYear(), carDto.getPower(), carDto.getType(), carDto.getDetails(), carDto.getRunKm(), carDto.getEngineVolume(), carDto.getColor(), carDto.getRegion(), carDto.getPlace(), carDto.getTransmission(), carDto.getGearbox(), carDto.getPhoto());
    }
}
