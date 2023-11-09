package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.service.UsersManagementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UsersManagementService usersManagementService;
    @PostMapping("/managers")
    @RolesAllowed("ADMIN")
    public UserResponse createManager(@RequestBody UserDto userDto){
        return usersManagementService.createManager(userDto);
    }
    @PutMapping("/managers/{id}")
    @RolesAllowed("ADMIN")
    public UserDto setManagerById(@PathVariable int id){
        return usersManagementService.setManager(id);
    }
    @PutMapping("/premium/{id}")
    @RolesAllowed("ADMIN")
    public String setPremiumById(@PathVariable int id){return usersManagementService.setPremium(id);}
    @DeleteMapping("/users/{id}")
    @RolesAllowed("ADMIN")
    public String deleteById(@PathVariable int id){
        return usersManagementService.deleteUserById(id);
    }
}
