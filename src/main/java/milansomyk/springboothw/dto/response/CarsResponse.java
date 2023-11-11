package milansomyk.springboothw.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.dto.CarDto;

import java.util.List;
@Data
@NoArgsConstructor
public class CarsResponse {
    private List<CarDto> cars;
    private Integer amount;
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
