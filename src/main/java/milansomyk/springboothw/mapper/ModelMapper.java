package milansomyk.springboothw.mapper;

import milansomyk.springboothw.dto.ModelDto;
import milansomyk.springboothw.entity.Model;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {
    public ModelDto toDto(Model model){
        return ModelDto.builder()
                .id(model.getId())
                .name(model.getName())
                .producer(model.getProducer())
                .build();
    }
    public Model fromDto(ModelDto modelDto){
        return new Model(modelDto.getId(), modelDto.getName(), modelDto.getProducer());
    }
}
