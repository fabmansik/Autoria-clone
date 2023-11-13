package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import milansomyk.springboothw.dto.ModelDto;
import milansomyk.springboothw.entity.Model;
import milansomyk.springboothw.entity.Producer;
import milansomyk.springboothw.mapper.ModelMapper;
import milansomyk.springboothw.mapper.ProducerMapper;
import milansomyk.springboothw.repository.ModelRepository;
import milansomyk.springboothw.repository.ProducerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Data
@Service
public class ModelService {
    public final ModelRepository modelRepository;
    public final ProducerRepository producerRepository;
    private final ModelMapper modelMapper;
    private final ProducerMapper producerMapper;
    public ModelDto addModel(Integer id, ModelDto model){
        Producer producer = producerRepository.findById(id).get();
        List<Model> models = producer.getModels();

        if (models.stream().map(Model::getName).toList().contains(model.getName())){
            return ModelDto.builder().error("This model is already added").build();
        }
        models.add(modelMapper.fromDto(model));
        producer.setModels(models);
        Producer save = producerRepository.save(producer);

        Model addedModel = save.getModels().get(save.getModels().size() - 1);
        addedModel.setProducer(new Producer().setName(save.getName()).setId(save.getId()));
        return modelMapper.toDto(addedModel);
    }
    @Transactional(readOnly = true)
    public List<ModelDto> findAllModels(Integer id){

        Producer producer = producerRepository.findById(id).get();
        List<Model> models = producer.getModels();
        return models.stream().map(modelMapper::toDto).toList();

    }
}
