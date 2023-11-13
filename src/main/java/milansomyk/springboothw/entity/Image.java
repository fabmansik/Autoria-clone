package milansomyk.springboothw.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Data
@NoArgsConstructor
@Entity
@EnableAutoConfiguration
@ToString
@Table(name = "images", schema = "public")
public class Image {
    @Id
    @GeneratedValue
    private Integer id;
    private String imageName;
    public Image(String imageName){
        this.imageName = imageName;
    }
}
