package milansomyk.springboothw.mapper;

import milansomyk.springboothw.dto.ProducerDto;
import milansomyk.springboothw.entity.Producer;
import org.springframework.stereotype.Component;

@Component
public class ProducerMapper {
    public ProducerDto toDto(Producer producer){
        return ProducerDto.builder()
                .id(producer.getId())
                .name(producer.getName())
                .models(producer.getModels())
                .build();
    }
    public Producer fromDto(ProducerDto producerDto){
        return new Producer(producerDto.getId(), producerDto.getName(), producerDto.getModels());
    }
}
