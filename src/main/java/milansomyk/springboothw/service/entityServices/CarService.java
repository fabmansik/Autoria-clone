package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.AverageResponse;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.*;
import milansomyk.springboothw.mapper.CarMapper;
import milansomyk.springboothw.repository.*;
import milansomyk.springboothw.service.JwtService;
import milansomyk.springboothw.service.mails.AdminNotFoundNotifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@Service
@Slf4j
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;
    private final AdminNotFoundNotifier adminNotFoundNotifier;
    private final ModelRepository modelRepository;
    private final ProducerRepository producerRepository;
    private final Constants constants;
    private final ImageRepository imageRepository;
    private final JwtService jwtService;

    public ResponseContainer findAveragePrice(String producer, String model, String ccy, String region, String username) {
        ResponseContainer responseContainer = new ResponseContainer();
        if (!userService.isPremiumAccount(username)) {
            log.info("not premium account");
            return responseContainer.setErrorMessageAndStatusCode("not premium account", HttpStatus.FORBIDDEN.value());
        }
        String currency;
        List<Car> cars;
        if (StringUtils.hasText(region)) {
            try{
                cars = carRepository.findByProducerAndModelAndActiveAndRegion(producer, model, true, region);
            }catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else {
            try{
                cars = carRepository.findByProducerAndModelAndActive(producer, model, true);
            }catch (Exception e){
                log.info(e.getMessage());
                return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
        if (!StringUtils.hasText(ccy)) {
            cars.forEach(car -> car.setPrice(
                    (int) currencyService.transferToCcy(ccy, car.getCurrencyName(), car.getPrice(), responseContainer).getResult()
            ));
            currency = ccy;
        } else {
            cars.forEach(car -> car.setPrice(
                    (int) currencyService.transferToCcy("USD", car.getCurrencyName(), car.getPrice(),responseContainer).getResult()
            ));
            currency = "USD";
        }
        List<Integer> prices = cars.stream().map(Car::getPrice).toList();
        Integer average = averageCalculator(prices);
        responseContainer.setSuccessResult(new AverageResponse(average, currency, prices.size()));
        return responseContainer;
    }

    public void addWatchesTotal(int id) {
        Car car = carRepository.findById(id).get();
        car.addWatches();
        carRepository.save(car);
    }

    public CarResponse findById(int id) {
        Car car = carRepository.findById(id).get();
        CarDto dto = carMapper.toDto(car);
        return new CarResponse(dto);
    }

    public String deleteById(int id) {
        Car car = carRepository.findById(id).get();
        User user = userRepository.findByCarsContaining(car).orElse(null);
        List<Car> cars = user.getCars();
        cars.remove(car);
        user.setCars(cars);
        userRepository.save(user);
        carRepository.deleteById(id);
        return "Car with this id: " + id + ", was deleted";
    }

    public CarsResponse getCars(String producer, String model, String region, Integer minPrice, Integer maxPrice, String ccy, String type) {
        List<Car> allCars = carRepository.findAllActive();
        try{
            userService.isValidValues(producer, model, region, type);
        }catch (IllegalArgumentException e){
            return new CarsResponse().setError(e.getMessage());
        }
        if (producer != null) {
            allCars.removeIf(car -> !Objects.equals(car.getProducer(), producer));
        }
        if (model != null) {
            allCars.removeIf(car -> !Objects.equals(car.getModel(), model));
        }
        if (region != null) {
            allCars.removeIf(car -> !Objects.equals(car.getRegion(), region));
        }
        if (ccy != null) {
            if (minPrice != null) {
                allCars.removeIf(car -> currencyService.transferToCcy(ccy, car.getCurrencyName(), car.getPrice()) <= minPrice);
            }
            if (maxPrice != null) {
                allCars.removeIf(car -> currencyService.transferToCcy(ccy, car.getCurrencyName(), car.getPrice()) >= maxPrice);
            }
        } else {
            if (minPrice != null) {
                allCars.removeIf(car -> currencyService.transferToCcy("USD", car.getCurrencyName(), car.getPrice()) <= minPrice);
            }
            if (maxPrice != null) {
                allCars.removeIf(car -> currencyService.transferToCcy("USD", car.getCurrencyName(), car.getPrice()) >= maxPrice);
            }
        }
        if (type != null){
            allCars.removeIf(car -> !Objects.equals(car.getType(),type));
        }

        return new CarsResponse(allCars.stream().map(carMapper::toDto).toList()).setAmount(allCars.size());
    }

    public String notifyNotFound(String model, String producer) {
        if (model != null) {
            if (modelRepository.findByName(model) != null) {
                return "Model already exists";
            }
            adminNotFoundNotifier.sendMail("Model", model);
            return "Admin was notified about missing model: " + model;
        }
        if (producer != null) {
            if (producerRepository.findProducerByName(producer) != null) {
                return "Producer already exists";
            }
            adminNotFoundNotifier.sendMail("Producer", producer);
            return "Admin was notified about missing producer: " + producer;
        }
        return "No arguments";
    }

    public ResponseContainer addImage(int id, MultipartFile file, String username){
        ResponseContainer responseContainer = new ResponseContainer();
        User user;
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(user)){
            log.info("user is empty");
            return responseContainer.setErrorMessageAndStatusCode("user is empty",HttpStatus.BAD_REQUEST.value());
        }
        List<Car> cars = user.getCars();
        ResponseContainer personalCarAndIndex = userService.isPersonalCarAndIndex(cars, id, responseContainer);
        if(personalCarAndIndex.isError()){
            return personalCarAndIndex;
        }

        String extension = file.getOriginalFilename().split("\\.")[1];
        String[] extensions = constants.getExtensions();
        List<String> list = Arrays.stream(extensions).toList();
        if (!list.contains(extension)) {
            responseContainer.setErrorMessageAndStatusCode("Not an image file! Supportive image extensions: "
                            + Arrays.toString(extensions)
                            .replace("[", "").replace("]", ""),HttpStatus.BAD_REQUEST.value());
        }
        Car car;
        try {
          car = this.carRepository.findById(id).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage())
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if (ObjectUtils.isEmpty(car)){
            log.info("car not found");
            return responseContainer.setErrorMessageAndStatusCode("car not found",HttpStatus.BAD_REQUEST.value());
        }
        List<Image> images = car.getImages();
        String title;
        if(images.size()!=0){
            Image lastImage = images.get(images.size() - 1);
            String numberOfPhoto = lastImage.getImageName().split("-")[3].split("\\.")[0];
            title = "car-" + id + "-image-" + (Integer.parseInt(numberOfPhoto)+1) + "." + extension;
        } else{
            title = "car-" + id + "-image-" + 0 + "." + extension;
        }
        String path = System.getProperty("user.home") + File.separator + "adImages" + File.separator;
        images.add(new Image(title));
        try {
            file.transferTo(new File(path + title));
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        car.setImages(images);
        Car savedCar;
        try {
            savedCar = carRepository.save(car);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(userService.isPremiumAccount(username)){
            responseContainer.setSuccessResult(new CarResponse(this.carMapper.toDto(savedCar)));
            return responseContainer;
        }else{
            responseContainer.setSuccessResult(new CarResponse().setCarBasic(this.carMapper.toBasicDto(savedCar)));
            return responseContainer;
        }

    }
    public ResponseContainer deleteImage(Integer id, String filename, String username){
        ResponseContainer responseContainer = new ResponseContainer();
        if(ObjectUtils.isEmpty(id)){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null",HttpStatus.BAD_REQUEST.value());
        }
        if(!StringUtils.hasText(filename)){
            log.info("filename is null");
            return responseContainer.setErrorMessageAndStatusCode("filename is null",HttpStatus.BAD_REQUEST.value());
        }
        if(!StringUtils.hasText(username)){
            log.info("username is null");
            return responseContainer.setErrorMessageAndStatusCode("username is null", HttpStatus.BAD_REQUEST.value());
        }
        User user;
        try {
            user = userRepository.findByUsername(username).orElse(null);
        } catch ( Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(user)){
            log.info("user not found");
            return responseContainer.setErrorMessageAndStatusCode("user not found", HttpStatus.BAD_REQUEST.value());
        }
        List<Car> cars = user.getCars();

        ResponseContainer personalCarAndIndex = userService.isPersonalCarAndIndex(cars, id, responseContainer);
        if(personalCarAndIndex.isError()){
            log.info(personalCarAndIndex.getErrorMessage());
            return personalCarAndIndex;
        }
        Car car;
        try {
            car = carRepository.findById(id).orElse(null);
        }catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(car)){
            log.info("car not found");
            return responseContainer.setErrorMessageAndStatusCode("car not found",HttpStatus.BAD_REQUEST.value());
        }
        List<Image> images = car.getImages();
        images.removeIf(image -> image.getImageName().equals(filename));
        car.setImages(images);
        String path = System.getProperty("user.home") + File.separator + "adImages" + File.separator;
        File f = new File(path+filename);
        if(!f.delete()){
            log.info("Failed to delete image");
            return responseContainer.setErrorMessageAndStatusCode("Failed to delete image",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            carRepository.save(car);
        } catch (Exception e){
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            imageRepository.deleteByImageName(filename);
        } catch (Exception e){
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
        responseContainer.setSuccessResult("Image with filename: "+filename+" was deleted");
        return responseContainer;
    }

    public Integer averageCalculator(List<Integer> integers) {
        Integer summary = 0;
        for (Integer integer : integers) {
            summary += integer;
        }
        return summary / integers.size();
    }

}
