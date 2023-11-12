package milansomyk.springboothw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EnableAutoConfiguration
@Table(name = "producers", schema = "public")
public class Producer {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "producer_models",
            joinColumns = @JoinColumn(name = "producer_id"),
            inverseJoinColumns = @JoinColumn(name = "model_id")
    )
    private List<Model> models;
}
