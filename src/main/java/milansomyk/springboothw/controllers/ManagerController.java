package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.CarResponse;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.service.CarService;
import milansomyk.springboothw.service.UsersManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/manage")

public class ManagerController {
    private final UsersManagementService usersManagementService;
    private final CarService carService;

    @GetMapping("/users")
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }
    @PutMapping("/users/ban/{id}")
    @RolesAllowed({"MANAGER", "ADMIN"})
    public UserResponse banUser(@PathVariable int id){
        return usersManagementService.banUser(id);
    }

    @DeleteMapping("/cars/{id}")
    @RolesAllowed({"MANAGER","ADMIN"})
    public String deleteCarById(@PathVariable int id){
        return carService.deleteById(id);
    }

    @GetMapping("/cars/{id}")
    @RolesAllowed({"MANAGER","ADMIN"})
    public CarResponse findCarById(@PathVariable int id){
        return carService.findById(id);
    }


}
