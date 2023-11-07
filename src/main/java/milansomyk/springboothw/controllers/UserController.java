package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.entity.User;
import milansomyk.springboothw.exceptions.UserAlreadyExistAuthenticationException;
import milansomyk.springboothw.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    @GetMapping
    @RolesAllowed({"MANAGER","ADMIN"})
    public ResponseEntity<List<UserDto>> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.create(userDto));
    }
    @PutMapping

    @DeleteMapping
    public void deleteById(int id){
        userService.deleteById(id);
    }
}
