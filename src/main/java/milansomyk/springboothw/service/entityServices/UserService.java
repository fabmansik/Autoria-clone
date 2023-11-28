package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.*;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        if (ObjectUtils.isEmpty(userDto)) {
            log.error("user is null");
            return responseContainer.setErrorMessageAndStatusCode("user is null", HttpStatus.BAD_REQUEST.value());
        }
        if (!StringUtils.hasText(user.getUsername())) {
            log.error("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null", HttpStatus.BAD_REQUEST.value());
        }
        if (!StringUtils.hasText(user.getPassword())) {
            log.error("password is null");
            return responseContainer.setErrorMessageAndStatusCode("password is null", HttpStatus.BAD_REQUEST.value());
        }
        if (!StringUtils.hasText(user.getEmail())) {
            log.error("email is null");
            return responseContainer.setErrorMessageAndStatusCode("email is null", HttpStatus.BAD_REQUEST.value());
        }
        if (ObjectUtils.isEmpty(user.getPhone())) {
            log.error("phone is null");
            return responseContainer.setErrorMessageAndStatusCode("phone is null", HttpStatus.BAD_REQUEST.value());
        }
        ResponseContainer usernameAlreadyExistsResponseContainer = isUsernameAlreadyExists(user.getUsername(), responseContainer);
        if (usernameAlreadyExistsResponseContainer.isError()) {
            log.error(usernameAlreadyExistsResponseContainer.getErrorMessage());
            return usernameAlreadyExistsResponseContainer;
        }
        ResponseContainer emailAlreadyExists = isEmailAlreadyExists(user.getEmail(), responseContainer);
        if (emailAlreadyExists.isError()) {
            log.error(emailAlreadyExists.getErrorMessage());
            return emailAlreadyExists;
        }
        ResponseContainer phoneNumberAlreadyUsed = isPhoneNumberAlreadyUsed(user.getPhone(), responseContainer);
        if (phoneNumberAlreadyUsed.isError()) {
            log.error(phoneNumberAlreadyUsed.getErrorMessage());
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
        ResponseContainer responseContainer = new ResponseContainer();
        if (ObjectUtils.isEmpty(carDto)) {
            log.error("car is null");
            return responseContainer.setErrorMessageAndStatusCode("car is null", HttpStatus.BAD_REQUEST.value());
        }
        Car car = carMapper.toCar(carDto);
        CarResponse carResponse = new CarResponse();
        User user;
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(user)) {
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        ResponseContainer validValues = isValidValues(car, responseContainer);
        if (validValues.isError()) {
            log.error(validValues.getErrorMessage());
            return validValues;
        }

        ResponseContainer carLimit = carLimit(user, responseContainer);
        if (carLimit.isError()) {
            log.error(carLimit.getErrorMessage());
            return carLimit;
        }

        ResponseContainer validCurrencyName = currencyService.validCurrencyName(car.getCurrencyName(), responseContainer);
        if (validCurrencyName.isError()) {
            log.error(validCurrencyName.getErrorMessage());
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
        if (addCar.isError()) {
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

    public ResponseContainer editMyCar(Integer id, CarDto carDto, String username) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (id == null) {
            log.error("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        if (carDto == null) {
            log.error("car is null");
            return responseContainer.setErrorMessageAndStatusCode("car is null", HttpStatus.BAD_REQUEST.value());
        }
        if (username == null) {
            log.error("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null", HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(user)) {
            log.error("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        Car car = carMapper.toCar(carDto);
        List<Car> cars = user.getCars();
        String ccy = carDto.getCurrencyName();
        CarResponse carResponse = new CarResponse();


        ResponseContainer validValues = isValidValues(car, responseContainer);
        if (validValues.isError()) {
            log.error(validValues.getErrorMessage());
            return validValues;
        }

        ResponseContainer personalCarAndIndex = isPersonalCarAndIndex(cars, id, responseContainer);
        if (personalCarAndIndex.isError()) {
            log.error(personalCarAndIndex.getErrorMessage());
            return personalCarAndIndex;
        }

        ResponseContainer validCurrencyName = currencyService.validCurrencyName(ccy, responseContainer);
        if (validCurrencyName.isError()) {
            log.error(validCurrencyName.getErrorMessage());
            return validCurrencyName;
        }
        Car foundCar;

        try {
            foundCar = carRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (foundCar == null) {
            log.error("car not found");
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
            log.error("your car publish is not active. Car sent on moderation");
            responseContainer.setErrorMessageAndStatusCode("your car publish is not active. Car sent on moderation", HttpStatus.ACCEPTED.value());
            foundCar.addCheckCount();
            foundCar.setActive(false);
            Car save;
            try {
                save = carRepository.save(foundCar);
            } catch (Exception e) {
                log.error(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            ResponseContainer sendMail = managerModerationNotifier.sendMail(save, responseContainer);
            if (sendMail.isError()) {
                log.error(sendMail.getErrorMessage());
                return responseContainer.setErrorMessageAndStatusCode(sendMail.getErrorMessage(), sendMail.getStatusCode());
            }
            return sendMail;
        }
        if (foundCar.getCheckCount() == 4) {
            log.error("your car publish is not active. Car sent on moderation. Wait for moderator gmail answer");
            responseContainer.setErrorMessageAndStatusCode("your car publish is not active. Car sent on moderation. Wait for moderator gmail answer", HttpStatus.ACCEPTED.value());
            return responseContainer;
        }

        Integer checkCount = foundCar.getCheckCount();
        foundCar.update(car);
        foundCar.setCheckCount(checkCount);
        Car save;
        try {
            save = carRepository.save(foundCar);
        } catch (Exception e) {
            log.error(e.getMessage());
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
        if (id == null) {
            log.error("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        if (username == null) {
            log.error("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null", HttpStatus.BAD_REQUEST.value());
        }
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user == null) {
            log.error("user not found");
            return responseContainer.setErrorMessageAndStatusCode("User not found", HttpStatus.BAD_REQUEST.value());
        }
        List<Car> cars = user.getCars();
        ResponseContainer personalCarAndIndex = isPersonalCarAndIndex(cars, id, responseContainer);
        if (personalCarAndIndex.isError()) {
            log.error(personalCarAndIndex.getErrorMessage());
            return personalCarAndIndex;
        }
        Car car = carRepository.findById(id).orElse(null);
        if (car == null) {
            log.error("car not found");
            return responseContainer.setErrorMessageAndStatusCode("Car not found", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        cars.remove(car);
        user.setCars(cars);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            carRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        responseContainer.setSuccessResult("Car with id: " + id + " was deleted");
        return responseContainer;
    }

    public ResponseContainer getMyCars(String username) {
        User user;
        ResponseContainer responseContainer = new ResponseContainer();
        if (username == null) {
            log.error("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null", HttpStatus.BAD_REQUEST.value());
        }
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user == null) {
            log.error("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        List<Car> cars = user.getCars();
        if (user.getPremium()) {
            responseContainer.setSuccessResult(new CarsResponse(cars.stream().map(carMapper::toDto).toList()).setAmount(cars.size()));
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
        try {
            allUsers = userRepository.findAll()
                    .stream()
                    .map(userMapper::toResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        allUsers.removeIf(userDto -> userDto.getRole().equals("ADMIN"));
        responseContainer.setSuccessResult(allUsers);
        return responseContainer;
    }

    public ResponseContainer getAllManagers() {
        ResponseContainer responseContainer = new ResponseContainer();
        List<User> managers;
        try {
            managers = userRepository.findByRole("MANAGER").orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (CollectionUtils.isEmpty(managers)) {
            log.error("managers not found");
            return responseContainer.setErrorMessageAndStatusCode("managers not found", HttpStatus.BAD_REQUEST.value());
        }
        List<UserDto> managersDto = managers.stream().map(userMapper::toResponseDto).toList();
        responseContainer.setSuccessResult(managersDto);
        return responseContainer;
    }

    public ResponseContainer banUser(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (id == null) {
            log.error("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        User foundUser;
        try {
            foundUser = userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(foundUser)) {
            log.error("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        if (!foundUser.isEnabled()) {
            log.error("user already banned");
            return responseContainer.setResultAndStatusCode("user already banned", HttpStatus.BAD_REQUEST.value());
        }
        foundUser.setEnabled(false);
        User saved;
        try {
            saved = userRepository.save(foundUser);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult(userMapper.toResponseDto(saved));
        return responseContainer;
    }

    public ResponseContainer unBanUser(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (id == null) {
            log.error("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try {
            user = userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user == null) {
            log.error("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        if (user.isEnabled()) {
            log.error("user is already not banned");
            return responseContainer.setResultAndStatusCode("user is already not banned", HttpStatus.BAD_REQUEST.value());
        }
        user.setEnabled(true);
        User saved;
        try {
            saved = userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult(userMapper.toResponseDto(saved));
        return responseContainer;
    }

    //Admin:
    public ResponseContainer createManager(UserDto userDto) {
        User user = userMapper.fromDto(userDto);
        ResponseContainer responseContainer = new ResponseContainer();
        if (ObjectUtils.isEmpty(userDto)) {
            log.error("user is null");
            return responseContainer.setErrorMessageAndStatusCode("user is null", HttpStatus.BAD_REQUEST.value());
        }
        if (!StringUtils.hasText(user.getUsername())) {
            log.error("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null", HttpStatus.BAD_REQUEST.value());
        }
        if (!StringUtils.hasText(user.getPassword())) {
            log.error("password is null");
            return responseContainer.setErrorMessageAndStatusCode("password is null", HttpStatus.BAD_REQUEST.value());
        }
        if (!StringUtils.hasText(user.getEmail())) {
            log.error("email is null");
            return responseContainer.setErrorMessageAndStatusCode("email is null", HttpStatus.BAD_REQUEST.value());
        }
        if (ObjectUtils.isEmpty(user.getPhone())) {
            log.error("phone is null");
            return responseContainer.setErrorMessageAndStatusCode("phone is null", HttpStatus.BAD_REQUEST.value());
        }
        ResponseContainer usernameAlreadyExistsResponseContainer = isUsernameAlreadyExists(user.getUsername(), responseContainer);
        if (usernameAlreadyExistsResponseContainer.isError()) {
            log.error(usernameAlreadyExistsResponseContainer.getErrorMessage());
            return usernameAlreadyExistsResponseContainer;
        }
        ResponseContainer emailAlreadyExists = isEmailAlreadyExists(user.getEmail(), responseContainer);
        if (emailAlreadyExists.isError()) {
            log.error(emailAlreadyExists.getErrorMessage());
            return emailAlreadyExists;
        }
        ResponseContainer phoneNumberAlreadyUsed = isPhoneNumberAlreadyUsed(user.getPhone(), responseContainer);
        if (phoneNumberAlreadyUsed.isError()) {
            log.error(phoneNumberAlreadyUsed.getErrorMessage());
            return phoneNumberAlreadyUsed;
        }
        user.setRole(Role.MANAGER.name());
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved;
        try {
            saved = userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setCreatedResult(userMapper.toResponseDto(saved));
        return responseContainer;
    }

    public ResponseContainer setManager(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (id == null) {
            log.error("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try {
            user = userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user == null) {
            log.error("user is null");
            return responseContainer.setErrorMessageAndStatusCode("user is null", HttpStatus.BAD_REQUEST.value());
        }
        if (user.getRole().equals("MANAGER")) {
            log.error("user is already MANAGER");
            return responseContainer.setErrorMessageAndStatusCode("user is already MANAGER", HttpStatus.BAD_REQUEST.value());
        }
        user.setRole("MANAGER");
        User saved;
        try {
            saved = userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult(userMapper.toResponseDto(saved));
        return responseContainer;
    }

    public ResponseContainer deleteUserById(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (id == null) {
            log.error("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try {
            user = userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(user)) {
            log.error("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        responseContainer.setSuccessResult("user with id: " + id + " was deleted");
        return responseContainer;
    }

    public ResponseContainer setPremium(Integer id) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (id == null) {
            log.error("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try {
            user = userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user == null) {
            log.error("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        if (user.getPremium()) {
            log.error("user is already has premium");
            return responseContainer.setErrorMessageAndStatusCode("user is already has premium", HttpStatus.BAD_REQUEST.value());
        }
        user.setPremium(true);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
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
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (sale == null) {
            log.error("Currency does not exists");
            return responseContainer.setErrorMessageAndStatusCode("Currency does not exists", HttpStatus.BAD_REQUEST.value());
        }
        car.setCurrencyValue(sale.getSale());
        cars.add(car);
        user.setCars(cars);
        User saved;
        try {
            saved = userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        List<Car> savedCars = saved.getCars();
        responseContainer.setSuccessResult(savedCars.get(savedCars.size() - 1));
        return responseContainer;
    }

    public ResponseContainer isUsernameAlreadyExists(String username, ResponseContainer responseContainer) {
        User user;
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (!ObjectUtils.isEmpty(user)) {
            return responseContainer.setErrorMessageAndStatusCode("username already exists", HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;
    }

    public ResponseContainer isEmailAlreadyExists(String email, ResponseContainer responseContainer) {
        User user;
        try {
            user = userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user != null) {
            return responseContainer.setErrorMessageAndStatusCode("email already exists", HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;

    }

    public ResponseContainer isPhoneNumberAlreadyUsed(Integer phone, ResponseContainer responseContainer) {
        User user;
        try {
            user = userRepository.findByPhone(phone).orElse(null);
        } catch (Exception e) {
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (user != null) {
            return responseContainer.setErrorMessageAndStatusCode("phone already exists", HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;
    }

    public ResponseContainer carLimit(User user, ResponseContainer responseContainer) {
        if (!(user.getPremium() || user.getCars().size() < 1)) {
            return responseContainer.setErrorMessageAndStatusCode("Not premium account! You can`t upload more than 1", HttpStatus.FORBIDDEN.value());
        }
        return responseContainer;
    }

    public ResponseContainer isPersonalCarAndIndex(List<Car> cars, int carId, ResponseContainer responseContainer) {
        List<Integer> list = cars.stream().map(Car::getId).toList();
        if (!list.contains(carId)) {
            log.error("not legal car id argument");
            return responseContainer.setErrorMessageAndStatusCode("Not legal car id argument", HttpStatus.BAD_REQUEST.value());
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

    public ResponseContainer isPremiumAccount(String username) {
        ResponseContainer responseContainer = new ResponseContainer();
        User user;
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        if (ObjectUtils.isEmpty(user)) {
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        responseContainer.setSuccessResult(user.getPremium());
        return responseContainer;
    }

    public ResponseContainer isValidValues(Car car, ResponseContainer responseContainer) {
        Producer producer;
        try {
            producer = producerRepository.findProducerByName(car.getProducer()).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(producer)){
            log.error("producer not legal");
            return responseContainer.setErrorMessageAndStatusCode("producer not legal", HttpStatus.BAD_REQUEST.value());
        }
        List<Model> producerModels = producer.getModels();
        if(CollectionUtils.isEmpty(producerModels)){
            log.error("models not found");
            return responseContainer.setErrorMessageAndStatusCode("models not found",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        List<String> modelNames = producerModels.stream().map(Model::getName).toList();
        if (!modelNames.contains(car.getModel())) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal model", HttpStatus.BAD_REQUEST.value());
        }
        List<String> allRegions = Arrays.stream(constants.getRegions()).toList();
        if (car.getRegion() != null && (!allRegions.contains(car.getRegion()))) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal region", HttpStatus.BAD_REQUEST.value());
        }
        List<String> allTypes = Arrays.stream(constants.getTypes()).toList();
        if (car.getType() != null && (!allTypes.contains(car.getType()))) {
            return responseContainer.setErrorMessageAndStatusCode("Not legal type", HttpStatus.BAD_REQUEST.value());
        }
        return responseContainer;
    }

    public ResponseContainer isValidValues(String producer, String model, String region, String types, ResponseContainer responseContainer) {
        List<Producer> allProducers;
        try {
            allProducers = producerRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (CollectionUtils.isEmpty(allProducers)) {
            log.error("cars not found any");
            return responseContainer.setResultAndStatusCode("cars not found any", HttpStatus.NO_CONTENT.value());
        }
        List<String> list = allProducers.stream().map(Producer::getName).map(String::toLowerCase).toList();
        if (StringUtils.hasText(producer) && (!list.contains(producer.toLowerCase()))) {
            log.error("not legal producer");
            return responseContainer.setErrorMessageAndStatusCode("not legal producer", HttpStatus.BAD_REQUEST.value());
        }

        List<Model> allModels;
        try {
            allModels = modelRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (CollectionUtils.isEmpty(allModels)) {
            log.error("models not found any");
            return responseContainer.setResultAndStatusCode("models not found any", HttpStatus.NO_CONTENT.value());
        }
        List<String> modelNames = allModels.stream().map(Model::getName).map(String::toLowerCase).toList();
        if (StringUtils.hasText(model) && (!modelNames.contains(model.toLowerCase()))) {
            log.error("not legal model");
            return responseContainer.setErrorMessageAndStatusCode("not legal model", HttpStatus.BAD_REQUEST.value());
        }

        List<String> allRegions = Arrays.stream(constants.getRegions()).map(String::toLowerCase).toList();
        if (StringUtils.hasText(region) && (!allRegions.contains(region.toLowerCase()))) {
            log.error("not legal region");
            return responseContainer.setErrorMessageAndStatusCode("not legal region", HttpStatus.BAD_REQUEST.value());
        }

        List<String> allTypes = Arrays.stream(constants.getTypes()).map(String::toLowerCase).toList();
        if (StringUtils.hasText(types) && (!allTypes.contains(types.toLowerCase()))) {
            log.error("not legal type");
            return responseContainer.setErrorMessageAndStatusCode("not legal type", HttpStatus.BAD_REQUEST.value());
        }
        responseContainer.setError(false);
        return responseContainer;
    }
}

