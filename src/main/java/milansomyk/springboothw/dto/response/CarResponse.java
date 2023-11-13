package milansomyk.springboothw.dto.response;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.entity.Car;
import milansomyk.springboothw.view.Views;

@Data
@NoArgsConstructor
@JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
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
