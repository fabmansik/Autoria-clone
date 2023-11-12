package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.CarSearchParametersDto;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BuyerController {
    private final CarService carService;

    @GetMapping("/search")
    public ResponseEntity<CarsResponse> searchCarWithParameters(
            @RequestParam(required = false) String producer, @RequestParam(required = false) String model,
            @RequestParam(required = false) String region, @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice, @RequestParam(required = false) String ccy
    ){
        return ResponseEntity.ok(carService.getSpecifiedCar(producer, model, region, minPrice, maxPrice, ccy));
    }
    @PostMapping("/views/{id}")
    public String addView(@PathVariable int id){
        carService.addWatchesTotal(id);
        return "Car with id: "+id+" watched";
    }
//    @PostMapping("/filter/producers")
//    public ResponseEntity<List<String>> getProducers(){
//        return ResponseEntity.ok(carService.get)
//    }
}
