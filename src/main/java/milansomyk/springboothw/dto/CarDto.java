package milansomyk.springboothw.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.*;
import lombok.*;
import milansomyk.springboothw.entity.Image;
import milansomyk.springboothw.view.Views;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarDto {
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private Integer id;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotBlank(message = "model required")
    @Size(min = 2, max = 20, message = "model: min: {min}, max: {max} characters")
    private String model;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotBlank(message = "producer required")
    @Size(min = 2, max = 20, message = "producer: min: {min}, max: {max} characters")
    private String producer;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @Min(1900)
    @Max(2023)
    private Integer year;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @Min(value = 50, message = "power: min: {value}")
    private Integer power;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotBlank(message = "type required")
    private String type;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String details;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotNull(message = "run km required")
    private Integer runKm;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @DecimalMax("20.0") @DecimalMin("0.0")
    private Double engineVolume;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String color;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotBlank(message = "region required")
    private String region;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotBlank(message = "place required")
    private String place;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String transmission;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String gearbox;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotNull(message = "price is required")
    private Integer price;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    @NotBlank(message = "currencyName is required")
    private String currencyName;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String currencyValue;

    @JsonView({Views.LevelManagerAdmin.class})
    private Integer checkCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelManagerAdmin.class})
    private Integer watchesTotal;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelManagerAdmin.class})
    private Integer watchesPerDay;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelManagerAdmin.class})
    private Integer watchesPerWeek;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonView({Views.LevelSeller.class,Views.LevelManagerAdmin.class})
    private Integer watchesPerMonth;

    @JsonView({Views.LevelManagerAdmin.class})
    private boolean active;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private Date creationDate;

    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private List<Image> images;
}
