package milansomyk.springboothw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProducerDto {
    private Integer id;
    @NotBlank(message = "name is required")
    private String name;

}
