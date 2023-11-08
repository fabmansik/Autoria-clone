package milansomyk.springboothw.controllers;

import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.service.JwtService;
import milansomyk.springboothw.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/add-car")
    public ResponseEntity<CarsResponse> createCar(@RequestBody CarDto carDto, @RequestHeader("Authorization") String auth ){
        String token = jwtService.extractTokenFromAuth(auth);
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok(userService.createCar(carDto, username));
    }
    @PutMapping("/edit-my-car/{id}")
    public ResponseEntity<CarResponse> editMyCar(@PathVariable int id, @RequestBody CarDto carDto, @RequestHeader("Authorization") String auth){
        String token = jwtService.extractTokenFromAuth(auth);
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok(userService.editMyCar(id, carDto, username));
    }
    @GetMapping("/my-cars")
    public ResponseEntity<CarsResponse> getMyCars(@RequestHeader("Authorization") String auth){
        String token = jwtService.extractTokenFromAuth(auth);
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok(userService.getMyCars(username));
    }

}
