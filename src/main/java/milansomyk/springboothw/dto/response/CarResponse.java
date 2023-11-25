package milansomyk.springboothw.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.dto.BasicCarDto;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.view.Views;

@Data
@NoArgsConstructor

public class CarResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private CarDto carPremium;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private BasicCarDto carBasic;

    public CarResponse(CarDto car){
        this.carPremium = car;
    }

    public CarResponse setCarBasic(BasicCarDto carBasic) {
        this.carBasic = carBasic;
        return this;
    }
}
