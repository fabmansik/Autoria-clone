package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.entity.*;

import milansomyk.springboothw.entity.Currency;
import milansomyk.springboothw.enums.Role;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.*;
import milansomyk.springboothw.service.DbUserDetailsService;
import milansomyk.springboothw.service.mails.ManagerModerationNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Data
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ProducerRepository producerRepository;
    private final ModelRepository modelRepository;
    private final Constants constants;
    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final CurrencyRepository currencyRepository;
    private final CurrencyService currencyService;
    private final MailSender mailSender;
    private final ManagerModerationNotifier managerModerationNotifier;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DbUserDetailsService dbUserDetailsService;

    //Buyer:
    public ResponseContainer register(UserDto userDto) {
        User user = userMapper.fromDto(userDto);
        ResponseContainer responseContainer = new ResponseContainer();
        if(user.getUsername()==null || user.getEmail()!=null || user.getPhone()!=null){
            log.info("ERROR: User is empty");
            return responseContainer.setErrorMessageAndStatusCode("User is empty",HttpStatus.BAD_REQUEST.value());
        }
        ResponseContainer usernameAlreadyExistsResponseContainer = isUsernameAlreadyExists(user.getUsername(), responseContainer);
        if (usernameAlreadyExistsResponseContainer.isError()) {
            log.info("ERROR:"+usernameAlreadyExistsResponseContainer.getErrorMessage());
            return usernameAlreadyExistsResponseContainer;
        }
        ResponseContainer emailAlreadyExists = isEmailAlreadyExists(user.getEmail(), responseContainer);
        if (emailAlreadyExists.isError()){
            log.info("ERROR:"+emailAlreadyExists.getErrorMessage());
            return emailAlreadyExists;
        }
        ResponseContainer phoneNumberAlreadyUsed = isPhoneNumberAlreadyUsed(user.getPhone(), responseContainer);
        if (phoneNumberAlreadyUsed.isError()){
            log.info("ERROR:"+phoneNumberAlreadyUsed.getErrorMessage());
            return phoneNumberAlreadyUsed;
        }

        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        User savedUser = userRepository.save(user.setPremium(false).setEnabled(true).setRole(Role.SELLER.name()));
        responseContainer.setCreatedResult(userMapper.toResponseDto(savedUser));
        return responseContainer;
    }

    //Seller:
    public ResponseContainer createCar(CarDto carDto, String username) {
        Car car = carMapper.toCar(carDto);
        ResponseContainer responseContainer = new ResponseContainer();
        CarResponse carResponse = new CarResponse();
        User user = new User();
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e){
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        if (car == null){
            return responseContainer.setErrorMessageAndStatusCode("Empty car",HttpStatus.BAD_REQUEST.value());
        }

        if (user != null) {
            ResponseContainer validValues = isValidValues(car, responseContainer);
            if(validValues.isError()){
                log.info("ERROR: "+validValues.getErrorMessage());
                return validValues;
            }

            ResponseContainer carLimit = carLimit(user,responseContainer);
            if(carLimit.isError()){
                log.info("ERROR: "+carLimit.getErrorMessage());
                return carLimit;
            }

            ResponseContainer validCurrencyName = currencyService.validCurrencyName(car.getCurrencyName(), responseContainer);
            if(validCurrencyName.isError()){
                log.info("ERROR: "+validCurrencyName.getErrorMessage());
                return validCurrencyName;
            }
            if (car.getCheckCount() == null) {
                car.setCheckCount(0);
            }
            if (hasSwearWords(car.getDetails())) {
                responseContainer.setErrorMessage("swear words used, you have 3 more tries to change it. Tries: " + car.getCheckCount());
                car.setActive(false);
            } else {
                car.setActive(true);
            }
            ResponseContainer addCar = addCar(car, user, responseContainer);
            if(addCar.isError()){
                return addCar;
            }
            Car savedCar = (Car) addCar.getResult();
            if (user.getPremium()) {
                carResponse.setCarPremium(carMapper.toDto(savedCar));
            } else {
                carResponse.setCarBasic(carMapper.toBasicDto(savedCar));
            }
            responseContainer.setCreatedResult(carResponse);
            return responseContainer;
        }
        return responseContainer.setErrorMessageAndStatusCode("user not found",HttpStatus.BAD_REQUEST.value());
    }

    public ResponseContainer editMyCar(Integer id, CarDto carDto, String username) {
        ResponseContainer responseContainer = new ResponseContainer();
        if(id == null){
            log.info("ERROR: id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null",HttpStatus.BAD_REQUEST.value());
        }
        if(carDto == null){
            log.info("ERROR: car is null");
            return responseContainer.setErrorMessageAndStatusCode("car is null",HttpStatus.BAD_REQUEST.value());
        }
        if(username == null){
            log.info("ERROR: username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null",HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try{
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        Car car = carMapper.toCar(carDto);
        List<Car> cars = user.getCars();
        String ccy = carDto.getCurrencyName();
        CarResponse carResponse = new CarResponse();


        ResponseContainer validValues = isValidValues(car, responseContainer);
        if(validValues.isError()){
            log.info(validValues.getErrorMessage());
            return validValues;
        }

        ResponseContainer personalCarAndIndex = isPersonalCarAndIndex(cars, id, responseContainer);
        if(personalCarAndIndex.isError()){
            log.info(personalCarAndIndex.getErrorMessage());
            return personalCarAndIndex;
        }

        ResponseContainer validCurrencyName = currencyService.validCurrencyName(ccy, responseContainer);
        if (validCurrencyName.isError()){
            log.info(validCurrencyName.getErrorMessage());
            return validCurrencyName;
        }
        Car foundCar;

        try{
           foundCar = carRepository.findById(id).orElse(null);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if( foundCar == null){
            log.info("car not found");
            return responseContainer.setErrorMessageAndStatusCode("Car not found", HttpStatus.BAD_REQUEST.value());
        }

        if (hasSwearWords(car.getDetails()) && foundCar.getCheckCount() < 4) {
            foundCar.addCheckCount();
            foundCar.setActive(false);
            responseContainer.setErrorMessage("swear words used, you have 3 more tries to change it. Tries: " + foundCar.getCheckCount());
        } else {
            foundCar.setActive(true);
        }
        if (foundCar.getCheckCount() == 3) {
            log.info("your car publish is not active. Car sent on moderation");
            responseContainer.setErrorMessageAndStatusCode("your car publish is not active. Car sent on moderation",HttpStatus.ACCEPTED.value());
            foundCar.addCheckCount();
            foundCar.setActive(false);
            Car save;
            try {
                save = carRepository.save(foundCar);
            } catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            ResponseContainer sendMail = managerModerationNotifier.sendMail(save, responseContainer);
            if(sendMail.isError()){
                log.info(sendMail.getErrorMessage());
                return responseContainer.setErrorMessageAndStatusCode(sendMail.getErrorMessage(), sendMail.getStatusCode());
            }
            return responseContainer;
        }
        if (foundCar.getCheckCount() == 4) {
            log.info("your car publish is not active. Car sent on moderation. Wait for moderator gmail answer");
            responseContainer.setErrorMessageAndStatusCode("your car publish is not active. Car sent on moderation. Wait for moderator gmail answer",HttpStatus.ACCEPTED.value());
            return responseContainer;
        }

        Integer checkCount = foundCar.getCheckCount();
        foundCar.update(car);
        foundCar.setCheckCount(checkCount);
        Car save;
        try{
            save = carRepository.save(foundCar);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        if (user.getPremium()) {
            carResponse.setCarPremium(carMapper.toDto(save));
        } else {
            carResponse.setCarBasic(carMapper.toBasicDto(save));
        }
        responseContainer.setSuccessResult(carResponse);
        return responseContainer;
    }

    public ResponseContainer deleteMyCar(Integer id, String username) {
        ResponseContainer responseContainer = new ResponseContainer();
        User user;
        if (id==null){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null",HttpStatus.BAD_REQUEST.value());
        }
        if(username==null){
            log.info("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null", HttpStatus.BAD_REQUEST.value());
        }
        try {
            user = userRepository.findByUsername(username).orElse(null);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(user==null){
            log.info("user not found");
            return responseContainer.setErrorMessageAndStatusCode("User not found", HttpStatus.BAD_REQUEST.value());
        }
        List<Car> cars = user.getCars();
        ResponseContainer personalCarAndIndex = isPersonalCarAndIndex(cars, id, responseContainer);
        if (personalCarAndIndex.isError()){
            log.info(personalCarAndIndex.getErrorMessage());
            return personalCarAndIndex;
        }
        Car car = carRepository.findById(id).orElse(null);
        if (car == null){
            log.info("car not found");
            return responseContainer.setErrorMessageAndStatusCode("Car not found",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        cars.remove(car);
        user.setCars(cars);
        try{
            userRepository.save(user);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try{
            carRepository.deleteById(id);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        responseContainer.setSuccessResult("Car with id: " + id + " was deleted");
        return responseContainer;
    }

    public ResponseContainer getMyCars(String username) {
        User user;
        ResponseContainer responseContainer = new ResponseContainer();
        if(username==null){
            log.info("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null",HttpStatus.BAD_REQUEST.value());
        }
        try{
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user==null){
            log.info("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found",HttpStatus.BAD_REQUEST.value());
        }
        List<Car> cars = user.getCars();
        if (user.getPremium()) {
            responseContainer.setSuccessResult( new CarsResponse(cars.stream().map(carMapper::toDto).toList()).setAmount(cars.size()));
            return responseContainer;
        } else {
            responseContainer.setSuccessResult(new CarsResponse().setCarsBasic(cars.stream().map(carMapper::toBasicDto).toList()).setAmount(cars.size()));
            return responseContainer;
        }
    }

    //Manager:
    public ResponseContainer getAllUsers() {
        ResponseContainer responseContainer = new ResponseContainer();
        List<UserDto> allUsers;
        try{
            allUsers = userRepository.findAll()
                    .stream()
                    .map(userMapper::toResponseDto)
                    .collect(Collectors.toList());
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        allUsers.removeIf(userDto -> userDto.getRole().equals("ADMIN"));
        responseContainer.setSuccessResult(allUsers);
        return responseContainer;
    }

    public ResponseContainer getAllManagers() {
        ResponseContainer responseContainer = new ResponseContainer();
        List<UserDto> managers;
        try{
            managers = userRepository.findByRole("MANAGER").orElse(null).stream().map(userMapper::toResponseDto).toList();
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setResult(managers);
        return responseContainer;
    }

    public ResponseContainer banUser(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if(id == null){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null",HttpStatus.BAD_REQUEST.value());
        }
        User foundUser;
        try {
             foundUser = userRepository.findById(id).orElse(null);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        foundUser.setEnabled(false);
        User saved;
        try{
            saved = userRepository.save(foundUser);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setResult(userMapper.toResponseDto(saved));
        return responseContainer;
    }

    public ResponseContainer unBanUser(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if(id == null){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null",HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try{
            user = userRepository.findById(id).orElse(null);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(user==null){
            log.info("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found",HttpStatus.BAD_REQUEST.value());
        }
        user.setEnabled(true);
        User saved;
        try{
            saved = userRepository.save(user);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setResult(new UserResponse(userMapper.toResponseDto(saved), null));
        return responseContainer;
    }

    //Admin:
    public ResponseContainer createManager(UserDto userDto) {
        User user = userMapper.fromDto(userDto);
        ResponseContainer responseContainer = new ResponseContainer();
        if (user == null){
            log.info("user is null");
            return responseContainer.setErrorMessageAndStatusCode("user is null",HttpStatus.BAD_REQUEST.value());
        }
        ResponseContainer usernameAlreadyExistsResponseContainer = isUsernameAlreadyExists(user.getUsername(), responseContainer);
        if (usernameAlreadyExistsResponseContainer.isError()) {
            log.info(usernameAlreadyExistsResponseContainer.getErrorMessage());
            return usernameAlreadyExistsResponseContainer;
        }
        ResponseContainer emailAlreadyExists = isEmailAlreadyExists(user.getEmail(), responseContainer);
        if (emailAlreadyExists.isError()){
            log.info(emailAlreadyExists.getErrorMessage());
            return emailAlreadyExists;
        }
        ResponseContainer phoneNumberAlreadyUsed = isPhoneNumberAlreadyUsed(user.getPhone(), responseContainer);
        if (phoneNumberAlreadyUsed.isError()){
            log.info(phoneNumberAlreadyUsed.getErrorMessage());
            return phoneNumberAlreadyUsed;
        }
        user.setRole(Role.MANAGER.name());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved;
        try{
            saved = userRepository.save(user);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setCreatedResult(userMapper.toResponseDto(saved));
        return responseContainer;
    }

    public ResponseContainer setManager(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if(id == null){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try{
            user = userRepository.findById(id).orElse(null);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(user == null){
            log.info("user is null");
            return responseContainer.setErrorMessageAndStatusCode("user is null",HttpStatus.BAD_REQUEST.value());
        }
        user.setRole("MANAGER");
        User saved;
        try{
            saved = userRepository.save(user);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult(userMapper.toResponseDto(saved));
        return responseContainer;
    }

    public ResponseContainer deleteUserById(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if(id == null){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        try {
            userRepository.deleteById(id);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult("user with id: " + id + " was deleted");
        return responseContainer;
    }

    public ResponseContainer setPremium(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if(id == null){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null",HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try{
            user = userRepository.findById(id).get();
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(user == null){
            return responseContainer.setErrorMessageAndStatusCode("user not found",HttpStatus.BAD_REQUEST.value());
        }
        user.setPremium(true);
        try{
            userRepository.save(user);
        }catch (Exception e){
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult("user with id: " + id + " was set to premium account");
        return responseContainer;
    }

    // Tools:
    public ResponseContainer addCar(Car car, User user, ResponseContainer responseContainer) {
        car.setCreationDate(new Date(System.currentTimeMillis()));
        car.setWatchesPerDay(0);
        car.setWatchesTotal(0);
        car.setWatchesPerWeek(0);
        car.setWatchesPerMonth(0);
        List<Car> cars = user.getCars();
        Currency sale;
        try {
            sale = currencyRepository.findCurrencyByCcy(car.getCurrencyName()).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(sale == null){
            log.info("ERROR: Currency does not exists");
            return responseContainer.setErrorMessageAndStatusCode("Currency does not exists",HttpStatus.BAD_REQUEST.value());
        }
        car.setCurrencyValue(sale.getSale());
        cars.add(car);
        user.setCars(cars);
        User saved;
        try {
            saved = userRepository.save(user);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        List<Car> savedCars = saved.getCars();
        responseContainer.setSuccessResult(savedCars.get(savedCars.size() - 1));
        return responseContainer;
    }
    public ResponseContainer isUsernameAlreadyExists(String username, ResponseContainer responseContainer) {
        User user = new User();
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user != null) {
            return responseContainer.setErrorMessageAndStatusCode("username already exists", HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;
    }
    public ResponseContainer isEmailAlreadyExists(String email, ResponseContainer responseContainer) {
        User user = new User();
        try{
            user = userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user != null) {
            return responseContainer.setErrorMessageAndStatusCode("username already exists", HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;

    }
    public ResponseContainer isPhoneNumberAlreadyUsed(Integer phone, ResponseContainer responseContainer) {
        User user = new User();
        try{
            user = userRepository.findByPhone(phone).orElse(null);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user != null) {
            return responseContainer.setErrorMessageAndStatusCode("username already exists", HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;
    }
    public ResponseContainer carLimit(User user, ResponseContainer responseContainer) {
        if (!(user.getPremium() || user.getCars().size() < 1)) {
            return responseContainer.setErrorMessageAndStatusCode("Not premium account! You can`t upload more than 1",HttpStatus.FORBIDDEN.value());
        }
        return responseContainer;
    }
    public ResponseContainer isPersonalCarAndIndex(List<Car> cars, int carId, ResponseContainer responseContainer) {
        List<Integer> list = cars.stream().map(Car::getId).toList();
        if (!list.contains(carId)) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal car id argument",HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;
    }
    public boolean hasSwearWords(String details) {
        String[] swears = constants.getSwears();
        for (String swear : swears) {
            if (details.contains(swear)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPremiumAccount(String username) {
        User user = new User();
        try{
            user = userRepository.findByUsername(username).orElse(null);
        }catch (Exception e){
            throw e;
        }

        if (user != null) {
            return user.getPremium();
        }
        return false;
    }

    public ResponseContainer isValidValues(Car car, ResponseContainer responseContainer) {
        List<Producer> producers = new ArrayList<>();
        try {
            producers = producerRepository.findAll();
        } catch (Exception e){
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        Map<String, List<Model>> producersAndModels = producers.stream().collect(toMap(Producer::getName, Producer::getModels));
        log.info(producersAndModels.toString());
        if (car.getProducer() == null) {
        } else if (!producersAndModels.containsKey(car.getProducer())) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal producer",HttpStatus.BAD_REQUEST.value());
        }

        if (car.getModel() == null) {
        } else if (!producersAndModels.containsValue(car.getModel())) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal model",HttpStatus.BAD_REQUEST.value());
        }
        List<String> allRegions = Arrays.stream(constants.getRegions()).toList();
        if (car.getRegion() == null) {
        } else if (!allRegions.contains(car.getRegion())) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal region",HttpStatus.BAD_REQUEST.value());
        }
        List<String> allTypes = Arrays.stream(constants.getTypes()).toList();
        if (car.getType() == null) {
        } else if (!allTypes.contains(car.getType())) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal type",HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;
    }

    public void isValidValues(String producer, String model, String region, String types) {
        List<Producer> allProducers = producerRepository.findAll();
        List<String> list = allProducers.stream().map(Producer::getName).toList();
        if (producer == null) {
        } else if (!list.contains(producer)) {
            throw new IllegalArgumentException("Not legal producer");
        }
        List<Model> allModels = modelRepository.findAll();
        List<String> modelNames = allModels.stream().map(Model::getName).toList();
        if (model == null) {
        } else if (!modelNames.contains(model)) {
            throw new IllegalArgumentException("Not legal model");
        }
        List<String> allRegions = Arrays.stream(constants.getRegions()).toList();
        if (region == null) {
        } else if (!allRegions.contains(region)) {
            throw new IllegalArgumentException("Not legal region");
        }
        List<String> allTypes = Arrays.stream(constants.getTypes()).toList();
        if (types == null) {
        } else if (!allTypes.contains(types)) {
            throw new IllegalArgumentException("Not legal type");
        }
    }
}

