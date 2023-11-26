package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.ModelDto;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.Model;
import milansomyk.springboothw.entity.Producer;
import milansomyk.springboothw.mapper.ModelMapper;
import milansomyk.springboothw.mapper.ProducerMapper;
import milansomyk.springboothw.repository.ModelRepository;
import milansomyk.springboothw.repository.ProducerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Data
@Service
@Slf4j
public class ModelService {
    public final ModelRepository modelRepository;
    public final ProducerRepository producerRepository;
    private final ModelMapper modelMapper;
    private final ProducerMapper producerMapper;
    public ResponseContainer addModel(Integer id, ModelDto model){
        ResponseContainer responseContainer = new ResponseContainer();
        if(ObjectUtils.isEmpty(id)){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null",HttpStatus.BAD_REQUEST.value());
        }
        if(ObjectUtils.isEmpty(model)){
            log.info("model is null");
            return responseContainer.setErrorMessageAndStatusCode("model is null",HttpStatus.BAD_REQUEST.value());
        }
        Producer producer;
        try {
            producer = producerRepository.findById(id).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(producer)){
            log.info("producer not found");
            return responseContainer.setErrorMessageAndStatusCode("producer not found",HttpStatus.BAD_REQUEST.value());
        }
        List<Model> models = producer.getModels();
        if (models.stream().map(Model::getName).toList().contains(model.getName())){
            return responseContainer.setErrorMessageAndStatusCode("This model is already added",HttpStatus.BAD_REQUEST.value());
        }
        models.add(modelMapper.fromDto(model));
        producer.setModels(models);
        Producer save;
        try {
            save = producerRepository.save(producer);
        } catch (Exception e){
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        Model addedModel = save.getModels().get(save.getModels().size() - 1);
        addedModel.setProducer(new Producer().setName(save.getName()).setId(save.getId()));
        responseContainer.setSuccessResult(modelMapper.toDto(addedModel));
        return responseContainer;
    }
    @Transactional(readOnly = true)
    public ResponseContainer findAllModels(Integer id){
        Producer producer;
        ResponseContainer responseContainer = new ResponseContainer();
        if(ObjectUtils.isEmpty(id)){
            log.info("id is null");
            return responseContainer.setErrorMessageAndStatusCode("id is null", HttpStatus.BAD_REQUEST.value());
        }
        try {
            producer = producerRepository.findById(id).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(ObjectUtils.isEmpty(producer)){
            return responseContainer.setResultAndStatusCode("models not found",HttpStatus.NO_CONTENT.value());
        }
        List<Model> models = producer.getModels();
        responseContainer.setSuccessResult(models.stream().map(modelMapper::toDto).toList());
        return responseContainer;

    }
}
