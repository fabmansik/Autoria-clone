package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.*;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.service.CarService;
import milansomyk.springboothw.service.ModelService;
import milansomyk.springboothw.service.ProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BuyerController {
    private final CarService carService;
    private final ModelService modelService;
    private final ProducerService producerService;
    private final CarTypeDto carTypeDto;
    private final RegionDto regionDto;

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
    public ResponseEntity<CarTypeDto> getAllTypes(){
        return ResponseEntity.ok(carTypeDto);
    }
    @GetMapping("/search/regions")
    public ResponseEntity<RegionDto> getAllRegions(){
        return ResponseEntity.ok(regionDto);
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

}
