package milansomyk.springboothw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarSearchParametersDto {
    private String producer;
    private String model;
    private String region;
    private Integer minPrice;
    private Integer maxPrice;
}
