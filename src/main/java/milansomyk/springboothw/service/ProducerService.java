package milansomyk.springboothw.service;

import lombok.Data;
import milansomyk.springboothw.dto.ProducerDto;
import milansomyk.springboothw.entity.Producer;
import milansomyk.springboothw.mapper.ProducerMapper;
import milansomyk.springboothw.repository.ProducerRepository;
import org.springframework.stereotype.Service;

@Service
@Data
public class ProducerService {
    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;
    public ProducerDto addProducer(ProducerDto producerDto){
        System.out.println(producerDto);
        Producer foundProducer = producerRepository.findProducerByName(producerDto.getName());
        System.out.println(foundProducer);
        if(foundProducer!=null){
            return ProducerDto.builder().error("Producer with this name already exists").build();
        }

        return producerMapper.toDto(producerRepository.save(producerMapper.fromDto(producerDto)));
    }
}
