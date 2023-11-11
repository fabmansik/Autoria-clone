package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.response.AverageResponse;
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
    private final UserService userService;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    public AverageResponse findAveragePrice(String producer, String model, String ccy, String region,String username){
        if(!userService.isPremiumAccount(username)){
            return new AverageResponse(null, null,null,"Not premium account");
        }
        String currency;
        List<Car> cars;
        if(region!=null){
            cars = carRepository.findByProducerAndModelAndActiveAndRegion(producer, model, true, region);
        } else{
            cars = carRepository.findByProducerAndModelAndActive(producer, model, true);
        }
        if(ccy != null){
            cars.forEach(car-> car.setPrice(
                    currencyService.transferToCcy(ccy,car.getCurrencyName(),car.getPrice())
            ));
            currency=ccy;
        } else {
            cars.forEach(car-> car.setPrice(
                    currencyService.transferToCcy("USD",car.getCurrencyName(),car.getPrice())
            ));
            currency="USD";
        }
        List<Integer> prices = cars.stream().map(Car::getPrice).toList();
        Integer average = averageCalculator(prices);
        return new AverageResponse(average,currency,prices.size(),null);
    }
    public void addWatchesTotal(int id){
        Car car = carRepository.findById(id).get();
        car.addWatches();
        carRepository.save(car);
    }
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
        List<Car> allCars = carRepository.findAllActive();

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

        return new CarsResponse(allCars.stream().map(carMapper::toDto).toList()).setAmount(allCars.size());
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
    public Integer averageCalculator(List<Integer> integers){
        Integer summary=0;
        for (Integer integer : integers) {
            summary+=integer;
        }
        return summary / integers.size();
    }
}
