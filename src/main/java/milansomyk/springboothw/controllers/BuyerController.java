package milansomyk.springboothw.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.*;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.service.ConstantsService;
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
    private final ConstantsService constantsService;
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
        ResponseContainer responseContainer = carService.getCars(producer, model, region, minPrice, maxPrice, ccy, type);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @GetMapping("/search/producers")
    public ResponseEntity<ResponseContainer> getAllProducers(){
        ResponseContainer responseContainer = producerService.findAllProducers();
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @GetMapping("/search/models/{id}")
    public ResponseEntity<ResponseContainer> getAllModels(@PathVariable Integer id){
        ResponseContainer responseContainer = modelService.findAllModels(id);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @GetMapping("/search/types")
    public ResponseEntity<ResponseContainer> getAllTypes(){
        ResponseContainer responseContainer = constantsService.getAllTypes();
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @GetMapping("/search/regions")
    public ResponseEntity<ResponseContainer> getAllRegions(){
        ResponseContainer responseContainer = constantsService.getAllRegions();
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
}
