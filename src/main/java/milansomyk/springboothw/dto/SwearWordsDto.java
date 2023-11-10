package milansomyk.springboothw.dto;

import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class SwearWordsDto {
    String[] swears = new String[]{"сук","нах","піда","долбо","єб","пізд","гандо","хуй","бля"};

}
