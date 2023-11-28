package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.service.entityServices.CarService;
import milansomyk.springboothw.service.entityServices.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/manage")

public class ManagerController {
    private final UserService userService;
    private final CarService carService;

    @GetMapping("/users")
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<ResponseContainer> getAllUsers(){
        ResponseContainer responseContainer = userService.getAllUsers();
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @GetMapping("/cars")
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<ResponseContainer> getCarById(@RequestParam int id){
        ResponseContainer responseContainer = carService.findById(id);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @PutMapping("/users/ban")
    @RolesAllowed({"MANAGER", "ADMIN"})
    public ResponseEntity<ResponseContainer> banUser(@RequestParam int id){
        ResponseContainer responseContainer = userService.banUser(id);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @PutMapping("/users/unban")
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<ResponseContainer> unBanUser(@RequestParam int id){
        ResponseContainer responseContainer = userService.unBanUser(id);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }
    @DeleteMapping("/cars")
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<ResponseContainer> deleteCarById(@RequestParam int id){
        ResponseContainer responseContainer = carService.deleteById(id);
        return ResponseEntity.status(responseContainer.getStatusCode()).body(responseContainer);
    }



}
