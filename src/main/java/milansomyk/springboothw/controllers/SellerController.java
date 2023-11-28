package milansomyk.springboothw.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.service.JwtService;
import milansomyk.springboothw.service.entityServices.CarService;
import milansomyk.springboothw.service.entityServices.UserService;
import milansomyk.springboothw.view.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/cars")
public class SellerController {
    private final UserService userService;
    private final CarService carService;
    private final JwtService jwtService;
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @JsonView(Views.LevelSeller.class)
    @PostMapping
    public ResponseEntity<ResponseContainer> createCar(@RequestBody @Valid CarDto carDto, @RequestHeader("Authorization") String auth){
        String username = extractUsernameFromAuth(auth);
        ResponseContainer responseContainer = userService.createCar(carDto, username);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @JsonView(Views.LevelSeller.class)
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @GetMapping
    public ResponseEntity<ResponseContainer> getMyCars(@RequestHeader("Authorization") String auth){
        String username=extractUsernameFromAuth(auth);
        ResponseContainer responseContainer = userService.getMyCars(username);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @JsonView(Views.LevelSeller.class)
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @GetMapping("/average-price")
    public ResponseEntity<ResponseContainer> findAveragePrice(@RequestHeader("Authorization") String auth,
                                                            @RequestParam String producer, @RequestParam String model,
                                                            @RequestParam(required = false) String ccy,
                                                            @RequestParam(required = false) String region){
        String username=extractUsernameFromAuth(auth);
        ResponseContainer responseContainer = carService.findAveragePrice(producer, model, ccy, region, username);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @JsonView(Views.LevelSeller.class)
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @PutMapping
    public ResponseEntity<ResponseContainer> editMyCar(@RequestParam int id, @RequestBody @Valid CarDto carDto, @RequestHeader("Authorization") String auth){
        String username=extractUsernameFromAuth(auth);
        ResponseContainer responseContainer = userService.editMyCar(id, carDto, username);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @JsonView(Views.LevelSeller.class)
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @PutMapping("/img")
    public ResponseEntity<ResponseContainer> addImage(@RequestParam Integer id,  @RequestParam MultipartFile image, @RequestHeader("Authorization") String auth){
        String username=extractUsernameFromAuth(auth);
        ResponseContainer responseContainer = carService.addImage(id, image, username);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @DeleteMapping
    public ResponseEntity<ResponseContainer> deleteMyCarById(@RequestParam int id, @RequestHeader("Authorization") String auth){
        String username=extractUsernameFromAuth(auth);
        ResponseContainer responseContainer = userService.deleteMyCar(id, username);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @RolesAllowed({"SELLER","ADMIN","MANAGER"})
    @DeleteMapping("/img")
    public ResponseEntity<ResponseContainer> deleteImage(@RequestParam Integer id, @RequestParam String fileName, @RequestHeader("Authorization") String auth){
        String username=extractUsernameFromAuth(auth);
        ResponseContainer responseContainer = carService.deleteImage(id, fileName, username);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    public String extractUsernameFromAuth(String auth){
        String token = jwtService.extractTokenFromAuth(auth);
        return jwtService.extractUsername(token);
    }

}
