package milansomyk.springboothw.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.*;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.CarsResponse;
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
    public ResponseEntity<UserResponse> create(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.register(userDto));
    }
    @PostMapping("/views/{id}")
    public String addView(@PathVariable int id){
        carService.addWatchesTotal(id);
        return "Car with id: "+id+" watched";
    }
    @PostMapping("/search/notify-not-found")
    public String notifyNotFound(@RequestParam(required = false) String model, @RequestParam(required = false) String producer){
        return carService.notifyNotFound( model, producer);
    }

    @JsonView(Views.LevelBuyer.class)
    @GetMapping("/search")
    public ResponseEntity<CarsResponse> search(
            @RequestParam(required = false) String producer, @RequestParam(required = false) String model,
            @RequestParam(required = false) String region, @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice, @RequestParam(required = false) String ccy,
            @RequestParam(required = false) String type
    ){
        return ResponseEntity.ok(carService.getCars(producer, model, region, minPrice, maxPrice, ccy, type));
    }
    @GetMapping("/search/producers")
    public ResponseEntity<List<ProducerDto>> getAllProducers(){
        return ResponseEntity.ok(producerService.findAllProducers());
    }
    @GetMapping("/search/models/{id}")
    public ResponseEntity<List<ModelDto>> getAllModels(@PathVariable Integer id){
        return ResponseEntity.ok(modelService.findAllModels(id));
    }
    @GetMapping("/search/types")
    public ResponseEntity<String[]> getAllTypes(){
        return ResponseEntity.ok(constants.getTypes());
    }
    @GetMapping("/search/regions")
    public ResponseEntity<String[]> getAllRegions(){
        return ResponseEntity.ok(constants.getRegions());
    }
}
