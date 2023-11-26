package milansomyk.springboothw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import milansomyk.springboothw.entity.Model;
import milansomyk.springboothw.entity.Producer;

@Data
@Builder
@AllArgsConstructor
public class ModelDto {
    private Integer id;
    private String name;
    private Producer producer;

    public ModelDto(){}
}
