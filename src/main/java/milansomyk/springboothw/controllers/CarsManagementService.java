package milansomyk.springboothw.controllers;

import lombok.Data;
import milansomyk.springboothw.mapper.UserMapper;
import milansomyk.springboothw.repository.CarRepository;
import milansomyk.springboothw.repository.UserRepository;
import org.springframework.stereotype.Service;

@Data
@Service
public class CarsManagementService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final UserMapper userMapper;


}
