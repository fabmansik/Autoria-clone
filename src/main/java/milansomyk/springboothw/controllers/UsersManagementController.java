package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.CarsResponse;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.service.UserService;
import milansomyk.springboothw.service.UsersManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/manage")
public class UsersManagementController {
    private final UsersManagementService usersManagementService;
    @GetMapping("/users")
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/cars")
    @RolesAllowed("{MANAGER, ADMIN}")
    public ResponseEntity<CarsResponse> getAllCars(){
        return ResponseEntity.ok(usersManagementService.getAllCars());
    }
    @PostMapping("/users/ban")
    @RolesAllowed("{MANAGER, ADMIN}")
    public UserResponse banUser(@RequestParam int id){
        return usersManagementService.banUser(id);
    }
    @DeleteMapping("/users/delete")
    @RolesAllowed("{MANAGER, ADMIN}")
    public String deleteCarById(@RequestParam int id){
        return usersManagementService.deleteById(id);
    }
    @GetMapping("/cars")

    @DeleteMapping
    @RolesAllowed("ADMIN")
    public void deleteById(int id){
        usersManagementService.deleteUserById(id);
    }
}
