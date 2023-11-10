package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.SwearWordsDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.exceptions.SwearWordsExceptions;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.CurrencyValueRepository;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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
    private final MailSender mailSender;
    private final ManagerModerationNotifier managerModerationNotifier;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DbUserDetailsService dbUserDetailsService;

    //Seller:
    public CarResponse createCar(CarDto carDto, String username){
        Car car = carMapper.toCar(carDto);
        User user = userRepository.findByUsername(username);
        CarResponse carResponse = new CarResponse();
        try{
            carLimit(user);
            currencyService.isValidCurrencyName(car.getCurrencyName());
        }catch (IllegalArgumentException e){
            carResponse.setError(e.getMessage());
            return carResponse;
        }
        if(car.getCheckCount()==null){
            car.setCheckCount(0);
        }
        if(hasSwearWords(car.getDetails())) {
            switch (car.getCheckCount()) {
                case 0, 1, 2, 3 -> {
                    carResponse.setError("swear words used, you have 3 more tries to change it. Tries: " + car.getCheckCount());
                    car.addCheckCount();
                }
                case 4 -> {
                    carResponse.setError("your car publish is not active. Car sent on moderation");
                    managerModerationNotifier.sendMail(car);
                    return carResponse;
                }
            }
        }
        Car savedCar = addCar(car, user);
        carResponse.setCar(carMapper.toDto(savedCar));
        return carResponse;
    }
    public CarResponse editMyCar(int id, CarDto carDto, String username){
        User user = userRepository.findByUsername(username);
        Car car = carMapper.toCar(carDto);
        List<Car> cars = user.getCars();
        String ccy = carDto.getCurrencyName();
        try{
            isPersonalCarAndIndex(cars, id);
            currencyService.isValidCurrencyName(ccy);
        }catch (IllegalArgumentException e){
            return new CarResponse().setError(e.getMessage());
        }
        Car foundCar = carRepository.findById(id).get();
        foundCar.update(car);
        Car save = carRepository.save(foundCar);
        return new CarResponse(carMapper.toDto(save));
    }
    public Car addCar(Car car, User user){
        List<Car> cars = user.getCars();
        car.setCurrencyValue(currencyValueRepository.findCurrencyValueByCcy(car.getCurrencyName()).getSale());
        cars.add(car);
        user.setCars(cars);
        User saved = userRepository.save(user);
        List<Car> savedCars = saved.getCars();
        return savedCars.get(savedCars.size()-1);
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
    public boolean hasSwearWords(String details) {
        String[] swears = swearWordsDto.getSwears();
        for (String swear : swears) {
            if (details.contains(swear)){
                return true;
            }
        }
        return false;
    }
    public CarResponse hasSwearWordsUpgraded(Car car, CarResponse carResponse){
        String[] swears = swearWordsDto.getSwears();
        for (String swear : swears) {
            if (car.getDetails().contains(swear)){
                switch (car.getCheckCount()) {
                    case 0, 1, 2, 3 -> {
                        carResponse.setCar(carMapper.toDto(car));
                        carResponse.setError("swear words used, you have 3 more tries to change it. Tries: " + car.getCheckCount());
                        car.addCheckCount();
                        return carResponse;
                    }
                    default-> {
                        carResponse.setError("your car publish is not active. Car sent on moderation");
                        managerModerationNotifier.sendMail(car);
                        return carResponse;
                    }
                }
            }
        }
        return carResponse;
    }

}

