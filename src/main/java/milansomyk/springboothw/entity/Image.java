package milansomyk.springboothw.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import milansomyk.springboothw.view.Views;
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
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private Integer id;
    @JsonView({Views.LevelSeller.class,Views.LevelBuyer.class,Views.LevelManagerAdmin.class})
    private String imageName;

    public Image(String imageName){
        this.imageName = imageName;
    }
}
