package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Data
@Service

public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    public CarResponse findById(int id){
        Car car = carRepository.findById(id).get();
        CarDto dto = carMapper.toDto(car);
        return new CarResponse(dto);
    }
    public String deleteById(int id){
        Car car = carRepository.findById(id).get();
        User user = userRepository.findByCarsContaining(car);
        List<Car> cars = user.getCars();
        cars.remove(car);
        user.setCars(cars);
        userRepository.save(user);
        carRepository.deleteById(id);
        return "Car with this id: "+id+", was deleted";
    }
    public CarsResponse getAllCars(){
        return new CarsResponse(carRepository.findAll().stream().map(carMapper::toDto).toList());
    }
    public CarsResponse getSpecifiedCar(String producer, String model, String region, Integer minPrice, Integer maxPrice, String ccy){
        List<Car> allCars = carRepository.findAll();
        if(producer!=null){
            allCars.removeIf(car-> !Objects.equals(car.getProducer(), producer));
        }if(model != null){
            allCars.removeIf(car->!Objects.equals(car.getModel(), model));
        }if(region != null){
            allCars.removeIf(car->!Objects.equals(car.getRegion(), region));
        }
        if(ccy != null){
            if(minPrice!=null){
                allCars.removeIf(car->currencyService.transferToCcy(ccy,car.getCurrencyName(), car.getPrice())<=minPrice);
            }
            if(maxPrice!=null){
                allCars.removeIf(car->currencyService.transferToCcy(ccy,car.getCurrencyName(), car.getPrice())>=maxPrice);
            }
        }else{
            if(minPrice!=null){
                allCars.removeIf(car->currencyService.transferToCcy("USD",car.getCurrencyName(), car.getPrice())<=minPrice);
            }
            if(maxPrice!=null){
                allCars.removeIf(car->currencyService.transferToCcy("USD",car.getCurrencyName(), car.getPrice())>=maxPrice);
            }
        }

        return new CarsResponse(allCars.stream().map(carMapper::toDto).toList());
    }
    public List<CarDto> getByPower(int power){
        return this.carRepository.getCarsByPower(power)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }
    public List<CarDto> getByProducer (String producer){
        return this.carRepository.getCarsByProducer(producer)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }
    public CarDto addPhotoByCarId(int id, MultipartFile file) throws IOException {
         Car car = this.carRepository.findById(id).orElseThrow();
         String originalFileName = file.getOriginalFilename();
         String path = System.getProperty("user.home") + File.separator+"cars"+File.separator + originalFileName;
         file.transferTo(new File(path));
         car.setPhoto(originalFileName);
         Car savedCar = this.carRepository.save(car);
         return this.carMapper.toDto(savedCar);
    }
}
