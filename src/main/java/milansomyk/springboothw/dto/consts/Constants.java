package milansomyk.springboothw.dto.consts;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.URL;

@Data
@Component
public class Constants {
    private String[] types = new String[]{"Suv","Hatchback","Sedan",
            "Coupe","Universal","Crossover","Minivan","Convertible","Sportcar","Van","Pickup",
            "Supercar"};
    private String[] extensions = new String[]{"jpg","jpeg","png","svg","webp"};
    private String[] regions = new String[]{"Vinnyckiy","Volinskiy","Dnipropetrovskiy",
            "Doneckiy","Zhitomirskiy","Zakarpatskiy","Zaporizkiy","Ivano-Frankivskiy",
            "Kyivskiy","Kirovogradskiy","Luganskiy","Lvivskiy","Mykolaivskiy","Odeskiy","Poltavskiy",
            "Rivnenskiy","Sumskiy","Ternopilskiy","Harkivskiy","Hersonskiy","Hmelnickiy",
            "Cherkaskiy","Cherniveckiy","Chernigivskiy"};
    private String[] swears = new String[]{"сук","нах","піда","долбо","єб","пізд","гандо","хуй","бля"};
    private String PrivatApiUrl = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
}
