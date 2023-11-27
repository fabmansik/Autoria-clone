package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.entity.Image;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.List;

@Data
@Service
@Slf4j
public class ImageService {
    private final CarRepository carRepository;
    private final ImageRepository imageRepository;

    public ResponseContainer deleteImage(Integer imageId, Integer carId){
        ResponseContainer responseContainer = new ResponseContainer();
        if(ObjectUtils.isEmpty(imageId)){
            log.error("imageId is null");
            return responseContainer.setErrorMessageAndStatusCode("imageId is null",HttpStatus.BAD_REQUEST.value());
        }
        if (ObjectUtils.isEmpty(carId)){
            log.error("carId is null");
            return responseContainer.setErrorMessageAndStatusCode("carId is null",HttpStatus.BAD_REQUEST.value());
        }
        Car car;
        try {
            car = carRepository.findById(carId).orElse(null);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(car)){
            log.error("car not found");
            return responseContainer.setErrorMessageAndStatusCode("car not found",HttpStatus.BAD_REQUEST.value());
        }
        Image image;
        try {
            image = imageRepository.findById(imageId).orElse(null);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(image)){
            log.error("image not found");
            return responseContainer.setErrorMessageAndStatusCode("image not found", HttpStatus.BAD_REQUEST.value());
        }
        String imageName = image.getImageName();
        List<Image> images = car.getImages();
        images.removeIf(img-> img.getId().equals(imageId));
        car.setImages(images);
        String path = System.getProperty("user.home") + File.separator + "adImages" + File.separator;
        File f = new File(path+imageName);
        if(!f.delete()){
            log.error("failed to delete image");
            return responseContainer.setErrorMessageAndStatusCode("failed to delete image",HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            carRepository.save(car);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            imageRepository.deleteById(imageId);
        } catch (Exception e){
            log.error(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        responseContainer.setSuccessResult( "Image with id: "+imageId+" was deleted");
        return responseContainer;
    }
}
