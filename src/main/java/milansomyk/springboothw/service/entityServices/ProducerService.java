package milansomyk.springboothw.service.entityServices;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.ProducerDto;
import milansomyk.springboothw.dto.response.ResponseContainer;
import milansomyk.springboothw.entity.Producer;
import milansomyk.springboothw.mapper.ProducerMapper;
import milansomyk.springboothw.repository.ProducerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Data
@Slf4j
public class ProducerService {
    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;
    public ResponseContainer addProducer(ProducerDto producerDto){
        ResponseContainer responseContainer = new ResponseContainer();
        if(producerDto.getName().isEmpty()){
            log.info("user is null");
            return responseContainer.setErrorMessageAndStatusCode("user is null", HttpStatus.BAD_REQUEST.value());
        }
        Producer foundProducer;
        try {
            foundProducer = producerRepository.findProducerByName(producerDto.getName()).orElse(null);
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(!ObjectUtils.isEmpty(foundProducer)){
            log.info("producer with this name already exists");
            return responseContainer.setErrorMessageAndStatusCode("producer with this name already exists",HttpStatus.BAD_REQUEST.value());
        }
        responseContainer.setSuccessResult(producerMapper.toDto(producerRepository.save(producerMapper.fromDto(producerDto))));
        return responseContainer;
    }
    public ResponseContainer findAllProducers(){
        ResponseContainer responseContainer = new ResponseContainer();

        List<ProducerDto> list;
        try {
            list = producerRepository.findAll().stream().map(producerMapper::toDto).toList();
        } catch (Exception e){
            log.info(e.getMessage());
            return responseContainer.setErrorMessageAndStatusCode(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        if(CollectionUtils.isEmpty(list)){
            return responseContainer.setResultAndStatusCode(list,HttpStatus.NO_CONTENT.value());
        }
        responseContainer.setSuccessResult(list);
        return responseContainer;
    }
}
