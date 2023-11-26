package milansomyk.springboothw.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import milansomyk.springboothw.dto.consts.Constants;
import milansomyk.springboothw.dto.response.ResponseContainer;
import org.springframework.stereotype.Service;

@Data
@Service
@Slf4j
public class ConstantsService {
    private final Constants constants;
    public ResponseContainer getAllTypes(){
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setSuccessResult(constants.getTypes());
        return responseContainer;
    }
    public ResponseContainer getAllRegions(){
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setSuccessResult(constants.getTypes());
        return responseContainer;
    }
}
