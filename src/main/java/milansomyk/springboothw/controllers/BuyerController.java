package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.CarSearchParametersDto;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class BuyerController {
    private final CarService carService;

    @GetMapping
    public ResponseEntity<CarsResponse> searchCarWithParameters(
            @RequestParam(required = false) String producer, @RequestParam(required = false) String model,
            @RequestParam(required = false) String region, @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice, @RequestParam(required = false) String ccy
    ){
        return ResponseEntity.ok(carService.getSpecifiedCar(producer, model, region, minPrice, maxPrice, ccy));
    }
}
