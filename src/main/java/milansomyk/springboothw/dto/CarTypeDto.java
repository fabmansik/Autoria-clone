package milansomyk.springboothw.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CarTypeDto {
    private String[] types = new String[]{"Suv","Hatchback","Sedan",
            "Coupe","Universal","Crossover","Minivan","Convertible","Sportcar","Van","Pickup",
    "Supercar"};
}
