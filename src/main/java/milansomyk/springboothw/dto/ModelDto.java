package milansomyk.springboothw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import milansomyk.springboothw.entity.Producer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelDto {
    private Integer id;
    private String name;
    private Producer producer;

}
