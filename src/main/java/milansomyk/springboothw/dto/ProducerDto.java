package milansomyk.springboothw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.entity.Model;

import java.util.List;

@Data
@Builder
public class ProducerDto {
    private Integer id;
    @NotBlank(message = "name is required")
    private String name;
    private List<Model> models;
    private String error;

    public ProducerDto setError(String error) {
        this.error = error;
        return this;
    }
}
