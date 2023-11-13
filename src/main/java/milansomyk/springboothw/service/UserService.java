package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.CarTypeDto;
import milansomyk.springboothw.dto.RegionDto;
import milansomyk.springboothw.dto.SwearWordsDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.entity.*;

import java.time.LocalDate;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ProducerRepository producerRepository;
    private final ModelRepository modelRepository;
    private final RegionDto regionDto;
    private final CarTypeDto carTypeDto;
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
            isValidValues(car);
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
            carResponse.setError("swear words used, you have 3 more tries to change it. Tries: " + car.getCheckCount());
            car.setActive(false);
        }else{
            car.setActive(true);
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
        CarResponse carResponse = new CarResponse();
        try{
            isValidValues(car);
            isPersonalCarAndIndex(cars, id);
            currencyService.isValidCurrencyName(ccy);
        }catch (IllegalArgumentException e){
            return new CarResponse().setError(e.getMessage());
        }
        Car foundCar = carRepository.findById(id).get();
        if(hasSwearWords(car.getDetails())&&foundCar.getCheckCount()<4) {
            foundCar.addCheckCount();
            foundCar.setActive(false);
            carResponse.setError("swear words used, you have 3 more tries to change it. Tries: " + foundCar.getCheckCount());
        }else{foundCar.setActive(true);}
        if(foundCar.getCheckCount()==3){
            carResponse.setError("your car publish is not active. Car sent on moderation");
            foundCar.addCheckCount();
            foundCar.setActive(false);
            Car save = carRepository.save(foundCar);
            managerModerationNotifier.sendMail(save);
            return carResponse;
        }
        if(foundCar.getCheckCount()==4){
            carResponse.setError("your car publish is not active. Car sent on moderation. Wait for moderator gmail answer");
            return carResponse;
        }

        Integer checkCount = foundCar.getCheckCount();
        System.out.println();
        foundCar.update(car);
        foundCar.setCheckCount(checkCount);

        Car save = carRepository.save(foundCar);
        carResponse.setCar(carMapper.toDto(save));
        return carResponse;
    }
    public Car addCar(Car car, User user){
        car.setCreationDate(new Date(System.currentTimeMillis()));
        car.setWatchesPerDay(0);
        car.setWatchesTotal(0);
        car.setWatchesPerWeek(0);
        car.setWatchesPerMonth(0);
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
    public void isPersonalCarAndIndex(List<Car> cars, int carId){
        List<Integer> list = cars.stream().map(Car::getId).toList();
        if (!list.contains(carId)){
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
    public boolean isPremiumAccount(String username){
        return userRepository.findByUsername(username).getPremium();
    }
    public void addImages(List<Image> images){
        for (Image image : images) {

        }
    }
    public void isValidValues(Car car){
        List<Producer> allProducers = producerRepository.findAll();
        List<String> list = allProducers.stream().map(Producer::getName).toList();
        if(car.getProducer()==null){
        }else if (!list.contains(car.getProducer())){
            throw new IllegalArgumentException("Not legal producer");
        }
        List<Model> allModels = modelRepository.findAll();
        List<String> modelNames = allModels.stream().map(Model::getName).toList();
        if(car.getModel()==null){}
        else if(!modelNames.contains(car.getModel())){
            throw new IllegalArgumentException("Not legal model");
        }
        List<String> allRegions = Arrays.stream(regionDto.getRegions()).toList();
        if(car.getRegion()==null){}
        else if(!allRegions.contains(car.getRegion())){
            throw new IllegalArgumentException("Not legal region");
        }
        List<String> allTypes = Arrays.stream(carTypeDto.getTypes()).toList();
        if(car.getType()==null){}
        else if(!allTypes.contains(car.getType())){
            throw new IllegalArgumentException("Not legal type");
        }
    };
    public void isValidValues(String producer, String model, String region, String types){
        List<Producer> allProducers = producerRepository.findAll();
        List<String> list = allProducers.stream().map(Producer::getName).toList();
        if(producer==null){
        }else if (!list.contains(producer)){
            throw new IllegalArgumentException("Not legal producer");
        }
        List<Model> allModels = modelRepository.findAll();
        List<String> modelNames = allModels.stream().map(Model::getName).toList();
        if(model==null){}
        else if(!modelNames.contains(model)){
            throw new IllegalArgumentException("Not legal model");
        }
        List<String> allRegions = Arrays.stream(regionDto.getRegions()).toList();
        if(region==null){}
        else if(!allRegions.contains(region)){
            throw new IllegalArgumentException("Not legal region");
        }
        List<String> allTypes = Arrays.stream(carTypeDto.getTypes()).toList();
        if(types==null){}
        else if(!allTypes.contains(types)){
            throw new IllegalArgumentException("Not legal type");
        }
    };
}

