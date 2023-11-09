package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.SwearWordsDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.exceptions.SwearWordsExceptions;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.CurrencyValueRepository;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final CurrencyValueRepository currencyValueRepository;
    private final CurrencyService currencyService;
    private final SwearWordsDto swearWordsDto;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DbUserDetailsService dbUserDetailsService;

    //Seller:
    public CarResponse createCar(CarDto carDto, String username){
        Car car = carMapper.toCar(carDto);
        User user = userRepository.findByUsername(username);
        String ccy = carDto.getCurrencyName();
        try{
            carLimit(user);
            currencyService.isValidCurrencyName(ccy);
        }catch (IllegalArgumentException e){
            return new CarResponse(null).setError(e.getMessage());
        }
        try {
            hasSwearWords(car.getDetails());
        } catch (SwearWordsExceptions e){
            car.setCheckCount(0);
            carRepository.save(car);
            return new CarResponse(null).setError(e.getMessage());
        }
        List<Car> cars = user.getCars();
        car.setCurrencyValue(currencyValueRepository.findCurrencyValueByCcy(ccy).getSale());
        cars.add(car);
        user.setCars(cars);
        User saved = userRepository.save(user);
        List<Car> savedCars = saved.getCars();
        Car savedCar = savedCars.get(savedCars.size()-1);
        return new CarResponse(carMapper.toDto(savedCar));
    }
    public CarResponse editMyCar(int id, CarDto carDto, String username){
        User user = userRepository.findByUsername(username);
        List<Car> cars = user.getCars();
        String ccy = carDto.getCurrencyName();
        try{
            isPersonalCarAndIndex(cars, id);
            currencyService.isValidCurrencyName(ccy);
        }catch (IllegalArgumentException e){
            return new CarResponse().setError(e.getMessage());
        }
        try {
            hasSwearWords(carDto.getDetails());
        } catch (SwearWordsExceptions e){
            carDto.setCheckCount(0);
            carRepository.save(carMapper.toCar(carDto));
            return new CarResponse(null).setError(e.getMessage());
        }
        Car car = carRepository.findById(id).get();
        car.update( carDto.getModel(), carDto.getProducer(), carDto.getYear(),
                carDto.getPower(),carDto.getType(),carDto.getDetails(),carDto.getRunKm(),carDto.getEngineVolume(),
                carDto.getColor(),carDto.getRegion(),carDto.getPlace(),carDto.getTransmission(),carDto.getGearbox(),
                carDto.getPrice(), carDto.getCurrencyName(),
                currencyValueRepository.findCurrencyValueByCcy(ccy).getSale(),carDto.getCheckCount(),carDto.getWatchesPerDay(),carDto.getWatchesPerWeek(),carDto.getWatchesPerMonth(),carDto.getPhoto());
        Car save = carRepository.save(car);
        return new CarResponse(carMapper.toDto(save));
    }
    public String deleteMyCar(int id, String username){
        User user = userRepository.findByUsername(username);
        List<Car> cars = user.getCars();
        try{
            isPersonalCarAndIndex(cars, id);
//            carRepository.deleteById(id);
            Car car = carRepository.findById(id).get();
            cars.remove(car);
            user.setCars(cars);
            userRepository.save(user);
            carRepository.deleteById(id);
            return "Car with id: "+id+" was deleted";
        }catch (IllegalArgumentException e){
            return e.getMessage();
        }
    }
    public CarsResponse getMyCars(String username){
        User user = userRepository.findByUsername(username);
        List<Car> cars = user.getCars();
        return new CarsResponse(cars.stream().map(carMapper::toDto).toList());
    }
    public void isUsernameAlreadyExists(String username){
        if(userRepository.findByUsername(username) != null){
            throw new IllegalArgumentException("username already exists");
        };
    }
    public void isEmailAlreadyExists(String email){
        if(userRepository.findByEmail(email) != null){
            throw new IllegalArgumentException("email already in use");
        };
    }
    public void carLimit(User user){
        if(!(user.getPremium() || user.getCars().size()<1)){
            throw new IllegalArgumentException("Not premium account! You can`t upload more than 1");
        }
    }
    public void isPersonalCarAndIndex(List<Car> cars, int id){
        List<Integer> list = cars.stream().map(Car::getId).toList();
        int i = list.indexOf(id);
        if (!list.contains(id)){
            throw new IllegalArgumentException("Not legal car id argument");
        };
    }
    public void isPhoneNumberAlreadyUsed(Integer phone){
        if(userRepository.findByPhone(phone) != null){
            throw new IllegalArgumentException("phone number already in use");
        };
    }
    public void hasSwearWords(String details) throws SwearWordsExceptions {
        String[] swears = swearWordsDto.getSwears();
        for (String swear : swears) {
            if (details.contains(swear)){
                throw new SwearWordsExceptions("swear words used, enter info without swear words. Tries:");
            }
        }
    }
}

