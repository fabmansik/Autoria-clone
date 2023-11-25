package milansomyk.springboothw.dto.response;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.view.Views;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
public class AverageResponse {
    private Integer price;
    private String ccy;
    private Integer amount;

}
