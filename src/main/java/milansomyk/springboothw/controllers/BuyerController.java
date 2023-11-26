package milansomyk.springboothw.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.*;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.service.entityServices.CarService;
import milansomyk.springboothw.service.entityServices.ModelService;
import milansomyk.springboothw.service.entityServices.ProducerService;
import milansomyk.springboothw.service.entityServices.UserService;
import milansomyk.springboothw.view.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping

public class BuyerController {
    private final CarService carService;
    private final UserService userService;
    private final ModelService modelService;
    private final ProducerService producerService;
    private final Constants constants;
    @PostMapping("/register")
    public ResponseEntity<ResponseContainer> create(@RequestBody UserDto userDto){
        ResponseContainer responseContainer = userService.register(userDto);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @PostMapping("/views/{id}")
    public ResponseEntity<ResponseContainer> addView(@PathVariable int id){
        ResponseContainer responseContainer = carService.addWatchesTotal(id);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);

    }
    @PostMapping("/search/notify-not-found")
    public ResponseEntity<ResponseContainer> notifyNotFound(@RequestParam(required = false) String model, @RequestParam(required = false) String producer){
        ResponseContainer responseContainer = carService.notifyNotFound(model, producer);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }

    @JsonView(Views.LevelBuyer.class)
    @GetMapping("/search")
    public ResponseEntity<ResponseContainer> search(
            @RequestParam(required = false) String producer, @RequestParam(required = false) String model,
            @RequestParam(required = false) String region, @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice, @RequestParam(required = false) String ccy,
            @RequestParam(required = false) String type
    ){
        return ResponseEntity.ok(carService.getCars(producer, model, region, minPrice, maxPrice, ccy, type));
    }
    @GetMapping("/search/producers")
    public ResponseEntity<ResponseContainer> getAllProducers(){
        return ResponseEntity.ok(producerService.findAllProducers());
    }
    @GetMapping("/search/models/{id}")
    public ResponseEntity<ResponseContainer> getAllModels(@PathVariable Integer id){
        return ResponseEntity.ok(modelService.findAllModels(id));
    }
    @GetMapping("/search/types")
    public ResponseEntity<ResponseContainer> getAllTypes(){
        return ResponseEntity.ok(constants.getTypes());
    }
    @GetMapping("/search/regions")
    public ResponseEntity<ResponseContainer> getAllRegions(){
        return ResponseEntity.ok(constants.getRegions());
    }
}
