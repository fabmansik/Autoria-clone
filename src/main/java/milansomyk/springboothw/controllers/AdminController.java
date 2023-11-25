package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.ModelDto;
import milansomyk.springboothw.dto.ProducerDto;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.service.entityServices.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final ModelService modelService;
    private final ProducerService producerService;
    private final ImageService imageService;
    private final CurrencyService currencyService;

    @PostMapping("/managers")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ResponseContainer> createManager(@RequestBody UserDto userDto){
        ResponseContainer responseContainer = userService.createManager(userDto);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @PostMapping("/producer")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ProducerDto> addProducer(@RequestBody ProducerDto producerDto){
        return ResponseEntity.ok(producerService.addProducer(producerDto));
    }
    @PostMapping("/model/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ModelDto> addModel(@RequestBody ModelDto model, @PathVariable Integer id){
        return ResponseEntity.ok(modelService.addModel(id, model));
    }
    @PostMapping("/currency")
    @RolesAllowed("ADMIN")
    public String uploadCurrencies() throws IOException {
        currencyService.uploadCurrencies();
        return "Currency value Updated...";
    }
    @GetMapping("/managers")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<UserDto>> getAllManagers(){
        return ResponseEntity.ok(userService.getAllManagers());
    }
    @PutMapping("/managers/{id}")
    @RolesAllowed("ADMIN")
    public UserDto setManagerById(@PathVariable int id){
        return userService.setManager(id);
    }
    @PutMapping("/premium/{id}")
    @RolesAllowed("ADMIN")
    public String setPremiumById(@PathVariable int id){return userService.setPremium(id);}
    @DeleteMapping("/users/{id}")
    @RolesAllowed("ADMIN")
    public String deleteUserById(@PathVariable int id){
        return userService.deleteUserById(id);
    }
    @DeleteMapping("/images")
    @RolesAllowed("ADMIN")
    public String deleteImageById(@RequestParam Integer carId, @RequestParam Integer imageId){return imageService.deleteImage(imageId, carId);}
}
