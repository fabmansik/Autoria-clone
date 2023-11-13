package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.consts.CarTypeConst;
import milansomyk.springboothw.dto.consts.RegionConst;
import milansomyk.springboothw.dto.consts.SwearWordsConst;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.entity.*;

import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.*;
import milansomyk.springboothw.service.DbUserDetailsService;
import milansomyk.springboothw.service.mails.ManagerModerationNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ProducerRepository producerRepository;
    private final ModelRepository modelRepository;
    private final RegionConst regionConst;
    private final CarTypeConst carTypeConst;
    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final CurrencyRepository currencyRepository;
    private final CurrencyService currencyService;
    private final SwearWordsConst swearWordsConst;
    private final MailSender mailSender;
    private final ManagerModerationNotifier managerModerationNotifier;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DbUserDetailsService dbUserDetailsService;

    //Buyer:
    public UserResponse register(UserDto userDto){
        User user = userMapper.fromDto(userDto);
        try{
            isUsernameAlreadyExists(user.getUsername());
            isEmailAlreadyExists(user.getEmail());
            isPhoneNumberAlreadyUsed(user.getPhone());
        }
        catch (IllegalArgumentException e){
            return new UserResponse(null,e.getMessage());
        }

        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        User savedUser = userRepository.save(user.setPremium(false).setEnabled(true).setRole(Role.SELLER.name()));
        return new UserResponse(userMapper.toDto(savedUser),null);
    }

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
        if(user.getPremium()){
            carResponse.setCarPremium(carMapper.toDto(savedCar));
        }else{
            carResponse.setCarBasic(carMapper.toBasicDto(savedCar));
        }
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
        if(user.getPremium()){
            carResponse.setCarPremium(carMapper.toDto(save));
        }else{
            carResponse.setCarBasic(carMapper.toBasicDto(save));
        }
        return carResponse;
    }
    public Car addCar(Car car, User user){
        car.setCreationDate(new Date(System.currentTimeMillis()));
        car.setWatchesPerDay(0);
        car.setWatchesTotal(0);
        car.setWatchesPerWeek(0);
        car.setWatchesPerMonth(0);
        List<Car> cars = user.getCars();
        car.setCurrencyValue(currencyRepository.findCurrencyByCcy(car.getCurrencyName()).getSale());
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
        if (user.getPremium()){
            return new CarsResponse(cars.stream().map(carMapper::toDto).toList()).setAmount(cars.size());
        }else{
            return new CarsResponse().setCarsBasic(cars.stream().map(carMapper::toBasicDto).toList()).setAmount(cars.size());
        }

    }

    //Manager:
    public List<UserDto> getAllUsers(){
        List<UserDto> allUsers = userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        allUsers.removeIf(userDto -> userDto.getRole().equals("ADMIN"));
        return allUsers;
    }
    public List<UserDto> getAllManagers(){
        return userRepository.findByRole("MANAGER").stream().map(userMapper::toDto).toList();
    }
    public UserResponse banUser(int id){
        User foundUser = userRepository.findById(id).get();
        foundUser.setEnabled(false);
        User saved = userRepository.save(foundUser);
        return new UserResponse(userMapper.toDto(saved), null);
    }
    public UserResponse unBanUser(int id){
        User user = userRepository.findById(id).get();
        user.setEnabled(true);
        User saved = userRepository.save(user);
        return new UserResponse(userMapper.toDto(saved),null);
    }

    //Admin:
    public UserResponse createManager(UserDto userDto){
        User user = userMapper.fromDto(userDto);
        try{
            isUsernameAlreadyExists(user.getUsername());
            isEmailAlreadyExists(user.getEmail());
            isPhoneNumberAlreadyUsed(user.getPhone());

        }catch (IllegalArgumentException e){
            return new UserResponse(null,e.getMessage());
        }
        user.setRole(Role.MANAGER.name());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return new UserResponse(userMapper.toDto(saved), null);
    }
    public UserDto setManager(int id){
        User user = userRepository.findById(id).get();
        user.setRole("MANAGER");
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
    public String deleteUserById(int id){
        userRepository.deleteById(id);
        return "user with id: "+id+" was deleted";
    }
    public String setPremium(int id){
        User user = userRepository.findById(id).get();
        user.setPremium(true);
        userRepository.save(user);
        return "user with id: "+id+" was set to premium account";
    }

    // Tools:

    public void isUsernameAlreadyExists(String username){
        if(userRepository.findByUsername(username) != null){
            throw new IllegalArgumentException("username already exists");
        }
    }
    public void isEmailAlreadyExists(String email){
        if(userRepository.findByEmail(email) != null){
            throw new IllegalArgumentException("email already in use");
        }
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
        }
    }
    public void isPhoneNumberAlreadyUsed(Integer phone){
        if(userRepository.findByPhone(phone) != null){
            throw new IllegalArgumentException("phone number already in use");
        }
    }
    public boolean hasSwearWords(String details) {
        String[] swears = swearWordsConst.getSwears();
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
        List<String> allRegions = Arrays.stream(regionConst.getRegions()).toList();
        if(car.getRegion()==null){}
        else if(!allRegions.contains(car.getRegion())){
            throw new IllegalArgumentException("Not legal region");
        }
        List<String> allTypes = Arrays.stream(carTypeConst.getTypes()).toList();
        if(car.getType()==null){}
        else if(!allTypes.contains(car.getType())){
            throw new IllegalArgumentException("Not legal type");
        }
    }
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
        List<String> allRegions = Arrays.stream(regionConst.getRegions()).toList();
        if(region==null){}
        else if(!allRegions.contains(region)){
            throw new IllegalArgumentException("Not legal region");
        }
        List<String> allTypes = Arrays.stream(carTypeConst.getTypes()).toList();
        if(types==null){}
        else if(!allTypes.contains(types)){
            throw new IllegalArgumentException("Not legal type");
        }
    }
}

