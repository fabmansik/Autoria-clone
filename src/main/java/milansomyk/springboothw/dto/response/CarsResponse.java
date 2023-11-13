package milansomyk.springboothw.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.dto.BasicCarDto;
import milansomyk.springboothw.dto.CarDto;
import milansomyk.springboothw.view.Views;

import java.util.List;
@Data
@NoArgsConstructor
public class CarsResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private List<CarDto> carsPremium;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private List<BasicCarDto> carsBasic;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private Integer amount;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String error;

    public CarsResponse(List<CarDto> carsPremium) {
        this.carsPremium = carsPremium;
    }

    public CarsResponse setError(String error) {
        this.error = error;
        return this;
    }
    public CarsResponse setAmount(Integer amount){
        this.amount = amount;
        return this;
    }
    public CarsResponse setCarsBasic(List<BasicCarDto> carsBasic){
        this.carsBasic = carsBasic;
        return this;
    }
}
