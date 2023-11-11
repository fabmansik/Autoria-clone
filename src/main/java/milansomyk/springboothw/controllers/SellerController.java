package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
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
@RequestMapping("/user/cars")
public class SellerController {
    private final UserService userService;
    private final JwtService jwtService;
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@RequestBody @Valid CarDto carDto, @RequestHeader("Authorization") String auth ){
        String token = jwtService.extractTokenFromAuth(auth);
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok(userService.createCar(carDto, username));
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> editMyCar(@PathVariable int id, @RequestBody @Valid CarDto carDto, @RequestHeader("Authorization") String auth){
        String token = jwtService.extractTokenFromAuth(auth);
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok(userService.editMyCar(id, carDto, username));
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @GetMapping
    public ResponseEntity<CarsResponse> getMyCars(@RequestHeader("Authorization") String auth){
        String token = jwtService.extractTokenFromAuth(auth);
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok(userService.getMyCars(username));
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @DeleteMapping("/{id}")
    public String deleteMyCarById(@PathVariable int id, @RequestHeader("Authorization") String auth){
        String token = jwtService.extractTokenFromAuth(auth);
        String username = jwtService.extractUsername(token);
        return userService.deleteMyCar(id, username);
    }


}
