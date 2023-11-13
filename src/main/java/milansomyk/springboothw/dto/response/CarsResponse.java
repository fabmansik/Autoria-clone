package milansomyk.springboothw.dto.response;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.view.Views;

import java.util.List;
@Data
@NoArgsConstructor
public class CarsResponse {
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private List<CarDto> cars;
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private Integer amount;
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String error;

    public CarsResponse(List<CarDto> cars) {
        this.cars = cars;
    }

    public CarsResponse setError(String error) {
        this.error = error;
        return this;
    }
    public CarsResponse setAmount(Integer amount){
        this.amount = amount;
        return this;
    }
}
