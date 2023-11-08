package milansomyk.springboothw.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.entity.Car;

@Data
@NoArgsConstructor
public class CarResponse {
    private CarDto car;
    private String error;

    public CarResponse(CarDto car){
        this.car = car;
    }

    public CarResponse setError(String error) {
        this.error = error;
        return this;
    }
}
