package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.service.entityServices.CarService;
import milansomyk.springboothw.service.entityServices.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/manage")

public class ManagerController {
    private final UserService userService;
    private final CarService carService;

    @GetMapping("/users")
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/cars/{id}")
    @RolesAllowed({"MANAGER","ADMIN"})
    public CarResponse getCarById(@PathVariable int id){
        return carService.findById(id);
    }
    @PutMapping("/users/ban/{id}")
    @RolesAllowed({"MANAGER", "ADMIN"})
    public UserResponse banUser(@PathVariable int id){
        return userService.banUser(id);
    }
    @PutMapping("/users/unban/{id}")
    @RolesAllowed({"MANAGER","ADMIN"})
    public UserResponse unBanUser(@PathVariable int id){return userService.unBanUser(id);}
    @DeleteMapping("/cars/{id}")
    @RolesAllowed({"MANAGER","ADMIN"})
    public String deleteCarById(@PathVariable int id){
        return carService.deleteById(id);
    }



}
