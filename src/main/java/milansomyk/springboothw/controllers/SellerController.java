package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.response.AverageResponse;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.service.CarService;
import milansomyk.springboothw.service.JwtService;
import milansomyk.springboothw.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/cars")
public class SellerController {
    private final UserService userService;
    private final CarService carService;
    private final JwtService jwtService;
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@RequestBody @Valid CarDto carDto, @RequestHeader("Authorization") String auth){
        String username = extractUsernameFromAuth(auth);
        return ResponseEntity.ok(userService.createCar(carDto, username));
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @PutMapping("/add-img")
    public ResponseEntity<CarResponse> addImage(@RequestParam Integer id,  @RequestParam MultipartFile image, @RequestHeader("Authorization") String auth) throws IOException {
        String username = extractUsernameFromAuth(auth);
        return ResponseEntity.ok(carService.addImage(id, image, username));
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @DeleteMapping("/delete-img")
    public String deleteImage(@RequestParam Integer id, @RequestParam String fileName, @RequestHeader("Authorization") String auth){
        String username = extractUsernameFromAuth(auth);
        return carService.deleteImage(id, fileName, username);
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> editMyCar(@PathVariable int id, @RequestBody @Valid CarDto carDto, @RequestHeader("Authorization") String auth){
        String username = extractUsernameFromAuth(auth);
        return ResponseEntity.ok(userService.editMyCar(id, carDto, username));
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @GetMapping
    public ResponseEntity<CarsResponse> getMyCars(@RequestHeader("Authorization") String auth){
        String username = extractUsernameFromAuth(auth);
        return ResponseEntity.ok(userService.getMyCars(username));
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @DeleteMapping("/{id}")
    public String deleteMyCarById(@PathVariable int id, @RequestHeader("Authorization") String auth){
        String username = extractUsernameFromAuth(auth);
        return userService.deleteMyCar(id, username);
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @GetMapping("/average-price")
    public ResponseEntity<AverageResponse> findAveragePrice(@RequestHeader("Authorization") String auth,
                                                            @RequestParam String producer, @RequestParam String model,
                                                            @RequestParam(required = false) String ccy,
                                                            @RequestParam(required = false) String region){
        String username = extractUsernameFromAuth(auth);
        return ResponseEntity.ok(carService.findAveragePrice(producer, model, ccy, region, username));
    }
    public String extractUsernameFromAuth(String auth){
        String token = jwtService.extractTokenFromAuth(auth);
        return jwtService.extractUsername(token);
    }

}
