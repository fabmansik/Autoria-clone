package milansomyk.springboothw.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import milansomyk.springboothw.enums.Currency;
import milansomyk.springboothw.view.Views;

import java.util.Date;

@Data
@Builder
public class CarDto {
    private Integer id;

    @NotBlank(message = "model required")
    @Size(min = 2, max = 20, message = "model: min: {min}, max: {max} characters")
    private String model;

    @NotBlank(message = "producer required")
    @Size(min = 2, max = 20, message = "producer: min: {min}, max: {max} characters")
    private String producer;

    @Min(1900)
    @Max(2023)
    private Integer year;
    @Min(value = 50, message = "power: min: {value}")
    private Integer power;
    @NotBlank(message = "type required")
    private String type;
    private String details;
    @NotNull(message = "run km required")
    private Integer runKm;
    @DecimalMax("20.0") @DecimalMin("0.0")
    private Double engineVolume;
    private String color;
    @NotBlank(message = "region required")
    private String region;
    @NotBlank(message = "place required")
    private String place;
    private String transmission;
    private String gearbox;
    @NotNull(message = "price is required")
    private Integer price;
    @NotBlank(message = "currencyName is required")
    private String currencyName;
    private String currencyValue;
    private Integer checkCount;
    private Integer watchesTotal;
    private Integer watchesPerDay;
    private Integer watchesPerWeek;
    private Integer watchesPerMonth;
    private boolean active;
    private Date creationDate;
    private String photo;
    public Integer addCheckCount(){
        this.checkCount++;
        return checkCount;
    }
}
