package milansomyk.springboothw.dto;

import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class ImageExtensionsDto {
    String[] extensions = new String[]{"jpg","jpeg","png","svg","webp"};
}
