package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.ModelDto;
import milansomyk.springboothw.entity.Model;
import milansomyk.springboothw.mapper.ModelMapper;
import milansomyk.springboothw.repository.ModelRepository;
import org.springframework.stereotype.Service;

@Data
@Service
public class ModelService {
    public final ModelRepository modelRepository;
    private final ModelMapper modelMapper;
    public ModelDto addModel(ModelDto model){
        return modelMapper.toDto(modelRepository.save(modelMapper.fromDto(model)));
    }
}
