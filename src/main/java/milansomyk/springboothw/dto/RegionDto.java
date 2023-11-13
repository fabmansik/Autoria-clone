package milansomyk.springboothw.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class RegionDto {
    private String[] regions = new String[]{"Vinnyckiy","Volinskiy","Dnipropetrovskiy",
            "Doneckiy","Zhitomirskiy","Zakarpatskiy","Zaporizkiy","Ivano-Frankivskiy",
    "Kyivskiy","Kirovogradskiy","Luganskiy","Lvivskiy","Mykolaivskiy","Odeskiy","Poltavskiy",
    "Rivnenskiy","Sumskiy","Ternopilskiy","Harkivskiy","Hersonskiy","Hmelnickiy",
    "Cherkaskiy","Cherniveckiy","Chernigivskiy"};
}
