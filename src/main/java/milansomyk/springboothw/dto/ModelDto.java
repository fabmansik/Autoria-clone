package milansomyk.springboothw.dto;

import lombok.Builder;
import lombok.Data;
import milansomyk.springboothw.entity.Producer;

@Data
@Builder
public class ModelDto {
    private Integer id;
    private String name;
    private Producer producer;
    private String error;
}
