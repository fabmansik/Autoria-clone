package milansomyk.springboothw.controllers;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import milansomyk.springboothw.dto.ModelDto;
import milansomyk.springboothw.dto.ProducerDto;
import milansomyk.springboothw.dto.UserDto;
import milansomyk.springboothw.dto.response.UserResponse;
import milansomyk.springboothw.entity.Model;
import milansomyk.springboothw.entity.Producer;
import milansomyk.springboothw.repository.ModelRepository;
import milansomyk.springboothw.repository.ProducerRepository;
import milansomyk.springboothw.service.ModelService;
import milansomyk.springboothw.service.ProducerService;
import milansomyk.springboothw.service.UsersManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UsersManagementService usersManagementService;
    private final ModelService modelService;
    private final ProducerService producerService;
    @PostMapping("/managers")
    @RolesAllowed("ADMIN")
    public UserResponse createManager(@RequestBody UserDto userDto){
        return usersManagementService.createManager(userDto);
    }
    @PostMapping("/add-producer")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ProducerDto> addProducer(@RequestBody ProducerDto producerDto){
        return ResponseEntity.ok(producerService.addProducer(producerDto));
    }


    @PostMapping("/add-model/{id}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<ModelDto> addModel(@RequestBody ModelDto model, @PathVariable Integer id){
        return ResponseEntity.ok(modelService.addModel(id, model));
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
